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
  (fn [db [_ image-id]]
    (assoc db :displayed-image image-id)))

(reg-event-fx
  ::upload-photo
  (fn [cofx [_ image-file]]
    (update cofx :uploaded-photos conj image-file)))

(reg-fx
  ::uploaded-photos
  (fn [photos]
    (doseq [photo photos]
      (ws/upload-photo photo))))