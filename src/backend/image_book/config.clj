(ns image-book.config
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))

(declare CONFIG-FILE)
(defonce CONFIG-FILE "config.edn")

(def config
  (-> CONFIG-FILE
      io/resource
      slurp
      edn/read-string))

(def db-config
  (:db config))

(def s3-config
  (:s3 config))