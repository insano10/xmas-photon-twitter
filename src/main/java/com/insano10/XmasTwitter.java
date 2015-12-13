package com.insano10;

import com.insano10.BatchingTweetRetriever.TweetBatchCallback;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import twitter4j.Status;
import twitter4j.Twitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        final HttpClient httpClient = HttpClientBuilder.create().build();
        final TweetBatchCallback callback = getTweetBatchCallback(httpClient, "notTheId", "notTheToken");

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

    private static TweetBatchCallback getTweetBatchCallback(final HttpClient httpClient, final String deviceId, final String accessToken)
    {
        return (location, tweets) -> {

            tweets.stream().forEach(t ->
            {
                System.out.println(String.format("[%s] - (%s) %d: %s", location.name(), t.getCreatedAt(), t.getId(), t.getText()));
                try
                {
                    final HttpPost postTweets = new HttpPost("https://api.particle.io/v1/devices/" + deviceId + "/tweet");

                    List<NameValuePair> urlParameters = new ArrayList<>();
                    urlParameters.add(new BasicNameValuePair("access_token", accessToken));
                    urlParameters.add(new BasicNameValuePair("args", t.getText()));
                    postTweets.setEntity(new UrlEncodedFormEntity(urlParameters));

                    httpClient.execute(postTweets);
                }
                catch (IOException e)
                {
                    throw new RuntimeException("Failed to send tweets to Photon", e);
                }
            });
        };
    }
}
