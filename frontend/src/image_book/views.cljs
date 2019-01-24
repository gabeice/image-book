(ns image-book.views
  (:require
   [re-frame.core :refer [subscribe dispatch]]
   [image-book.subs :as subs]
   [image-book.events :as events]))

(defn- neutralize-drag []
  (let [stop-drag-defaults (fn [e] (.preventDefault e) (.stopPropagation e))]
    {:on-drag       stop-drag-defaults
     :on-drag-enter stop-drag-defaults
     :on-drag-leave stop-drag-defaults
     :on-drag-over  stop-drag-defaults
     :on-drag-start stop-drag-defaults}))

(defn upload-box []
  (let [uploading? (subscribe [::subs/uploading?])
        upload-fn #(dispatch [::events/upload-photo (-> % .-dataTransfer .-files (.item 0))])]
    [:div#upload-box.flex-column (merge neutralize-drag {:on-drop upload-fn})
     (if @uploading?
         [:div#box-uploading
          [:p "Uploading..."]]
         [:div#box-input
          [:input#file.box-file {:type "file"}]
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

(defn dividing-line []
  [:div#dividing-line])

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
     [dividing-line]
     [image-gallery]]))