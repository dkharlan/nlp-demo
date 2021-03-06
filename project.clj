(defproject nlp-demo "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [edu.stanford.nlp/stanford-corenlp "3.6.0"]
                 [edu.stanford.nlp/stanford-corenlp "3.6.0" :classifier "models"]
                 [org.apache.pdfbox/pdfbox "1.8.11"]
                 [clojurewerkz/propertied "1.2.0"]]
  :main ^:skip-aot nlp-demo.samples
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {:dependencies [[alembic "0.3.2"]]}})
