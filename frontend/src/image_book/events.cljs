(ns image-book.events
  (:require
   [re-frame.core :refer [reg-event-db reg-event-fx reg-fx dispatch]]
   [ajax.core :as ajax]
   [image-book.config :as config]
   [image-book.db :as db]))

(reg-event-fx
 ::initialize-db
 (fn [_ _]
   {:db db/default-db
    :http-xhrio {:method :get
                 :uri config/server
                 :response-format (ajax/json-response-format {:keywords? true})
                 :timeout 30000
                 :on-success [::process-response]}}))

(reg-event-db
  ::process-response
  (fn [db [_ response-data]]
    (-> db
        (update :all-images into (:new-images response-data))
        (assoc :displayed-image (first (:new-images response-data))
               :upload-view? false
               :uploading? false))))

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
    (-> cofx
        (assoc-in [:db :uploading?] true)
        (assoc :http-xhrio {:method :post
                            :uri (str config/server "/photo")
                            :params {:file image-file}
                            :response-format (ajax/json-response-format {:keywords? true})
                            :timeout 30000
                            :on-success [::process-response]}))))