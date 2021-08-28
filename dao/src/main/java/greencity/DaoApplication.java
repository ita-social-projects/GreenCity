package greencity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DaoApplication {
    /**
     * Main method of SpringBoot dao module.
     */
    public static void main(String[] args) {
        SpringApplication.run(DaoApplication.class, args);
    }
}
