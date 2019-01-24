(ns image-book.views
  (:require
   [re-frame.core :refer [subscribe dispatch]]
   [image-book.subs :as subs]
   [image-book.events :as events]))

(defn upload-box []
  (let [uploading? (subscribe [::subs/uploading?])]
    [:div#upload-box.flex-column
     (if @uploading?
         [:div#box-uploading
          [:p "Uploading..."]]
         [:div#box-input
          [:input#file.box-file {:type "file"
                                 :on-drop #(dispatch [::events/upload-photo (-> % .-target .-files first)])}]
          [:label {:for "file"}
           [:strong.event-link "Choose a file"]
           [:span " or drag it here"]]])
     [:p.event-link {:on-click #(dispatch [::events/main-view])} "close"]]))

(defn image-viewer []
  (let [displayed-image @(subscribe [::subs/displayed-image])]
    [:div#image-viewer.flex-column
     (when displayed-image
       [:div#main-image
        [:img {:src (:url displayed-image)}]])
     [:p.event-link {:on-click #(dispatch [::events/upload-view])} "Upload an image"]]))

(defn image-thumbnail [image]
  [:div.thumbnail {:key (:id image)}
   [:img.event-link {:src (:url image)
                     :on-click #(dispatch [::events/display-image image])}]])

(defn image-gallery []
  (let [uploaded-images (subscribe [::subs/all-images])]
    [:div#gallery
     (for [image @uploaded-images]
       (image-thumbnail image))]))

(defn main-panel []
  (let [upload-view? (subscribe [::subs/upload-view?])]
    [:div#main-panel.flex-column
     [:h1 "The Image Book"]
     (if @upload-view?
         [upload-box]
         [image-viewer])
     [image-gallery]]))