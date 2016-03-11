package misc.web;

import javax.inject.Inject;
import misc.model.TrendDesign;
import misc.services.ShirtService;
import misc.services.TwitterService;
import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MiscController {

    @Inject
    private TwitterService twitterService;
    
    @Inject
    private ShirtService shirtService;
    
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
    
    @RequestMapping(value="/design", method=RequestMethod.GET)
    public String trendDesignForm(Model model) {
        model.addAttribute("trendDesign", new TrendDesign());
        return "design";
    }
    
    @RequestMapping(value="/design", method=RequestMethod.POST)
    public String trendDesignSubmit(@ModelAttribute TrendDesign trendDesign, Model model) {
        model.addAttribute("trendDesign", trendDesign);
        return "result";
    }
}
