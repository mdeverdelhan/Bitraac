package misc.web;

import javax.inject.Inject;
import misc.services.TwitterService;
import org.springframework.boot.autoconfigure.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

@Controller
public class MiscController {

    private TwitterService twitterService;
    
    @Inject
    public MiscController(TwitterService twitterService) {
        this.twitterService = twitterService;
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
}
