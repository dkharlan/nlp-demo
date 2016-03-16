(ns nlp-demo.samples
  (:require [clojure.java.io :as io]
            [nlp-demo.pdf :refer :all]
            [nlp-demo.nlp :refer :all]
            [nlp-demo.analysis :refer :all]
            [nlp-demo.reports :refer :all]))

(defn all-named-entities []
  (->> "essays"
       (get-all-paths)
       (map read-pdf-text)
       (map #(analyze-document default-annotator-props %))
       (count-entities)
       (combine-and-sort-counts)))

(defn display-document-info [doc-path]
  (->> doc-path
       (io/resource)
       (read-pdf-text)
       (analyze-document default-annotator-props)
       (print-document-info)))
