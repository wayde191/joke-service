(ns joke-service.joke
  (:require
            [clojure.data.json :as json]
            [clojure.string :as string]
            [utils.file :refer :all]
            [utils.http :as middleware]
            [utils.date :as date-utils]
            [clj-time.core :as time]
            [clj-time.coerce :as coerce]
            [environ.core  :refer [env]]
            [joke-service.config :as config]
            [clojure.tools.logging :as log]
            [pl.danieljanus.tagsoup :as html-tag]
            [hickory.core :as html-parser]
            [hickory.select :as html-selecter]
            [joke-service.mysql :as mysql]
            [digest :as digest]))

(defn insert-detail-info [doc-id joke-detail]
  (let [content (:content joke-detail)
        first-ele (first content)
        last-ele (last content)
        img (if (nil? (:content first-ele))
              (get-in first-ele [:attrs :src])
              (get-in (first (:content first-ele)) [:attrs :src]))
        span (if (nil? (:content last-ele)) ""
               (first (:content last-ele)))]
    (log/info (str "insert: " doc-id ":" img ":" span))
    (try
      (mysql/insert-joke-detail {:img img :span span :docID doc-id})
      (catch  Exception e
        (log/error (str "caught exception: " (.getMessage e) " with insert-detail-info"))))
    ))

(defn get-joke-detail [joke-detail-url]
  (try
    (let [html-str (middleware/http-atom {:url joke-detail-url})
           parsed-doc (html-parser/as-hickory (html-parser/parse html-str))]
      (html-selecter/select
        (html-selecter/descendant
          (html-selecter/class "img_wrapper"))
        parsed-doc))
    (catch Exception e
      (println e)
      (log/error (str "caught exception: " (.getMessage e) " with get-joke-detail")))))

(defn insert-joke [joke create-date]
  (let [doc-id (:docID joke)
         log-info (str "insert-joke " doc-id " : " create-date)]
    (try
      (if (nil? (mysql/get-joke doc-id))
        (do
          (mysql/insert-joke joke create-date)
          (->>
            (get-joke-detail (:link joke))
            (map #(insert-detail-info doc-id %))
            (count))
          (log/info log-info))
        (log/info "exist! " log-info))
      (catch  Exception e
        (log/error (str "caught exception: " (.getMessage e) " with insert-joke"))))
    ))

(defn process-latest-joke []
  (try
    (let [html-str (middleware/http-atom {:url (config/get-joke-url 1 (coerce/to-long (time/now)))})
          trim-str (string/trim
                     (->
                       (string/replace html-str #"callbackFunction\(" "")
                       (string/replace #"\);" "")
                       ))
          jokes (json/read-str trim-str :key-fn keyword)
          data (:data jokes)
          normal-data (filter #(= (:mediaTypes %) "normal") data)
          create-date (date-utils/unparse-date "YYYY-MM-dd" (time/now))
          ]
      (persist-as-json trim-str (str "/tmp/jokes-" create-date ".json"))
      (count (map #(insert-joke % create-date) normal-data)))
    (catch Exception e
      (println e)
      (log/error (str "caught exception: " (.getMessage e) " with process-latest-joke")))))

(defn start []
  (log/info "Starting the joke service ... ")
  (process-latest-joke)
  )