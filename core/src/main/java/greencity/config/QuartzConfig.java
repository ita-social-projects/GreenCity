package greencity.config;

import static greencity.constant.QuartzConstants.*;
import greencity.scheduler.EcoNewsGenerationJob;
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
public class QuartzConfig {
    @Value("${cron.sendContentToSubscribers}")
    private String cronExpression;

    /**
     * Creates a JobDetail for the Quartz job.
     *
     * @return The JobDetail for EcoNewsGenerationJob, stored durably even without active triggers.
     */
    @Bean
    public JobDetail ecoNewsGenerationJobDetail() {
        return JobBuilder.newJob(EcoNewsGenerationJob.class)
            .withIdentity(ECO_NEWS_GENERATION_JOB_IDENTITY)
            .storeDurably()
            .build();
    }

    /**
     * Creates a trigger using the cron expression.
     * If the expression specifies both day-of-month (position 3) and day-of-week (position 5),
     * Quartz throws an exception because it doesn't support both being set at the same time.
     * The fixCronExpression() method automatically replaces the day-of-month with '?' in such cases.
     *
     * @return The configured cron trigger.
     */
    @Bean
    public Trigger ecoNewsGenerationTrigger() {
        String fixedCron = fixCronExpression(cronExpression);
        return TriggerBuilder.newTrigger()
            .forJob(ecoNewsGenerationJobDetail())
            .withIdentity(ECO_NEWS_GENERATION_TRIGGER_IDENTITY)
            .withSchedule(CronScheduleBuilder.cronSchedule(fixedCron))
            .build();
    }

    /**
     * Processes the provided cron expression to ensure it meets Quartz's requirements.
     * Quartz does not support both day-of-month (position 4) and day-of-week (position 6) being set simultaneously.
     * <p>
     * Quartz cron expression format:
     * {@code seconds minutes hours dayOfMonth month dayOfWeek}
     *
     * @param cron The original cron expression.
     * @return The corrected cron expression, if needed.
     */
    private String fixCronExpression(String cron) {
        String[] fields = cron.trim().split(CRON_FIELD_SPLIT_REGEX);
        if (fields.length != CRON_FIELDS_COUNT_EXPECTED) {
            throw new IllegalArgumentException(INVALID_CRON_EXPRESSION_ERROR + cron);
        }

        boolean hasDayOfMonth = !fields[CRON_FIELD_DAY_OF_MONTH_INDEX].equals(CRON_DAY_OF_MONTH_PLACEHOLDER)
            && !fields[CRON_FIELD_DAY_OF_MONTH_INDEX].equals(CRON_WILDCARD);

        boolean hasDayOfWeek = !fields[CRON_FIELD_DAY_OF_WEEK_INDEX].equals(CRON_DAY_OF_MONTH_PLACEHOLDER)
            && !fields[CRON_FIELD_DAY_OF_WEEK_INDEX].equals(CRON_WILDCARD);

        if (hasDayOfMonth && hasDayOfWeek) {
            fields[CRON_FIELD_DAY_OF_MONTH_INDEX] = CRON_DAY_OF_MONTH_PLACEHOLDER;
        }

        if (fields[CRON_FIELD_DAY_OF_MONTH_INDEX].equals(CRON_WILDCARD)
            && fields[CRON_FIELD_DAY_OF_WEEK_INDEX].equals(CRON_WILDCARD))
        {
            fields[CRON_FIELD_DAY_OF_MONTH_INDEX] = CRON_DAY_OF_MONTH_PLACEHOLDER;
        }
        return String.join(CRON_SPACE_SEPARATOR, fields);
    }
}