package com.insano10;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterClientProvider
{
    public static Twitter getTwitterClient(String consumerKey, String consumerSecret)
    {
        try
        {
            final Twitter twitterClient = new TwitterFactory(new ConfigurationBuilder().
                    setApplicationOnlyAuthEnabled(true).
                    setDebugEnabled(false).
                    build()).
                    getInstance();

            twitterClient.setOAuthConsumer(consumerKey, consumerSecret);
            twitterClient.getOAuth2Token();

            return twitterClient;
        }
        catch (TwitterException e)
        {
            throw new RuntimeException("Failed to create twitter client", e);
        }
    }
}
