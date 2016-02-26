package misc;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;

@SpringBootApplication // same as @Configuration @EnableAutoConfiguration @ComponentScan
public class MiscApp {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(MiscApp.class, args);
    }
}
