## First Steps
* Decided to use Clojure
    * Excels at rapid prototyping and interactive development
    * Easy to use Java libraries
* Used PDFBox to extract text
* Wrote simple wrapper around library

## Playing With Stanford CoreNLP
### Dependency Parsing
* Powerful feature, but challenging to pull meaning in isolation

### Sentiment Analysis
* Can recognize positive / negative connotations
* CoreNLP uses deep learning to improve on typical approach
* Potentially useful, but requires domain-specific tuning

### Named Entity Recognition
* Built in NER can recognize people, locations, organizations
* Also recognizes normalized entities, e.g. dates, ordinals, numbers, etc.
* Can use this to pull out commonly used entities
* We care less about normalized entities for now (little meaning without context)
* Pull out named entities and convert to lowercase, remove normalized entities
* Combine and sort counts for all named entities
* Problems with my method:
    * Token-based with no knowledge of grammar; could combine with Dependency Parsing or EntityMentionsAnnotator
    * Could combine with dependency analysis to pull out phrases instead of tokens
    * Included NER model is very generic
    * Better results if trained with domain-specific corpus

### Others
* EntityMentionsAnnotator
    * Can pull out multi-token named entities
* Corefeference Resolution
    * Can recognize phrases that refer to the same clause / entity
* Natural Logic
    * Deep learning models to infer hypotheses from a given premise
    * Might help to "ask questions" concerning input text
 
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
    
### Final Thoughts
* Heavily influenced by noise in input data
* Many low-level decisions need to be made
* Training new models will improve inferences
* Combination of methods seems necessary
