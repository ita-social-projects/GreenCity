package greencity.config;

import greencity.scheduler.EcoNewsGenerationJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {
    @Bean
    public JobDetail ecoNewsGenerationJobDetail() {
        return JobBuilder.newJob(EcoNewsGenerationJob.class)
            .withIdentity("ecoNewsGenerationJob")
            .storeDurably()
            .build();
    }

    @Bean
    public Trigger ecoNewsGenerationTrigger() {
        return TriggerBuilder.newTrigger()
            .forJob(ecoNewsGenerationJobDetail())
            .withIdentity("ecoNewsGenerationTrigger")
            .withSchedule(CronScheduleBuilder.cronSchedule("0 0 20 ? * SAT"))
            .build();
    }
}