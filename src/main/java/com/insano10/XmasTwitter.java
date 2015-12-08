package com.insano10;

import com.insano10.BatchingTweetRetriever.TweetBatchCallback;
import twitter4j.Twitter;

public class XmasTwitter
{
    private static final int BATCH_SIZE = 100;
    private static final long FREQUENCY = 5L;

    public static void main(String[] args)
    {
        final String consumerKey = getSystemProperty("consumerKey");
        final String consumerSecret = getSystemProperty("consumerSecret");

        final Twitter twitterClient = TwitterClientProvider.getTwitterClient(consumerKey, consumerSecret);
        final TweetBatchCallback callback = tweets ->
                tweets.stream().forEach(t -> System.out.println(String.format("(%s) %d: %s", t.getCreatedAt(), t.getId(), t.getText())));

        final BatchingTweetRetriever tweetRetriever = new BatchingTweetRetriever(twitterClient, callback, "christmas", BATCH_SIZE, FREQUENCY);

        tweetRetriever.start();


        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                tweetRetriever.stop();
            }
        });
    }

    private static String getSystemProperty(final String propertyKey)
    {
        final String propertyValue = System.getProperty(propertyKey);

        if(propertyValue == null)
        {
            throw new RuntimeException(String.format("You must pass in %s to the application in the form of VM arg -D%s=XXX", propertyKey, propertyKey));
        }

        return propertyValue;
    }
}
