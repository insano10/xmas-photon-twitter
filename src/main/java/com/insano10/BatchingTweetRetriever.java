package com.insano10;

import org.apache.log4j.*;
import org.apache.log4j.Logger;
import twitter4j.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BatchingTweetRetriever
{
    private static final org.apache.log4j.Logger LOGGER = Logger.getLogger(BatchingTweetRetriever.class);

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private final Twitter twitterClient;
    private final TweetBatchCallback callback;
    private final String queryString;
    private final Location location;
    private final long frequency;

    private final int batchSize;
    private long currentTweetId;

    public BatchingTweetRetriever(final Twitter twitterClient, final TweetBatchCallback callback, final String queryString, final Location location, final int batchSize, final long frequency)
    {
        this.twitterClient = twitterClient;
        this.queryString = queryString;
        this.location = location;
        this.frequency = frequency;
        this.batchSize = batchSize;
        this.callback = callback;
        this.currentTweetId = 0L;
    }

    public void start()
    {
        executor.scheduleAtFixedRate(() -> callback.onBatch(location, getNextBatch()), 0L, frequency, TimeUnit.SECONDS);
    }

    public void stop()
    {
        executor.shutdownNow();
    }

    private List<Status> getNextBatch()
    {
        System.out.println(location.name() + ": running from id: " + currentTweetId);
        try
        {
            final Query query = getQuery(batchSize, currentTweetId, queryString);
            final QueryResult result = twitterClient.search(query);
            final List<Status> tweets = result.getTweets();

            Optional<Long> maxTweetId = tweets.stream().map(Status::getId).reduce(Math::max);
            maxTweetId.ifPresent(id -> currentTweetId = id);

            return tweets;
        }
        catch (TwitterException e)
        {
            //150 unauthenticated API calls per hour allowed (350 authenticated)
            LOGGER.error("Failed to get tweet batch for " + location.name(), e);
            throw new RuntimeException(e);
        }
    }

    private Query getQuery(final int batchSize, final long lastTweetId, final String queryString)
    {
        final Query query = new Query(queryString);
        query.setCount(batchSize);
        query.sinceId(lastTweetId);
        query.setGeoCode(location.getGeoLocation(), location.getRadiusKm(), Query.Unit.km);

        return query;
    }

    public interface TweetBatchCallback
    {
        void onBatch(Location location, List<Status> tweets);
    }
}
