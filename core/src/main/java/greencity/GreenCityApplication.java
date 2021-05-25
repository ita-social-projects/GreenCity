package greencity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class GreenCityApplication {
    /**
     * Main method of SpringBoot app.
     */
    public static void main(String[] args) {
        SpringApplication.run(GreenCityApplication.class, args);
    }
}
