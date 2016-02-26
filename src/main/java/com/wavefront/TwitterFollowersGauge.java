package com.wavefront;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by evanpease on 1/16/16.
 */
public class TwitterFollowersGauge {


    private static final Logger log = Logger.getLogger(TwitterFollowersGauge.class.getName());

    public static void main(String [] args)
    throws InterruptedException, IOException {

        TwitterFactory twitterFactory = new TwitterFactory();
        Twitter twitter = twitterFactory.getInstance();
        log.log(Level.INFO, "Connected to Twitter");


        String address = args[0];
        int port = Integer.parseInt(args[1]);

        StatsdClient client = new StatsdClient(address, port);

        while(true) {

            String[] users = args[2].split(",");

            User twUser= null;
            for (int i=0;i<users.length;i++) {
                String user = users[i];
                int fcount = getFollowerCount(user,twitter);
                log.log(Level.INFO, "User " + user + " has " + fcount + " followers.");
                client.gauge("twitter.user." + user + ".followerCount", fcount);
            }
            Thread.sleep(60000);
        }
    }

    public static int getFollowerCount(String user, Twitter twitterInstance) {

        try {
            User twUser = twitterInstance.showUser(user);
            return twUser.getFollowersCount();
        } catch (TwitterException e) {
            log.log(Level.SEVERE, "Error getting follower count.", e);
        }

        return -1;

    }


}