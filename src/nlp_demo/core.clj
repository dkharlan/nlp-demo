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
           (edu.stanford.nlp.dcoref CorefCoreAnnotations$CorefChainAnnotation)
           (intoxicant.analytics.coreNlp StopwordAnnotator)
           (edu.stanford.nlp.util Pair)))

; tokenize, pos added as dependencies for ssplit, lemma respectively
(def annotators {:tokenize CoreAnnotations$TokensAnnotation
                 :ssplit   CoreAnnotations$SentencesAnnotation
                 :pos      CoreAnnotations$PartOfSpeechAnnotation
                 :lemma    CoreAnnotations$LemmaAnnotation
                 :stopword StopwordAnnotator
                 :ner      [CoreAnnotations$NamedEntityTagAnnotation
                            CoreAnnotations$NormalizedNamedEntityTagAnnotation]
                 :parse    [SemanticGraphCoreAnnotations$BasicDependenciesAnnotation
                            SemanticGraphCoreAnnotations$CollapsedDependenciesAnnotation
                            SemanticGraphCoreAnnotations$CollapsedCCProcessedDependenciesAnnotation]
                 :dcoref   CorefCoreAnnotations$CorefChainAnnotation})

(def default-annotator-props
  (props/map->properties {:annotators                    (apply str (interpose ", " (map name (keys annotators))))
                          :customAnnotatorClass.stopword "intoxicant.analytics.coreNlp.StopwordAnnotator"}))

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

(defn document->sentences [document]
  (.get document CoreAnnotations$SentencesAnnotation))

(defn sentence->tokens [sentence]
  (.get sentence CoreAnnotations$TokensAnnotation))

(defn sentence->dependency-graph [sentence]
  (.get sentence SemanticGraphCoreAnnotations$CollapsedCCProcessedDependenciesAnnotation))

(defn label->text [label]
  (.get label CoreAnnotations$TextAnnotation))

(defn token->pos [token]
  (.get token CoreAnnotations$PartOfSpeechAnnotation))

(defn token->entity [token]
  (.get token CoreAnnotations$NamedEntityTagAnnotation))

(defn token->normalized-entity [token]
  (.get token CoreAnnotations$NormalizedNamedEntityTagAnnotation))

(defn is-stopword? [token]
  (.first ^Pair (.get token StopwordAnnotator)))

(defn is-punctuation? [token]
  (let [punctuation ["," "." ":" "?" "!" "'" "-LRB-" "-RRB-"]]
    (some #(= (token->pos token) %) punctuation)))

(defn remove-stopwords-and-punctuation [tokens]
  (->> tokens
       (remove is-stopword?)
       (remove is-punctuation?)))

; inspired by http://blog.find-method.de/index.php?/archives/208-Coding-katas-Clojure-Trigrams.html
; EPL licensed
(defn tokens->ngrams
  ([tokens n]
   (lazy-seq
     (tokens->ngrams tokens n [])))
  ([tokens n accum]
   (if-let [tokens (seq tokens)]
     (recur (rest tokens) n (conj accum (take n tokens)))
     accum)))

(defn print-token-info [token]
  (let [text (label->text token)
        part-of-speech (token->pos token)
        named-entity (token->entity token)
        normalized-named-entity (token->normalized-entity token)]
    (printf "%-16s%-8s%-12s%-15s%-5s\n"
            text
            part-of-speech
            (if (is-stopword? token)
              "Y"
              "N")
            named-entity
            (or normalized-named-entity ""))))

(defn print-ngram [ngram]
  (let [ngram-desc (apply str (interpose " " (map label->text ngram)))
        pos-desc (apply str (interpose " " (map token->pos ngram)))]
    (printf "%-45s%-15s\n" ngram-desc pos-desc)))

(defn print-sentence-info [sentence]
  (let [text (label->text sentence)
        tokens (sentence->tokens sentence)
        n 3
        ngrams (tokens->ngrams (remove-stopwords-and-punctuation tokens) n)
        dependency-graph (sentence->dependency-graph sentence)]
    (println "+===============================================================+")
    (printf "Sentence: %s\n\n" text)
    (printf "%-16s%-8s%-12s%-15s%-5s" "Word" "POS" "Stopword?" "Ent." "N.Ent.\n")
    (println "-----------------------------------------------------------------")
    (doseq [token tokens]
      (print-token-info token))
    (printf "\nDependency graph:\n%s\n" (str dependency-graph))
    (printf "%d-grams\n--------\n" n)
    (doseq [ngram ngrams]
      (print-ngram ngram))
    (println "+===============================================================+\n")))

(defn print-document-info [document]
  (let [sentences (document->sentences document)]
    (doseq [sentence sentences]
      (print-sentence-info sentence))))

(defn -main
  [& args]
  (->> "essays/D2.130.10.pdf"
       (io/resource)
       (read-pdf-text)
       (analyze-document default-annotator-props)
       (print-document-info)))
