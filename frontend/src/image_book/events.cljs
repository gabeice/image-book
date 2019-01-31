(ns image-book.events
  (:require
   [re-frame.core :refer [reg-event-db reg-event-fx reg-fx dispatch]]
   [ajax.core :as ajax]
   [day8.re-frame.http-fx]
   [image-book.config :as config]
   [image-book.db :as db]))

(reg-event-fx
 ::initialize-db
 (fn [_ _]
   {:db db/default-db
    :http-xhrio {:method          :get
                 :uri             config/server
                 :response-format (ajax/json-response-format {:keywords? true})
                 :timeout         30000
                 :on-success      [::process-all-photos]
                 :on-failure      [::process-failure]}}))

(reg-event-db
  ::process-failure
  (fn [db [_ response]]
    (println (str "Request failed: " response))
    db))

(reg-event-db
  ::process-all-photos
  (fn [db [_ response-data]]
    (assoc db :all-images (:images response-data))
    db))

(reg-event-db
  ::process-new-photo
  (fn [db [_ response-data]]
    (let [{:keys [timestamp image]} (:new-image response-data)]
      (-> db
          (update :all-images assoc timestamp image)
          (assoc :displayed-image image
                 :upload-view?    false
                 :uploading?      false)))))

(reg-event-db
  ::display-image
  (fn [db [_ image]]
    (assoc db :displayed-image image)))

(reg-event-db
  ::upload-view
  (fn [db _]
    (assoc db :upload-view? true)))

(reg-event-db
  ::main-view
  (fn [db _]
    (assoc db :upload-view? false)))

(reg-event-fx
  ::upload-photo
  (fn [cofx [_ image-file]]
    (println image-file)
    (-> cofx
        (assoc-in [:db :uploading?] true)
        (assoc :http-xhrio {:method           :post
                            :uri              (str config/server "/photo")
                            :multipart-params {:file image-file}
                            :response-format  (ajax/json-response-format {:keywords? true})
                            :timeout          30000
                            :on-success       [::process-new-photo]
                            :on-failure       [::process-failure]}))))