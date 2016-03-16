(ns nlp-demo.reports
  (:require [nlp-demo.nlp :as nlp]
            [nlp-demo.analysis :as analysis]))

(defn print-token-info [token]
  (let [text (nlp/label->text token)
        part-of-speech (nlp/token->pos token)
        named-entity (nlp/token->entity token)
        normalized-named-entity (nlp/token->normalized-entity token)]
    (printf "%-16s%-8s%-15s%-5s\n"
            text
            part-of-speech
            named-entity
            (or normalized-named-entity ""))))

(defn print-ngram [ngram]
  (let [ngram-desc (apply str (interpose " " (map nlp/label->text ngram)))
        pos-desc (apply str (interpose " " (map nlp/token->pos ngram)))]
    (printf "%-45s%-15s\n" ngram-desc pos-desc)))

(defn print-sentence-info [sentence]
  (let [text (nlp/label->text sentence)
        tokens (nlp/sentence->tokens sentence)
        n 2
        ngrams (analysis/tokens->ngrams (analysis/remove-punctuation tokens) n)
        dependency-graph (nlp/sentence->dependency-graph sentence)]
    (println "+===============================================================+")
    (printf "Sentence: %s\n\n" text)
    (printf "%-16s%-8s%-15s%-5s\n" "Word" "POS" "Ent." "N.Ent.")
    (println "-----------------------------------------------------------------")
    (doseq [token tokens]
      (print-token-info token))
    (printf "\nDependency graph:\n%s\n" (str dependency-graph))
    (printf "%d-grams\n--------\n" n)
    (doseq [ngram ngrams]
      (print-ngram ngram))
    (println "+===============================================================+\n")))

(defn print-document-info [document]
  (let [sentences (nlp/document->sentences document)]
    (doseq [sentence sentences]
      (print-sentence-info sentence))))
