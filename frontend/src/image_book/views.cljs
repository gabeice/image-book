(ns image-book.views
  (:require
   [re-frame.core :refer [subscribe dispatch]]
   [image-book.subs :as subs]
   [image-book.events :as events]))

(defn upload-box []
  [:div#upload-box
   [:input {:type "file"} "Drag image here"]])

(defn image-viewer []
  (let [displayed-image @(subscribe [::subs/displayed-image])]
    [:div#image-viewer
     (when displayed-image
       [:img {:href (:url displayed-image)
              :on-click (dispatch [::events/display-image (:id displayed-image)])}])]))

(defn image-thumbnail [image]
  [:img {:href (:url image)}])

(defn image-gallery []
  (let [uploaded-images (subscribe [::subs/all-images])]
    [:div#gallery
     (for [image @uploaded-images]
       [image-thumbnail image])]))

(defn main-panel []
  (let [upload-view? (subscribe [::subs/upload-view?])]
    [:div
     [:h1 "Le livre d'image"]
     (if @upload-view?
         [upload-box]
         [image-viewer])
     [image-gallery]]))