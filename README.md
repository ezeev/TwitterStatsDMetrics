
# Twitter Stream Metrics

A set of tools for collecting metrics from Twitter's APIs.

## Requirements

- A StatsD server running somewhere accessible from this app.
- Java 1.8
- Python - tested with Python 2.7
- A Twitter account with API access and your API credentials.

## Installing & Building

1. Install dependencies: `pip install -U textblob && python -m textblob.download_corpora`
2. Create a jar: `mvn package`


## Configuring
Create a file called `twitter4j.properties` in either your current directory or root of the class path directory. See http://twitter4j.org/en/configuration.html for more info.
```
debug=true
oauth.consumerKey=YOUR_CONSUMER_KEY
oauth.consumerSecret=YOUR_CONSUMER_SECRET
oauth.accessToken=YOUR_ACCESS_TOKEN
oauth.accessTokenSecret=YOUR_ACCESS_TOKEN_SECRET
```


## Running

The jar provides 2 different command line tools.

### Tracking Follower Counts

Usage:
`java -jar twitterstatsd-1.0-SNAPSHOT.jar <StatsD server address> <StatsD server port> <Comma separated list of twitter handles>` 

Example:
`java -jar twitterstatsd-1.0-SNAPSHOT.jar localhost 8125 askplaystation,xboxsupport`

#### Output 
Every 60 seconds, it will poll the Twitter APIs and update a gauge containing the number of followers for each twitter handle. This is useful for measuring growth/decline of Twitter followers over time.

The metric name format is: `twitter.user.<twitter handle>.followerCount`

