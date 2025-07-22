package greencity.config;

import greencity.constant.QuartzConstants;
import greencity.logging.LoggingJobListener;
import greencity.exception.exceptions.InvalidCronException;
import greencity.exception.exceptions.TriggerException;
import greencity.scheduler.EcoNewsGenerationJob;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

/**
 * Quartz configuration for the EcoNews generation job.
 *
 * <p>
 * Uses a cron expression from the configuration file and automatically fixes it
 * if both day-of-month and day-of-week are specified, which is not supported by
 * Quartz.
 * </p>
 */
@Configuration
@RequiredArgsConstructor
public class QuartzConfig {
    @Value("${cron.generateEcoNews}")
    private String cronExpression;
    private final ApplicationContext applicationContext;

    @Bean
    public SchedulerFactoryBeanCustomizer customizer(SpringBeanJobFactory jobFactory) {
        return factory -> factory.setJobFactory(jobFactory);
    }

    @Bean
    public SpringBeanJobFactory springBeanJobFactory(AutowireCapableBeanFactory beanFactory) {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory(beanFactory);
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean
    public Scheduler scheduler(SchedulerFactoryBean factoryBean,
        Trigger ecoNewsGenerationTrigger,
        JobDetail ecoNewsGenerationJobDetail) throws SchedulerException {
        Scheduler scheduler = factoryBean.getScheduler();
        scheduler.getListenerManager().addJobListener(new LoggingJobListener());

        if (!scheduler.checkExists(ecoNewsGenerationJobDetail.getKey())) {
            scheduler.scheduleJob(ecoNewsGenerationJobDetail, ecoNewsGenerationTrigger);
        } else if (!scheduler.checkExists(ecoNewsGenerationTrigger.getKey())) {
            scheduler.rescheduleJob(ecoNewsGenerationTrigger.getKey(), ecoNewsGenerationTrigger);
        }

        return scheduler;
    }

    @Bean
    public JobDetail ecoNewsGenerationJobDetail() {
        return JobBuilder.newJob(EcoNewsGenerationJob.class)
            .withIdentity(QuartzConstants.ECO_NEWS_GENERATION_JOB_IDENTITY)
            .storeDurably()
            .build();
    }

    @Bean
    public Trigger ecoNewsGenerationTrigger(JobDetail ecoNewsGenerationJobDetail) {
        String fixedCron = fixCronExpression(cronExpression);
        try {
            return TriggerBuilder.newTrigger()
                .forJob(ecoNewsGenerationJobDetail)
                .withIdentity(QuartzConstants.ECO_NEWS_GENERATION_TRIGGER_IDENTITY)
                .withSchedule(CronScheduleBuilder.cronSchedule(fixedCron))
                .build();
        } catch (RuntimeException e) {
            throw new TriggerException(QuartzConstants.CREATION_CRON_FAILED_MESSAGE + fixedCron, e);
        }
    }

    private String fixCronExpression(String cron) {
        String[] fields = cron.trim().split(QuartzConstants.CRON_FIELD_SPLIT_REGEX);
        if (fields.length != QuartzConstants.CRON_FIELDS_COUNT_EXPECTED) {
            throw new InvalidCronException(QuartzConstants.INVALID_CRON_EXPRESSION_ERROR + cron);
        }

        if (shouldFixDayOfMonth(fields)) {
            fields[QuartzConstants.CRON_FIELD_DAY_OF_MONTH_INDEX] = QuartzConstants.CRON_DAY_OF_MONTH_PLACEHOLDER;
        }

        return String.join(QuartzConstants.CRON_SPACE_SEPARATOR, fields);
    }

    private boolean shouldFixDayOfMonth(String[] fields) {
        boolean hasDayOfMonth = !fields[QuartzConstants.CRON_FIELD_DAY_OF_MONTH_INDEX]
            .equals(QuartzConstants.CRON_DAY_OF_MONTH_PLACEHOLDER)
            && !fields[QuartzConstants.CRON_FIELD_DAY_OF_MONTH_INDEX].equals(QuartzConstants.CRON_WILDCARD);
        boolean hasDayOfWeek = !fields[QuartzConstants.CRON_FIELD_DAY_OF_WEEK_INDEX]
            .equals(QuartzConstants.CRON_DAY_OF_MONTH_PLACEHOLDER)
            && !fields[QuartzConstants.CRON_FIELD_DAY_OF_WEEK_INDEX].equals(QuartzConstants.CRON_WILDCARD);

        return (hasDayOfMonth && hasDayOfWeek)
            || fields[QuartzConstants.CRON_FIELD_DAY_OF_MONTH_INDEX].equals(QuartzConstants.CRON_WILDCARD);
    }

    @RequiredArgsConstructor
    private static class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory {
        private final AutowireCapableBeanFactory factory;

        @NotNull
        @Override
        protected Object createJobInstance(@NotNull TriggerFiredBundle bundle) throws Exception {
            Object jobInstance = super.createJobInstance(bundle);
            factory.autowireBean(jobInstance);
            return jobInstance;
        }
    }
}