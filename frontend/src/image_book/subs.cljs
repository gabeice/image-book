(ns image-book.subs
  (:require
   [re-frame.core :refer [reg-sub]]))

(reg-sub
  ::displayed-image
  (fn [db]
    (or (:displayed-image db)
        (first (:all-images db)))))

(reg-sub
  ::all-images
  (fn [db]
    (:all-images db)))

(reg-sub
 ::upload-view?
 (fn [db]
   (:upload-view? db)))

(reg-sub
  ::uploading?
  (fn [db]
    (:uploading? db)))
