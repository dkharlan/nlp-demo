(ns nlp-demo.nlp
  (:require [clojurewerkz.propertied.properties :as props])
  (:import (edu.stanford.nlp.dcoref CorefCoreAnnotations$CorefChainAnnotation)
           (edu.stanford.nlp.semgraph SemanticGraphCoreAnnotations$CollapsedCCProcessedDependenciesAnnotation SemanticGraphCoreAnnotations$CollapsedDependenciesAnnotation SemanticGraphCoreAnnotations$BasicDependenciesAnnotation)
           (edu.stanford.nlp.ling CoreAnnotations$NormalizedNamedEntityTagAnnotation CoreAnnotations$NamedEntityTagAnnotation CoreAnnotations$LemmaAnnotation CoreAnnotations$PartOfSpeechAnnotation CoreAnnotations$SentencesAnnotation CoreAnnotations$TokensAnnotation CoreAnnotations$TextAnnotation)
           (edu.stanford.nlp.pipeline StanfordCoreNLP Annotation)
           (java.util Properties)))

; this structure is just for convenience
; tokenize, pos added as dependencies for ssplit, lemma respectively
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
