package com.wavefront;

import java.io.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SentimentScorer {


    private static final Logger log = Logger.getLogger(TwitterFollowersGauge.class.getName());



    public static void sendSentimentMetric(String metricBaseName,String tweetText, StatsdClient client) {
        double sentiment = getSentiment(tweetText);

        //increment the counter gauge
        String counterMetricName = metricBaseName + ".mentions";
        //increment the total
        client.incrementGauge(counterMetricName + ".total",1);

        if (sentiment > 0) counterMetricName += ".positive";
        else if (sentiment < 0) counterMetricName += ".negative";
        else counterMetricName += ".neutral";
        log.log(Level.INFO, "Sending metric " + counterMetricName);
        client.incrementGauge(counterMetricName,1);

        //now increment sentiment score metric
        //String sentimentMetricName = metricBaseName+".sentimentScore";
        //log.log(Level.INFO, "Sending metric " + sentimentMetricName + "; sentiment score: " + sentiment);
        //client.incrementGauge(sentimentMetricName,sentiment);
    }

    public static double getSentiment(String tweetText) {

        double sentiment = 0.0;
        try {


            String s;

            ProcessBuilder pb = new ProcessBuilder("python", "src/main/py/sentiment.py","\""+tweetText+"\"");
            Process p = pb.start();

            log.log(Level.INFO,"Calculating Sentiment Scores for tweet: " + tweetText);

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));

            // read the output from the command
            while ((s = stdInput.readLine()) != null) {
                sentiment += Double.parseDouble(s);
            }

            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
                log.log(Level.SEVERE, "Error processing sentiment score from python:" + s);
            }
        }
        catch (IOException e) {
            log.log(Level.SEVERE, "Error processing sentiment:",e);
            e.printStackTrace();
        }

        return sentiment;
    }


    public static void main(String args[]) {

        String tweetText = "@amazon Welcome to the frustration of self-publishing: trying to sell lemonade in Pisstown.";

        CompletableFuture futureSentiment = CompletableFuture.supplyAsync(() -> SentimentScorer.getSentiment(tweetText));

        /*CompletableFuture futureSentiment = CompletableFuture.supplyAsync( ( ) -> {
            return getSentiment(tweetText);
        } );*/
        try {
            double sentiment = (double)futureSentiment.get();
            System.out.println("this is our sentiment:" + sentiment);

        } catch (Exception e) {
            System.out.println(e);
        }
    }

}