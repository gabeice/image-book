(ns image-book.db
  (:require [datomic.client.api :as d]
            [image-book.s3 :as s3]))

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
                   {:db/ident :photo/bucket
                    :db/valueType :db.type/string
                    :db/cardinality :db.cardinality/one
                    :db/doc "The s3 bucket for the photo"}
                   {:db/ident :photo/key
                    :db/valueType :db.type/string
                    :db/cardinality :db.cardinality/one
                    :db/doc "The s3 key for the photo"}
                   {:db/ident :photo/etag
                    :db/valueType :db.type/string
                    :db/cardinality :db.cardinality/one
                    :db/doc "The image file etag"}])

(d/transact conn {:tx-data photo-schema})

(defn- url [bucket key]
  (str "https://" bucket ".s3.amazonaws.com/" key))

(defn all-photos []
  (let [db (d/db conn)
        query '[:find ?title ?bucket ?key
                :where [?e :photo/title ?title
                        ?e :photo/bucket ?bucket
                        ?e :photo/key ?key]]
        [title bucket key] (d/q query db)]
    {:title title
     :url (url bucket key)}))

(defn add-photo [image-file]
  (let [uploaded-photo (s3/upload-photo image-file)
        photo-data [{:photo/title  (:title uploaded-photo)
                     :photo/bucket (:bucket uploaded-photo)
                     :photo/key    (:key uploaded-photo)
                     :photo/etag   (:etag uploaded-photo)}]]
    (d/transact conn {:tx-data photo-data})))
