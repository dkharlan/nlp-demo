(ns nlp-demo.analysis
  (:require [nlp-demo.nlp :as nlp]))

(defn is-punctuation? [token]
  (let [punctuation ["," "." ":" "?" "!" "'" "-LRB-" "-RRB-"]]
    (some #(= (nlp/token->pos token) %) punctuation)))

(defn remove-punctuation [tokens]
  (->> tokens
       (remove is-punctuation?)))

(defn is-non-normalized-named-entity? [token]
  (let [entity (nlp/token->entity token)
        normalized-entity (nlp/token->normalized-entity token)]
    (and entity
         (not normalized-entity)
         (not (= entity "O")))))

(defn find-named-entity-labels [document]
  (->> document
       (nlp/document->sentences)
       (map nlp/sentence->tokens)
       (map #(filter is-non-normalized-named-entity? %))
       (reduce concat [])))

(defn find-named-entity-counts [named-entity-labels]
  (let [named-entity-texts (->> named-entity-labels
                                (map nlp/label->text)
                                (map clojure.string/lower-case))
        texts-with-counts (for [txt named-entity-texts]
                            [txt (count (filter #(= txt %) named-entity-texts))])]
    (into {} texts-with-counts)))

(defn count-entities [documents]
  (->> documents
       (map find-named-entity-labels)
       (map find-named-entity-counts)))

(defn combine-and-sort-counts [counts]
  (let [combined-counts (apply merge-with + counts)]
    (into (sorted-map-by (fn [key1 key2]
                           (compare [(get combined-counts key2) key2]
                                    [(get combined-counts key1) key1])))
          combined-counts)))

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

(defn sentence->ngrams [sentence n]
  (let [tokenized-sentence (->> sentence
                                (nlp/sentence->tokens)
                                (map nlp/label->text)
                                (map clojure.string/lower-case))]
    (tokens->ngrams tokenized-sentence n)))

(defn find-ngram-counts [ngrams]
  (into {} (for [ngram ngrams]
             [(vec ngram) (count (filter #(= ngram %) ngrams))])))

(defn find-token-counts [tokens]
  (into {} (for [token tokens]
             [token (count (filter #(= token %) tokens))])))

(defn count-tokens-in-docs [documents]
  (->> documents
       (map nlp/document->sentences)
       (mapcat #(mapcat nlp/sentence->tokens %))
       (map nlp/label->text)
       (map clojure.string/lower-case)
       (find-token-counts)))

; Bigram prob P(Wi | Wi-1) = count(Wi-1,Wi) / count(Wi-1)
(defn ngram-probability [ngram-counts token-counts]
  (into {} (for [[ngram ngram-count] ngram-counts]
             (let [first-word (first ngram)
                   first-word-count (get token-counts first-word)
                   ngram-prob (double (/ ngram-count first-word-count))]
               [ngram ngram-prob]))))

(defn document->ngrams [document n]
  (->> document
       (nlp/document->sentences)
       (mapcat #(sentence->ngrams % n))))

(defn count-ngrams-in-docs [documents n]
  (->> documents
       (mapcat #(document->ngrams % n))
       (find-ngram-counts)))
