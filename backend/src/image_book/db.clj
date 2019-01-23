(ns image-book.db
  (:require [datomic.client.api :as d]))

(def cfg {:server-type :peer-server
          :access-key "myaccesskey"
          :secret "mysecret"
          :endpoint "localhost:8998"})

(def client (d/client cfg))
(def conn (d/connect client {:db-name "image-book"}))

(def photo-schema [{:db/ident :photo/title
                    :db/valueType :db.type/string
                    :db/cardinality :db.cardinality/one
                    :db/doc "The title of the photo"}
                   {:db/ident :photo/url
                    :db/valueType :db.type/string
                    :db/cardinality :db.cardinality/one
                    :db/doc "The s3 url for the photo"}])

(defn all-photos []
  (let [db (d/db conn)
        query '[:find ?title ?url
                :where [?e :photo/title ?title
                        ?e :photo/url ?url]]
        [title url] (d/q query db)]
    {:title title
     :url url}))

(defn add-photo [image-file]
  (let [uploaded-photo [{:photo/title (:title image-file)}]]
    (d/transact conn {:tx-data uploaded-photo})))
