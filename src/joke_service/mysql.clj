(ns joke-service.mysql
  (:require [clojure.java.jdbc :as sql]))

(def db {:classname "com.mysql.jdbc.Driver"
         :subprotocol "mysql"
         :subname "//localhost:3306/ihakula_joke?characterEncoding=UTF-8"
         :user "root"
         :password "Wayde191!"})

(defn get-fund-company-by-code [code]
  (sql/with-connection db
    (sql/with-query-results rows [(str "select * from fund_company where company_code = '" code "'")]
      (doall rows))))

(defn insert-fund-company [company-code name search-field]
  (sql/with-connection db
    (sql/insert-records :fund_company
      {:company_code company-code
       :name name
       :search_field search-field})))

(defn update-fund-company [company-code name search-field]
  (sql/with-connection db
    (sql/update-values :fund_company
      ["company_code=?" company-code]
      {:company_code company-code
       :name name
       :search_field search-field})))

(defn insert-resource-md5-with-name [md5 name]
  (sql/with-connection db
    (sql/insert-records :resource
      {:name name
       :md5 md5})))

(defn get-fund-by-code [code]
  (sql/with-connection db
    (sql/with-query-results rows [(str "select * from fund where code = '" code "'")]
      (doall rows))))

(defn get-all-funds []
  (sql/with-connection db
    (sql/with-query-results rows ["select * from fund"]
      (doall rows))))

(defn insert-fund [code short-name name type]
  (sql/with-connection db
    (sql/insert-records :fund
      {:code code
       :short_name short-name
       :name name
       :type type})))

(defn update-fund [code short-name name type]
  (sql/with-connection db
    (sql/update-values :fund
      ["code=?" code]
      {:code code
       :short_name short-name
       :name name
       :type type})))

; Start here
(defn get-joke [doc-id]
  (sql/with-connection db
    (sql/with-query-results rows [(str "select * from joke where doc_id = '" doc-id "'")]
      (doall rows))))

(defn insert-joke [joke create-date]
  (sql/with-connection db
    (sql/insert-records :joke
      {:title (:title joke)
       :wap_title (:wap_title joke)
       :img (:img joke)
       :link (:link joke)
       :intro (:intro joke)
       :doc_id (:docID joke)
       :date (:date joke)
       :create_date create-date
       })))

(defn insert-joke-detail [joke-detail]
  (sql/with-connection db
    (sql/insert-records :joke_content
      {:doc_id (:docID joke-detail)
       :img (:img joke-detail)
       :intro (:span joke-detail)
       })))

(defn get-joke-detail []
  (sql/with-connection db
    (sql/with-query-results rows [(str "select * from joke_content")]
      (doall rows))))

(defn get-resource-history-number []
  (let [record (sql/with-connection db
              (sql/with-query-results rows [(str "select counter from resource where name = 'history_page'")]
                (doall rows)))]
    (if (nil? record)
      nil
      (:counter (first record)))))

(defn update-resource-by-name [content name]
  (sql/with-connection db
    (sql/update-values :resource
      ["name=?" name]
      {:counter content})))

(defn get-resource-refresh-switch []
  (let [record (sql/with-connection db
                 (sql/with-query-results rows [(str "select counter from resource where name = 'refresh_images'")]
                   (doall rows)))]
    (if (nil? record)
      nil
      (:counter (first record)))))