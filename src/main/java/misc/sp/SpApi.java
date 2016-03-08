
package misc.sp;

import java.util.List;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

public class SpApi {
    
    private static final String BASE_URL = "https://api.scalablepress.com/v2/";
    
    private String apiKey;
    
    private RestOperations restOperations = new RestTemplate();

    public SpApi(String apiKey) {
        this.apiKey = apiKey;
    }

    public Category getCategory(String name) {
        return restOperations.getForObject(BASE_URL + "/categories/" + name, Category.class);
    }
    
    public Design createDesign() {
        // @TODO
        return null;
    }
}
