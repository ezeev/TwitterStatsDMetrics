
# Twitter Stream Metrics

A set of tools for collecting metrics from Twitter's APIs. This code was used to help create the blog post at https://blogs.wavefront.com/2016/02/13/detecting-service-issues-from-twitter-with-wavefront/.

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

### Tracking Mentions and Sentiment
Usage:

`java -cp twitterstatsd-1.0-SNAPSHOT.jar com.wavefront.TwitterMentionsCounter <StatsD server address> <StatsD server port> <Comma separated list of search terms>`

Example:

`java -cp twitterstatsd-1.0-SNAPSHOT.jar com.wavefront.TwitterMentionsCounter localhost 8125 @askplaystation,@xboxsupport`

The search terms are sent as separate tracks to the Twitter streaming API. See https://dev.twitter.com/streaming/overview/request-parameters#track for more information. Search terms can be user IDs (starting with "@") or regular search terms.

#### Output

As matching tweets flow through the streaming API, gauges are generated and incremented for each track:

The format is: 
- `twitter.<track>.mentions.positive`
- `twitter.<track>.mentions.negative`
- `twitter.<track>.mentions.neutral`
- `twitter.<track>.mentions.total`

If the track is a user ID, `<track>` will be replaced with `user.<user ID>`.

### Querying in Wavefront

There are multiple ways you could represent the data in a chart. For the blog post, the rate of negative tweets was subtracted from the rate of positive tweets. A moving average was used to _smooth_ the data. For example:
 
 ```
 mavg(10m,rate(ts("stats.gauges.twitter.user.askplaystation.mentions.positive"))
  - rate(ts("stats.gauges.twitter.user.askplaystation.mentions.negative")))
 ```
 

