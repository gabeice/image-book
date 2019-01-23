(ns image-book.views
  (:require
   [re-frame.core :refer [subscribe dispatch]]
   [image-book.subs :as subs]
   [image-book.events :as events]))

(defn upload-box []
  [:div#upload-box
   [:div#box-input
    [:input#file.box-file {:type "file"
                           :on-change #(dispatch [::events/upload-photo (-> % .-target .-files first)])}]
    [:label {:for "file"}
     [:strong "Choose a file"]
     [:span " or drag it here"]]]
   [:p {:on-click #(dispatch [::events/main-view])} "close"]])

(defn image-viewer []
  (let [displayed-image @(subscribe [::subs/displayed-image])]
    [:div#image-viewer
     (when displayed-image
       [:img {:href (:url displayed-image)
              :on-click #(dispatch [::events/display-image (:id displayed-image)])}])
     [:p {:on-click #(dispatch [::events/upload-view])} "Upload an image"]]))

(defn image-thumbnail [image]
  [:img {:href (:url image)}])

(defn image-gallery []
  (let [uploaded-images (subscribe [::subs/all-images])]
    [:div#gallery
     (for [image @uploaded-images]
       [image-thumbnail image])]))

(defn main-panel []
  (let [upload-view? (subscribe [::subs/upload-view?])]
    [:div#main-panel
     [:h1 "Le livre d'image"]
     (if @upload-view?
         [upload-box]
         [image-viewer])
     [image-gallery]]))