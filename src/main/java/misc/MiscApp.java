package misc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.Bean;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;

@SpringBootApplication // same as @Configuration @EnableAutoConfiguration @ComponentScan
public class MiscApp {

    /**
     * Needed to give the right Twitter configuration to the scheduled tasks.
     * @param appId the Twitter application ID
     * @param appSecret the Twitter application secret key
     * @return the Twitter bean
     */
    @Bean
    public Twitter twitter(final @Value("${spring.social.twitter.appId}") String appId,
            final @Value("${spring.social.twitter.appSecret}") String appSecret) {
        // Twitter account configuration
        return new TwitterTemplate(appId, appSecret);
    }
    
    public static void main(String[] args) throws Exception {
        SpringApplication.run(MiscApp.class, args);
    }
}
