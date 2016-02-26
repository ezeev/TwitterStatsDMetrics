
package com.wavefront;

import twitter4j.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by evanpease on 1/16/16.
 */
public final class TwitterMentionsCounter {
    /**
     * Main entry of this application.
     *
     * @param args follow(comma separated user ids) track(comma separated filter terms)
     * @throws TwitterException when Twitter service or network is unavailable
     */

    private static final Logger log = Logger.getLogger(TwitterMentionsCounter.class.getName());

    public static void main(String[] args) throws TwitterException, IOException {

        if (args.length < 1) {
            System.out.println("Usage: java com.wavefront.TwitterMentionsCounter track(comma separated filter terms) metricName");
            System.exit(-1);
        }

        ArrayList<String> track = new ArrayList<String>();
        track.addAll(Arrays.asList(args[2].split(",")));

        String address = args[0];
        int port = Integer.parseInt(args[1]);

        StatsdClient client = new StatsdClient(address, port);

        StatusListener listener = new StatusListener() {

            int mentions = 0;

            @Override
            public void onStatus(Status status) {
                mentions++;

                //which track did it match?
                for (int i=0;i<track.size();i++) {
                    String strTrack = track.get(i);
                    if (status.getText().toLowerCase().contains(strTrack)) {
                        String metricBaseName = "twitter.";
                        //the track is a user handle
                        if (strTrack.contains("@")) {
                            metricBaseName += "user." + strTrack.replace("@","");
                        } else {
                            //track is not a username
                            metricBaseName += "track." + strTrack;
                        }
                        //client.incrementGauge(metricName,1);
                        System.out.println(mentions);
                        log.log(Level.INFO, metricBaseName + " has " + mentions + " mentions.");
                        final String sentMetricBaseName = metricBaseName;
                        //calculate the sentiment of the tweet asynchly
                        CompletableFuture sentimentJob = CompletableFuture.runAsync(() -> SentimentScorer.sendSentimentMetric(sentMetricBaseName,status.getText(),client));
                    }
                }
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
               log.log(Level.INFO, "Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                log.log(Level.INFO, "Got track limitation notice:" + numberOfLimitedStatuses);
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                log.log(Level.INFO, "Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            @Override
            public void onStallWarning(StallWarning warning) {
                log.log(Level.SEVERE, "Got stall warning:" + warning);
            }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(listener);

        String[] trackArray = track.toArray(new String[track.size()]);

        twitterStream.filter(new FilterQuery(0, null,trackArray));
    }

}