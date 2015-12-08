package com.insano10;

import twitter4j.*;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BatchingTweetRetriever
{
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private final Twitter twitterClient;
    private final TweetBatchCallback callback;
    private final String queryString;
    private final long frequency;
    private final int batchSize;

    private long currentTweetId;

    public BatchingTweetRetriever(final Twitter twitterClient, final TweetBatchCallback callback, final String queryString, final int batchSize, final long frequency)
    {
        this.twitterClient = twitterClient;
        this.queryString = queryString;
        this.frequency = frequency;
        this.batchSize = batchSize;
        this.callback = callback;
        this.currentTweetId = 0L;
    }

    public void start()
    {
        executor.scheduleAtFixedRate(() -> callback.onBatch(getNextBatch()), 0L, frequency, TimeUnit.SECONDS);
    }

    public void stop()
    {
        executor.shutdownNow();
    }

    private List<Status> getNextBatch()
    {
        try
        {
            final Query query = getQuery(batchSize, currentTweetId, queryString);
            final QueryResult result = twitterClient.search(query);
            this.currentTweetId = result.getMaxId();

            return result.getTweets();
        }
        catch (TwitterException e)
        {
            throw new RuntimeException("Failed to get tweet batch", e);
        }
    }

    private Query getQuery(final int batchSize, final long lastTweetId, final String queryString)
    {
        final Query query = new Query(queryString);
        query.setCount(batchSize);
        query.sinceId(lastTweetId);

        return query;
    }

    public interface TweetBatchCallback
    {
        void onBatch(List<Status> tweets);
    }
}
