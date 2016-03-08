package misc.services;

import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.springframework.social.twitter.api.SearchParameters;
import org.springframework.social.twitter.api.SearchResults;
import org.springframework.social.twitter.api.Trends;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.*;

@Service
public class TwitterService {

    private static final int MAX_TWEETS_COUNT = 10;
    
    private Twitter twitter;
    
    @Inject
    public TwitterService(Twitter twitter) {
        this.twitter = twitter;
    }
    
    public List<String> getTrends() {
        long woeid = 1l;
        Trends trends = twitter.searchOperations().getLocalTrends(woeid);
        return trends.getTrends().stream()
                .map(t -> t.getName())
                .collect(Collectors.toList());
    }
    
    public String getMostPopularTweetText(String name) {
        SearchParameters searchParameters = new SearchParameters(name)
                .count(MAX_TWEETS_COUNT)
                .resultType(SearchParameters.ResultType.POPULAR);
        SearchResults results = twitter.searchOperations().search(searchParameters);
        List<Tweet> tweets = results.getTweets();
        if (tweets.isEmpty()) {
            return null;
        }
        return tweets.get(0).getUnmodifiedText();
    }
    
    public void tweet() {
        // @TODO
    }
}
