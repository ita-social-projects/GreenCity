package greencity.logging;

import static greencity.constant.QuartzConstants.*;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

@Slf4j
public class LoggingJobListener implements JobListener {
    @Override
    public String getName() {
        return JOB_LISTENER_NAME;
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        Date previousFireTime = context.getTrigger().getPreviousFireTime();
        Date nextFireTime = context.getTrigger().getNextFireTime();

        log.info(JOB_EXECUTION_START, context.getJobDetail().getKey());
        log.info(PREVIOUS_FIRE_TIME, previousFireTime != null ? previousFireTime : NONE_EXECUTION);
        log.info(NEXT_FIRE_TIME, nextFireTime != null ? nextFireTime : NONE_EXECUTION);
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        String jobName = context.getJobDetail().getKey().toString();
        String fireTime = context.getFireTime().toString();
        String scheduledFireTime = context.getScheduledFireTime().toString();

        log.warn(JOB_EXECUTION_VETOED, jobName, fireTime, scheduledFireTime);
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException e) {
        if (e != null) {
            log.error(JOB_EXECUTION_FAILED, context.getJobDetail().getKey(), e);
        } else {
            log.info(JOB_EXECUTION_SUCCESS, context.getJobDetail().getKey());
        }
    }
}
