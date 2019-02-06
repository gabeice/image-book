(ns image-book.routes
  (:require
    [image-book.db :as db]
    [clojure.data.json :as json]
    [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
    [ring.middleware.cors :as cors]
    [ring.middleware.multipart-params :as mp]
    [ring.util.response :as response]
    [compojure.core :refer [defroutes GET POST]]
    [compojure.route :as route]
    [org.httpkit.server :refer [send! with-channel on-close on-receive]]))

(defonce channels (atom #{}))

(defn connect! [channel]
  (println "channel open")
  (swap! channels conj channel))

(defn disconnect! [channel status]
  (println "channel closed:" status)
  (swap! channels #(remove #{channel} %)))

(defn notify-clients [msg]
  (doseq [channel @channels]
     (send! channel msg)))

(defn ws-handler [request]
  (with-channel request channel
                (connect! channel)
                (on-close channel (partial disconnect! channel))
                (on-receive channel #(notify-clients %))))

(defroutes routes
  (GET "/" _ (response/content-type
               (-> {:images (db/all-photos)}
                   json/write-str
                   response/response)
               "application/json"))
  (GET "/ws" request (ws-handler request))
  (mp/wrap-multipart-params
     (POST "/photo" {params :params}
       (let [saved-photo (db/save-photo (:file params))]
         (response/content-type
           (response/response (json/write-str {:new-image saved-photo}))
           "application/json"))))
  (route/not-found "Not found"))

(def handler
  (-> routes
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
      (cors/wrap-cors :access-control-allow-origin [#".*"]
                      :access-control-allow-methods [:get :put :post :delete]
                      :access-control-allow-credentials ["true"])))