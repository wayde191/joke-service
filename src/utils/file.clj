(ns utils.file
  (:require [utils.json :as json]
            [me.raynes.fs :as fs]
            [clj-http.client :as client]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]))

(defn persist-as-json [contents location]
  (fs/mkdirs (fs/parent location))
  (json/to-file location contents)
  location)

(defn persist-resource [contents path & paths]
  (let [target (apply fs/file path paths)]
    (println "Creating resource " (.getName target) " at " (.getParent target))
    (persist-as-json contents target)))

(defn file-exists? [path]
  (if (fs/exists? path)
    "yes"
    "no"))

(defn delete-directory [path]
  (if (fs/exists? path)
    (fs/delete-dir path)))

(defn read-file [file-name]
  (json/read-json-file file-name))

(defn fetch-photo
  "makes an HTTP request and fetches the binary object"
  [url]
  (let [req (client/get url {:as :byte-array :throw-exceptions false})]
    (if (= (:status req) 200)
      (:body req))))

(defn save-photo
  "downloads and stores the photo on disk"
  [photo path & paths]
  (let [target (apply fs/file path paths)
        p (fetch-photo photo)]
    (fs/mkdirs (fs/parent target))
    (log/info "Creating resource " (.getName target) " at " (.getParent target))
    (if (not (nil? p))
      (with-open [w (io/output-stream target)]
        (.write w p)))))