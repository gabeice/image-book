;adapted from http://www.functionalbytes.nl/clojure/nodejs/figwheel/repl/clojurescript/cli/2017/12/20/tools-deps-figwheel.html

(require '[cljs.build.api :as api])

(def source-dir "src/frontend")

(def compiler-config {:main          'image-book.core
                      :output-to     "resources/public/js/compiled/app.js"
                      :output-dir    "resources/public/js/compiled/out"
                      :target        :nodejs
                      :optimizations :none
                      :source-map    true})

(defn try-require
  [ns-sym]
  (try (require ns-sym) true (catch Exception e (println ns-sym " not found") false)))

(defmacro with-namespaces
  [namespaces & body]
  (if (every? try-require namespaces)
    `(do ~@body)
    `(println "task not available - required dependencies not found")))

(defmulti task first)

(defmethod task :default
  [args]
  (let [all-tasks  (-> task methods (dissoc :default) keys sort)
        interposed (->> all-tasks (interpose ", ") (apply str))]
    (println "Unknown or missing task. Choose one of:" interposed)
    (System/exit 1)))

(defmethod task "compile" [args]
  (api/build source-dir compiler-config))

(defmethod task "figwheel" [_]
  (with-namespaces [figwheel.main.api]
                   (figwheel.main.api/start {:watch-dirs ["src/frontend"]}
                                            {:id      "dev"
                                             :options {:main                 'image-book.core
                                                       :output-to            "resources/public/js/compiled/app.js"
                                                       :output-dir           "resources/public/js/compiled/out"
                                                       :asset-path           "js/compiled/out"
                                                       :source-map-timestamp true
                                                       :optimizations        :none
                                                       :preloads             ['devtools.preload]}})))

(task *command-line-args*)