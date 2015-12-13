package com.insano10;

import com.insano10.BatchingTweetRetriever.TweetBatchCallback;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import twitter4j.Twitter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static com.insano10.Location.*;

public class XmasTwitter
{
    private static final String PROPERTY_FILE_PATH = "xmas-twitter.properties";

    private static final int BATCH_SIZE = 10;
    private static final long FREQUENCY = 10L;
    private static final String QUERY_STRING = "christmas";

    public static void main(String[] args) throws IOException
    {
        final Properties properties = getProperties(PROPERTY_FILE_PATH);

        final Twitter twitterClient = TwitterClientProvider.getTwitterClient(properties.getProperty("consumerKey"), properties.getProperty("consumerSecret"));
        final HttpClient httpClient = HttpClientBuilder.create().build();
        final TweetBatchCallback callback = getTweetBatchCallback(httpClient, properties.getProperty("deviceId"), properties.getProperty("accessToken"));

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

    private static Properties getProperties(final String propertyFilePath) throws IOException
    {
        final Properties properties = new Properties();
        properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(propertyFilePath));
        return properties;
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
