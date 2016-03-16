## First Steps
* Decided to use Clojure for rapid prototyping
* Used PDFBox to extract text
* Decided to focus on shallow techniques -- deep learning requires more expertise and time for training

## Experimentation
### Dependency Parsing
* Played with dependency graphs, but challenging to pull meaning -- decided to skip

### Sentiment Analysis
* Can recognize positive / negative connotations
* CoreNLP uses deep learning to improve on typical approach
* Potentially useful, but probably needs domain-specific tuning

### Named Entity Recognition
* Built in NER can recognize people, locations, organizations
* Also recognizes normalized entities, e.g. dates, ordinals, numbers, etc.
* Can use this to pull out commonly used entities
* We care less about normalized entities for now (little meaning without context)
* Pull out named entities and convert to lowercase, remove normalized entities
* Combine and sort counts for all named entities
* Problems:
    * Token-based with no knowledge of grammar; could combine with Dependency Parsing
    * Included NER model is very generic
    * Better results if trained with domain-specific corpus
    
### N-Grams
* Approaches besides CoreNLP?
* Found Stanford's NLP course on Coursera
* How likely is "He went to the store" likely to appear?
* Markov assumption: assume that P(store | the | to | went | He) = P(store | He)
* P(W<sub>i</sub> | W<sub>i-1</sub>) = count(W<sub>i-1</sub>,W<sub>i</sub>) / count(W<sub>i-1</sub>)
    * E.g. if P(have | would) = 0.5, then "would have" accounts for 50% of the occurrences of "would"
* Separated words into bigrams
* Bigram probabilities reflect different characteristics of corpus
    * Some relevant -- e.g. domain knowledge
    * Some irrelevant -- grammar "artifacts"
* Found other suggested approaches
    * Tokenize, remove stopwords, construct trigrams, drop everything but noun-verb combinations
    
