(ns image-book.server
  (:require
    [org.httpkit.server :as server]
    [image-book.routes :as routes])
  (:gen-class))

(defn -main [& args]
  (println "Server starting...")
  (server/run-server routes/handler {:port 3000}))