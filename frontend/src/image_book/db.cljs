(ns image-book.db
  (:require [image-book.websockets :as ws]))

(def default-db
  {:all-images [{:id 1
                 :title "Nixon"
                 :url "https://images.fineartamerica.com/images/artworkimages/mediumlarge/1/they-cant-lick-our-dick-nixon-72-election-poster-war-is-hell-store.jpg"}
                {:id 2
                 :title "Objet du désir"
                 :url "http://www.espressokino.ca/wp-content/uploads/obscure-object-2.jpg"}
                {:id 3
                 :title "Whut"
                 :url "https://img.washingtonpost.com/wp-apps/imrs.php?src=https://img.washingtonpost.com/rf/image_908w/2010-2019/Wires/Images/2015-06-08/Getty/TS-DV2058912.jpg&w=1484"}]})
