package greencity.config;

import greencity.aspect.LoggingJobListener;
import static greencity.constant.QuartzConstants.*;
import greencity.exception.exceptions.InvalidCronException;
import greencity.exception.exceptions.TriggerException;
import greencity.quartz.AutowiringSpringBeanJobFactory;
import greencity.scheduler.EcoNewsGenerationJob;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

/**
 * Quartz configuration for the EcoNews generation job.
 * <p>
 * Uses a cron expression from the configuration file and automatically fixes
 * it if both day-of-month and day-of-week are specified, which is not supported by Quartz.
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class QuartzConfig {
    @Value("${cron.sendContentToSubscribers}")
    private String cronExpression;
    private final DataSource dataSource;
    private final ApplicationContext applicationContext;

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(SpringBeanJobFactory jobFactory) {
        SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setJobFactory(jobFactory);
        return factoryBean;
    }

    @Bean
    public SpringBeanJobFactory springBeanJobFactory(AutowireCapableBeanFactory beanFactory) {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory(beanFactory);
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }


    @Bean
    public Scheduler scheduler(SchedulerFactoryBean factoryBean) throws SchedulerException {
        Scheduler scheduler = factoryBean.getScheduler();
        scheduler.getListenerManager().addJobListener(new LoggingJobListener());
        return scheduler;
    }


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
            throw new TriggerException(CREATION_CRON_FAILED_MESSAGE + fixedCron, e);
        }
    }

    private String fixCronExpression(String cron) {
        String[] fields = cron.trim().split(CRON_FIELD_SPLIT_REGEX);
        if (fields.length != CRON_FIELDS_COUNT_EXPECTED) {
            throw new InvalidCronException(INVALID_CRON_EXPRESSION_ERROR + cron);
        }

        if (shouldFixDayOfMonth(fields)) {
            fields[CRON_FIELD_DAY_OF_MONTH_INDEX] = CRON_DAY_OF_MONTH_PLACEHOLDER;
        }

        return String.join(CRON_SPACE_SEPARATOR, fields);
    }

    private boolean shouldFixDayOfMonth(String[] fields) {
        boolean hasDayOfMonth = !fields[CRON_FIELD_DAY_OF_MONTH_INDEX]
            .equals(CRON_DAY_OF_MONTH_PLACEHOLDER)
            && !fields[CRON_FIELD_DAY_OF_MONTH_INDEX].equals(CRON_WILDCARD);
        boolean hasDayOfWeek = !fields[CRON_FIELD_DAY_OF_WEEK_INDEX]
            .equals(CRON_DAY_OF_MONTH_PLACEHOLDER)
            && !fields[CRON_FIELD_DAY_OF_WEEK_INDEX].equals(CRON_WILDCARD);

        return (hasDayOfMonth && hasDayOfWeek)
            || fields[CRON_FIELD_DAY_OF_MONTH_INDEX].equals(CRON_WILDCARD);
    }
}