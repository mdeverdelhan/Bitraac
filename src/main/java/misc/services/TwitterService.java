package misc.services;

import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.springframework.social.twitter.api.Trends;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.*;

@Service
public class TwitterService {

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
}
