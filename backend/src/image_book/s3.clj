(ns image-book.s3
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clj-time.core :as t]
            [amazonica.aws.s3 :as aws-s3]))

(declare CONFIG-FILE)
(defonce CONFIG-FILE "config.edn")

(def config
  (-> CONFIG-FILE
      io/resource
      slurp
      edn/read-string))

(def cred {:endpoint (:aws-region config)
           :access-key (:aws-access-key config)
           :secret-key (:aws-secret-key config)})

(defn upload-photo [image-file]
  (let [{:keys [aws-bucket]} config
        {:keys [tempfile filename size content-type]} image-file
        key (str "photos/" (java.util.UUID/randomUUID) "/" filename)
        s3-data (aws-s3/put-object cred :bucket-name    aws-bucket
                                        :key            key
                                        :file           tempfile
                                        :content-length size
                                        :content-type   content-type)]
    {:etag (:etag s3-data)
     :key  key
     :bucket aws-bucket
     :title filename
     :timestamp (t/now)}))
