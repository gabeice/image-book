(ns image-book.s3
  (:require [image-book.config :refer [s3-config]]
            [amazonica.aws.s3 :as aws-s3]))

(def cred {:endpoint (:aws-region s3-config)
           :access-key (:aws-access-key s3-config)
           :secret-key (:aws-secret-key s3-config)})

(defn- random-triplet []
  (str (.nextInt (java.util.Random.) 10)
       (.nextInt (java.util.Random.) 10)
       (.nextInt (java.util.Random.) 10)))

(defn- build-key [filename]
  (str "photos/" (random-triplet) "/" (random-triplet) "/" (random-triplet) "/" filename))

(defn upload-photo [image-file]
  (let [{:keys [aws-bucket]} s3-config
        {:keys [tempfile filename size content-type]} image-file
        key (build-key filename)
        s3-data (aws-s3/put-object cred :bucket-name    aws-bucket
                                        :key            key
                                        :file           tempfile
                                        :content-length size
                                        :content-type   content-type)]
    {:etag (:etag s3-data)
     :key  key
     :bucket aws-bucket
     :title filename
     :timestamp (java.util.Date.)}))
