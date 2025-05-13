package greencity.config;

import static greencity.constant.QuartzConstants.*;
import greencity.scheduler.EcoNewsGenerationJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Quartz configuration for the EcoNews generation job.
 * <p>
 * Uses a cron expression from the configuration file and automatically fixes
 * it if both day-of-month and day-of-week are specified, which is not supported by Quartz.
 */
@Configuration
@Slf4j
public class QuartzConfig {
    @Value("${cron.sendContentToSubscribers}")
    private String cronExpression;

    @Bean
    public JobDetail ecoNewsGenerationJobDetail() {
        return JobBuilder.newJob(EcoNewsGenerationJob.class)
            .withIdentity(ECO_NEWS_GENERATION_JOB_IDENTITY)
            .storeDurably()
            .build();
    }

    @Bean
    public Trigger ecoNewsGenerationTrigger() {
        String fixedCron = fixCronExpression(cronExpression);
        try {
            return TriggerBuilder.newTrigger()
                .forJob(ecoNewsGenerationJobDetail())
                .withIdentity(ECO_NEWS_GENERATION_TRIGGER_IDENTITY)
                .withSchedule(CronScheduleBuilder.cronSchedule(fixedCron))
                .build();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to create trigger with cron: " + fixedCron, e);
        }
    }

    private String fixCronExpression(String cron) {
        String[] fields = cron.trim().split(CRON_FIELD_SPLIT_REGEX);
        if (fields.length != CRON_FIELDS_COUNT_EXPECTED) {
            throw new IllegalArgumentException(INVALID_CRON_EXPRESSION_ERROR + cron);
        }

        if (shouldFixDayOfMonth(fields)) {
            fields[CRON_FIELD_DAY_OF_MONTH_INDEX] = CRON_DAY_OF_MONTH_PLACEHOLDER;
        }

        return String.join(CRON_SPACE_SEPARATOR, fields);
    }

    private boolean shouldFixDayOfMonth(String[] fields) {
        boolean hasDayOfMonth = !fields[CRON_FIELD_DAY_OF_MONTH_INDEX].equals(CRON_DAY_OF_MONTH_PLACEHOLDER)
            && !fields[CRON_FIELD_DAY_OF_MONTH_INDEX].equals(CRON_WILDCARD);
        boolean hasDayOfWeek = !fields[CRON_FIELD_DAY_OF_WEEK_INDEX].equals(CRON_DAY_OF_MONTH_PLACEHOLDER)
            && !fields[CRON_FIELD_DAY_OF_WEEK_INDEX].equals(CRON_WILDCARD);

        return (hasDayOfMonth && hasDayOfWeek)
            || fields[CRON_FIELD_DAY_OF_MONTH_INDEX].equals(CRON_WILDCARD);
    }
}