(ns nlp-demo.samples
  (:require [nlp-demo.core :refer :all]
            [clojure.java.io :as io]))

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
