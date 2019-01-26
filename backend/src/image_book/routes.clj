(ns image-book.routes
  (:require
    [image-book.db :as db]
    [image-book.util :as util]
    [clojure.data.json :as json]
    [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
    [ring.middleware.cors :as cors]
    [ring.middleware.multipart-params :as mp]
    [ring.util.response :as response]
    [compojure.core :refer [defroutes GET POST]]
    [compojure.route :as route]))

(declare channel-socket)

(defroutes routes
  (GET "/" _ (response/content-type (json/write-str {:new-images (db/all-photos)}) "application/json"))
  (mp/wrap-multipart-params
     (POST "/photo" {params :params}
       (let [saved-photo (:tx-data (db/save-photo (get params "file")))]
         (response/content-type
           (json/write-str {:new-images [{:title (:photo/title saved-photo)
                                          :url (util/url (:photo/bucket saved-photo) (:photo/key saved-photo))}]})
           "application/json"))))
  (route/not-found "Not found"))

(def handler
  (-> routes
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
      (cors/wrap-cors :access-control-allow-origin [#".*"]
                      :access-control-allow-methods [:get :put :post :delete]
                      :access-control-allow-credentials ["true"])))