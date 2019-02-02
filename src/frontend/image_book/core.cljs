(ns image-book.core
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [image-book.events :as events]
   [image-book.views :as views]
   [image-book.config :as config]))

(defn dev-setup []
  (enable-console-print!)
  (println "dev mode"))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
