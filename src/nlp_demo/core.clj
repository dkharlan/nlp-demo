(ns nlp-demo.core
  (:gen-class)
  (:require [clojure.java.io :as io]
            [clojurewerkz.propertied.properties :as props])
  (:import (org.apache.pdfbox.pdfparser PDFParser)
           (org.apache.pdfbox.pdmodel PDDocument)
           (org.apache.pdfbox.util PDFTextStripper)
           (edu.stanford.nlp.pipeline StanfordCoreNLP Annotation)
           (java.util Properties)
           (edu.stanford.nlp.ling CoreAnnotations$TokensAnnotation)))

(def default-annotator-props (props/map->properties {:annotators "tokenize"}))

(defn read-pdf-text [file]
  (let [parser (PDFParser. (io/input-stream file))]
    (.parse parser)
    (with-open [cos-doc (.getDocument parser)
                pd-doc (PDDocument. cos-doc)]
      (let [stripper (PDFTextStripper.)]
        (.getText stripper pd-doc)))))

(defn read-all-pdfs [resource-dir]
  (map read-pdf-text (-> resource-dir
                         (io/resource)
                         (io/file)
                         (file-seq)
                         (rest))))

(defn analyze-document [annotator-props text]
  (let [pipeline (StanfordCoreNLP. ^Properties annotator-props)
        annotation (Annotation. ^String text)]
    (.annotate pipeline annotation)
    annotation))

(defn -main
  [& args]
  (let [sample-text (read-pdf-text (io/resource "essays/D2.130.10.pdf"))
        document (analyze-document default-annotator-props sample-text)]
    (.get document CoreAnnotations$TokensAnnotation)))
