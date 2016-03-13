(ns nlp-demo.core
  (:gen-class)
  (:require [clojure.java.io :as io])
  (:import (org.apache.pdfbox.pdfparser PDFParser)
           (org.apache.pdfbox.pdmodel PDDocument)
           (org.apache.pdfbox.util PDFTextStripper)))

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

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
