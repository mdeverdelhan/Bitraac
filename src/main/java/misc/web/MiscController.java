package misc.web;

import javax.inject.Inject;
import misc.services.ShirtService;
import misc.services.TwitterService;
import org.springframework.boot.autoconfigure.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

@Controller
public class MiscController {

    private TwitterService twitterService;
    
    private ShirtService shirtService;
    
    @Inject
    public MiscController(TwitterService twitterService, ShirtService shirtService) {
        this.twitterService = twitterService;
        this.shirtService = shirtService;
    }
    
    @RequestMapping("/")
    @ResponseBody
    String home() {
        return "Hello World!";
    }
    
    @RequestMapping("/trend")
    @ResponseBody
    String trend() {
        return twitterService.getTrends().get(0);
    }
    
    @RequestMapping("/besttweet")
    @ResponseBody
    String besttweet() {
        String trendName = twitterService.getTrends().get(0);
        String tweetText = twitterService.getMostPopularTweetText(trendName);
        return tweetText;
    }
    
    @RequestMapping("/shirt")
    @ResponseBody
    String shirt() {
        return shirtService.category();
    }
    
    
}
