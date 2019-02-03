run:
	clj -Aserver -m image-book.server

clean:
	rm -rf resources/public/js

compile:
	clojure script/build.clj compile

figwheel:
	clojure -Adev script/build.clj figwheel