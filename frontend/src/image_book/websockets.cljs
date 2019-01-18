(ns image-book.websockets
  (:require [image-book.config :as config]
            [taoensso.sente :as sente]))

(defn get-chsk-url
  "Connect to a configured server instead of the page host"
  [protocol chsk-host chsk-path type]
  (let [protocol (case type :ajax protocol
                            :ws   (if (= protocol "https:") "wss:" "ws:"))]
    (str protocol "//" config/server chsk-path)))

(defonce channel-socket
  (with-redefs [sente/get-chsk-url get-chsk-url]
    (sente/make-channel-socket! "/chsk")))

(defonce chsk (:chsk channel-socket))
(defonce ch-chsk (:ch-recv channel-socket))
(defonce chsk-send! (:send-fn channel-socket))
(defonce chsk-state (:state channel-socket))

(defmulti event-msg-handler :id)

(defmethod event-msg-handler :default [{:keys [event]}]
  (println "Unhandled event: " event))

(defmethod event-msg-handler :chsk/state [{:keys [?data]}]
  (if (:first-open? (second ?data))
      (println "Channel socket successfully established!")
      (println "Channel socket state change:" ?data)))

(defmethod event-msg-handler :chsk/recv [{:keys [?data]}]
  (println "Push event from server: " ?data))

(defmethod event-msg-handler :chsk/handshake [{:keys [?data]}]
  (println "Handshake: " ?data))

(defn fetch-photos []
  (chsk-send! [:image-book/fetch-photos]))

(defn upload-photo [image-file]
  (chsk-send! [:image-book/upload-photo image-file]))

(defonce router
  (sente/start-client-chsk-router! ch-chsk event-msg-handler))