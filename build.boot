(set-env!
 :source-paths
 #{"src"}

 :dependencies
 '[[org.clojure/clojurescript "0.0-2814"]
   [pandeiro/boot-http        "0.6.3-SNAPSHOT"]
   [adzerk/boot-cljs          "0.0-2814-4"]
   [adzerk/boot-cljs-repl     "0.1.9"]
   [adzerk/boot-reload        "0.2.6"]])

(require '[pandeiro.boot-http    :refer :all]
         '[adzerk.boot-cljs      :refer :all]
         '[adzerk.boot-cljs-repl :refer :all]
         '[adzerk.boot-reload    :refer :all])

(deftask start-dev
  "Start all the things."
  []
  (comp (watch)
        (serve :dir ".")
        (reload)
        (cljs-repl)
        (cljs :compiler-options {:source-map true
                                 :optimizations :none})))
