package misc.services;

import javax.inject.Inject;
import misc.model.Category;
import org.springframework.stereotype.*;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

@Service
public class ShirtService {

    // @TODO Why not injection?
    private RestOperations restOperations = new RestTemplate();
    
    public String category() {
        Category c = restOperations.getForObject("https://api.scalablepress.com/v2/categories/sweatshirts", Category.class);
        return c.getType();
    }
    
}
