(ns image-book.util)

(defn url [bucket key]
  (str "https://" bucket ".s3.amazonaws.com/" key))