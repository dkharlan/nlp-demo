## First Steps
* Decided to use Clojure for rapid prototyping
* Used PDFBox to extract text
* Decided to focus on shallow techniques -- deep learning requires more expertise and time for training
* Used Stanford CoreNLP for P.O.S. tagging and NER

## Experimentation
### Dependency graphs
* Played with dependency graphs, but challenging to pull meaning

### N-Grams
* Found [jconwell/CoreNlp](https://github.com/jconwell/coreNlp) for removing stopwords
* Separated words into trigrams
* Start symbol or no start symbol?
* Bigram prob P(Wi | Wi-1) = count(Wi-1,Wi) / count(Wi-1)
* Store probs at log p rather than p -- avoids underflow, faster
