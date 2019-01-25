(ns image-book.routes
  (:require
    [image-book.db :as db]
    [ring.middleware.defaults :as defaults]
    [ring.middleware.reload :as reload]
    [ring.middleware.cors :as cors]
    [ring.util.response :as response]
    [environ.core :as environ]
    [taoensso.sente :as sente]
    [taoensso.sente.server-adapters.http-kit :as http-kit]
    [compojure.core :refer [defroutes GET POST]]
    [compojure.route :as route]))

(declare channel-socket)

(defroutes routes
  (GET "/" _ (response/content-type "{}" "application/json"))
  (GET "/status" _ (str "Running: " (pr-str @(:connected-uids channel-socket))))
  (GET "/chsk" req ((:ajax-get-or-ws-handshake-fn channel-socket) req))
  (POST "/chsk" req ((:ajax-post-fn channel-socket) req))
  (route/resources "/")
  (route/not-found "Not found"))

(def handler
  (-> routes
      (cond-> (environ/env :dev?) (reload/wrap-reload))
      (defaults/wrap-defaults (assoc-in defaults/site-defaults [:security :anti-forgery] false))
      (cors/wrap-cors :access-control-allow-origin [#".*"]
                      :access-control-allow-methods [:get :put :post :delete]
                      :access-control-allow-credentials ["true"])))

(defn start-websocket []
  (defonce channel-socket
    (sente/make-channel-socket! (http-kit/get-sch-adapter) {})))

(defmulti event :id)

(defmethod event :default [{:keys [event]}]
  (println "Unhandled event: " event))

(defmethod event :chsk/uidport-open [{:keys [uid client-id]}]
  (println "New connection:" uid client-id))

(defmethod event :chsk/uidport-close [{:keys [uid]}]
  (println "Disconnected:" uid))

(defmethod event :chsk/ws-ping [_])

(defmethod event :image-book/fetch-photos [_]
  (db/all-photos))

(defmethod event :image-book/upload-photo [image-file]
  (db/add-photo image-file))

(defn start-router []
  (defonce router
    (sente/start-chsk-router! (:ch-recv channel-socket) event)))