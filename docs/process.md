## First Steps
* Decided to use Clojure for rapid prototyping
* Used PDFBox to extract text
* Decided to focus on shallow techniques -- deep learning requires more expertise and time for training
* Used Stanford CoreNLP for P.O.S. tagging and NER

## Experimentation
### Dependency Parsing
* Played with dependency graphs, but challenging to pull meaning -- decided to skip

### Sentiment Analysis
* Can recognize positive / negative connotations
* CoreNLP uses deep learning to improve on typical approach
* Related idea, but probably needs domain-specific tuning

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
* Found [jconwell/CoreNlp](https://github.com/jconwell/coreNlp) for removing stopwords
* Separated words into trigrams
* Start symbol or no start symbol?
* Bigram prob P(Wi | Wi-1) = count(Wi-1,Wi) / count(Wi-1)
* Store probs at log p rather than p -- avoids underflow, faster 
