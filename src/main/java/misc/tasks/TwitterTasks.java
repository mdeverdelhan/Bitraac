package misc.tasks;

import java.util.Date;
import javax.inject.Inject;
import misc.model.TrendDesign;
import misc.repositories.TrendDesignRepository;
import misc.services.TwitterService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.*;

@Component
@EnableScheduling
public class TwitterTasks {

    @Inject
    private TrendDesignRepository trendDesignRepository;
    
    @Inject
    private TwitterService twitterService;
    
    @Scheduled(fixedDelay = 5000)
    void storeTrends() {
        String trendName = twitterService.getTrends().get(0);
        String tweetText = twitterService.getMostPopularTweetText(trendName);
        TrendDesign newDesign = new TrendDesign();
        newDesign.setName(trendName);
        newDesign.setText(tweetText);
        newDesign.setCreatedDate(new Date());
        trendDesignRepository.save(newDesign);
        System.out.println("design count: " + trendDesignRepository.count());
    }
    
}
