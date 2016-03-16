(ns nlp-demo.pdf
  (:require [clojure.java.io :as io])
  (:import (org.apache.pdfbox.pdfparser PDFParser)
           (org.apache.pdfbox.pdmodel PDDocument)
           (org.apache.pdfbox.util PDFTextStripper)
           (java.io IOException)))

(defn read-pdf-text [file]
  (let [parser (PDFParser. (io/input-stream file))]
    (.parse parser)
    (with-open [cos-doc (.getDocument parser)
                pd-doc (PDDocument. cos-doc)]
      (try
        (.getText (PDFTextStripper.) pd-doc)
        (catch IOException _ "")))))

(defn get-all-paths [resource-dir]
  (-> resource-dir
      (io/resource)
      (io/file)
      (file-seq)
      (rest)))
