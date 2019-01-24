(ns image-book.events
  (:require
   [re-frame.core :refer [reg-event-db reg-event-fx reg-fx]]
   [image-book.db :as db]
   [image-book.websockets :as ws]))

(reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

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
        (update :uploaded-photos conj image-file)
        (assoc-in :db [:uploading? true]))))

(reg-fx
  ::uploaded-photos
  (fn [photos]
    (doseq [photo photos]
      (ws/upload-photo photo))))