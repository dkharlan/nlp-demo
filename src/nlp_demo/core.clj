(ns nlp-demo.core
  (:gen-class)
  (:require [clojure.java.io :as io]
            [clojurewerkz.propertied.properties :as props])
  (:import (org.apache.pdfbox.pdfparser PDFParser)
           (org.apache.pdfbox.pdmodel PDDocument)
           (org.apache.pdfbox.util PDFTextStripper)
           (edu.stanford.nlp.pipeline StanfordCoreNLP Annotation)
           (java.util Properties)
           (edu.stanford.nlp.ling CoreAnnotations$TokensAnnotation CoreAnnotations$SentencesAnnotation CoreAnnotations$PartOfSpeechAnnotation CoreAnnotations$LemmaAnnotation CoreAnnotations$NamedEntityTagAnnotation CoreAnnotations$NormalizedNamedEntityTagAnnotation CoreAnnotations$TextAnnotation)
           (edu.stanford.nlp.semgraph SemanticGraphCoreAnnotations$BasicDependenciesAnnotation SemanticGraphCoreAnnotations$CollapsedDependenciesAnnotation SemanticGraphCoreAnnotations$CollapsedCCProcessedDependenciesAnnotation)
           (edu.stanford.nlp.dcoref CorefCoreAnnotations$CorefChainAnnotation)))

; tokenize, pos and parse added as dependencies for ssplit, lemma and dcoref respectively
(def annotators {:tokenize CoreAnnotations$TokensAnnotation
                 :ssplit   CoreAnnotations$SentencesAnnotation
                 :pos      CoreAnnotations$PartOfSpeechAnnotation
                 :lemma    CoreAnnotations$LemmaAnnotation
                 :ner      [CoreAnnotations$NamedEntityTagAnnotation
                            CoreAnnotations$NormalizedNamedEntityTagAnnotation]
                 :parse    [SemanticGraphCoreAnnotations$BasicDependenciesAnnotation
                            SemanticGraphCoreAnnotations$CollapsedDependenciesAnnotation
                            SemanticGraphCoreAnnotations$CollapsedCCProcessedDependenciesAnnotation]
                 :dcoref   CorefCoreAnnotations$CorefChainAnnotation})

(def default-annotator-props
  (props/map->properties {:annotators (apply str (interpose ", " (map name (keys annotators))))}))

(defn read-pdf-text [file]
  (let [parser (PDFParser. (io/input-stream file))]
    (.parse parser)
    (with-open [cos-doc (.getDocument parser)
                pd-doc (PDDocument. cos-doc)]
      (.getText (PDFTextStripper.) pd-doc))))

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

(defn print-token-info [token]
  (let [text (.get token CoreAnnotations$TextAnnotation)
        part-of-speech (.get token CoreAnnotations$PartOfSpeechAnnotation)
        named-entity (.get token CoreAnnotations$NamedEntityTagAnnotation)
        normalized-named-entity (.get token CoreAnnotations$NormalizedNamedEntityTagAnnotation)]
    (printf "%-16s%-8s%-15s%-5s\n" text part-of-speech named-entity (or normalized-named-entity ""))))

(defn print-sentence-info [sentence]
  (let [text (.get sentence CoreAnnotations$TextAnnotation)
        tokens (.get sentence CoreAnnotations$TokensAnnotation)
        dependency-graph (.get sentence SemanticGraphCoreAnnotations$CollapsedCCProcessedDependenciesAnnotation)]
    (printf "Sentence: %s\n" text)
    (printf "%-16s%-8s%-15s%-5s" "Word" "POS" "Ent." "N.Ent.\n")
    (println "-------------------------------------------------")
    (doseq [token tokens]
      (print-token-info token))
    (printf "Dependency graph:\n%s\n" (str dependency-graph))))

(defn print-document-info [document]
  (let [sentences (.get document CoreAnnotations$SentencesAnnotation)]
    (doseq [sentence sentences]
      (print-sentence-info sentence))))

(defn -main
  [& args]
  (->> "essays/D2.130.10.pdf"
       (io/resource)
       (read-pdf-text)
       (analyze-document default-annotator-props)
       (print-document-info)))
