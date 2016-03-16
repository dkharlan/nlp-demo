(ns nlp-demo.samples
  (:require [clojure.java.io :as io]
            [nlp-demo.pdf :refer :all]
            [nlp-demo.nlp :refer :all]
            [nlp-demo.analysis :refer :all]
            [nlp-demo.reports :refer :all]))

(defn analyze-all-docs []
  (->> "essays"
       (get-all-paths)
       (map read-pdf-text)
       (map #(analyze-document default-annotator-props %))))

(defn all-named-entities []
  (-> (analyze-all-docs)
      (count-entities)
      (combine-and-sort-counts)))

(defn ngram-probabilities []
  (let [documents (analyze-all-docs)
        ngram-counts (count-ngrams-in-docs documents 2)
        token-counts (count-tokens-in-docs documents)
        ngram-probs (ngram-probability ngram-counts token-counts)]
    (into (sorted-map-by (fn [k1 k2]
                           (compare [(get ngram-probs k2) k2]
                                    [(get ngram-probs k1) k1])))
          ngram-probs)))

(defn display-document-info [doc-path]
  (->> doc-path
       (io/resource)
       (read-pdf-text)
       (analyze-document default-annotator-props)
       (print-document-info)))
