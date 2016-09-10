(ns joke-service.core
  (:require [joke-service.joke :as joke]
            [utils.cli :refer :all]
            [utils.file :refer :all]
            [clj-time.core :as dt]
            [overtone.at-at :as at-at]
            [clojure.tools.logging :as log])
  (:import [java.util TimeZone])
  (:gen-class))

(defn- env [key] (System/getenv key))

(defoperation :process [args]
  (log/info (str "Sync start at:" (dt/now)))
  (joke/start))

(defoperation :log [args]
  (log/info (str "Sync end at:" (dt/now))))

(defoperation :daemon [args]
  (def my-pool (at-at/mk-pool))
  (defn daemon [args]
    (try
      (doseq [x [:process :log]] (operation x args))
      (catch Exception e (do
                           (println (str "Failure: " e))
                           (.printStackTrace e)))))

;  1 hour 1 * 60 * 60 * 1000
  (at-at/every 3600000 #(daemon args) my-pool))

(defmethod operation :default [_ args]
  (println "Please choose an operation"))

(defn -main
  [& args]
  (TimeZone/setDefault (TimeZone/getTimeZone "CST"))
  (let [requested-operation (first args)]
    (operation (keyword requested-operation) (rest args))))
