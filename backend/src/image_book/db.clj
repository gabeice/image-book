(ns image-book.db
  (:require [datomic.client.api :as d]
            [image-book.s3 :as s3]
            [image-book.util :as util]))

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
                    :db/doc "The image file etag"}
                   {:db/ident :photo/timestamp
                    :db/valueType :db.type/instant
                    :db/cardinality :db.cardinality/one
                    :db/doc "The time the photo was added"}])

(d/transact conn {:tx-data photo-schema})

(defn all-photos []
  (let [db (d/db conn)
        query '[:find ?title ?bucket ?key ?timestamp
                :where [?e :photo/title ?title]
                       [?e :photo/bucket ?bucket]
                       [?e :photo/key ?key]
                       [?e :photo/timestamp ?timestamp]]
        photos (d/q query db)
        photo-map (reduce
                    (fn [coll el]
                      (let [[title bucket key timestamp] el]
                        (assoc coll (.getTime timestamp) {:title title
                                                          :url (util/url bucket key)})))
                    {}
                    photos)]
    (into (sorted-map-by >) photo-map)))

(defn save-photo [image-file]
  (let [uploaded-photo (s3/upload-photo image-file)
        {:keys [title bucket key etag timestamp]} uploaded-photo
        photo-data [{:photo/title     title
                     :photo/bucket    bucket
                     :photo/key       key
                     :photo/etag      etag
                     :photo/timestamp timestamp}]]
    (d/transact conn {:tx-data photo-data})
    {:timestamp (.getTime timestamp)
     :image {:title title
             :url (util/url bucket key)}}))

