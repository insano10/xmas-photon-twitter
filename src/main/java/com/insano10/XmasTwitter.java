package com.insano10;

import com.insano10.BatchingTweetRetriever.TweetBatchCallback;
import twitter4j.GeoLocation;
import twitter4j.Twitter;

import static com.insano10.Location.*;

public class XmasTwitter
{
    private static final int BATCH_SIZE = 10;
    private static final long FREQUENCY = 10L;
    private static final String QUERY_STRING = "christmas";

    public static void main(String[] args)
    {
        final String consumerKey = getSystemProperty("consumerKey");
        final String consumerSecret = getSystemProperty("consumerSecret");

        final Twitter twitterClient = TwitterClientProvider.getTwitterClient(consumerKey, consumerSecret);
        final TweetBatchCallback callback = (loc, tweets) ->
                tweets.stream().forEach(t -> System.out.println(String.format("[%s] - (%s) %d: %s", loc.name(), t.getCreatedAt(), t.getId(), t.getText())));

        createAndStartTweetRetriever(twitterClient, callback, NORTH_LONDON);
        createAndStartTweetRetriever(twitterClient, callback, SOUTH_LONDON);
        createAndStartTweetRetriever(twitterClient, callback, EAST_LONDON);
        createAndStartTweetRetriever(twitterClient, callback, WEST_LONDON);
        createAndStartTweetRetriever(twitterClient, callback, CENTRAL_LONDON);
    }

    private static void createAndStartTweetRetriever(Twitter twitterClient, TweetBatchCallback callback, final Location location)
    {
        final BatchingTweetRetriever tweetRetriever = new BatchingTweetRetriever(twitterClient, callback, QUERY_STRING, location, BATCH_SIZE, FREQUENCY);

        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                tweetRetriever.stop();
            }
        });

        tweetRetriever.start();
    }

    private static String getSystemProperty(final String propertyKey)
    {
        final String propertyValue = System.getProperty(propertyKey);

        if (propertyValue == null)
        {
            throw new RuntimeException(String.format("You must pass in %s to the application in the form of VM arg -D%s=XXX", propertyKey, propertyKey));
        }

        return propertyValue;
    }
}
