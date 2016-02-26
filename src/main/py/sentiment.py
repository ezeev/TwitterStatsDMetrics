

from textblob import TextBlob

import sys

text = sys.argv[1]

s = text.decode('utf-8')

blob = TextBlob(s)

for sentence in blob.sentences:
    print(sentence.sentiment.polarity)