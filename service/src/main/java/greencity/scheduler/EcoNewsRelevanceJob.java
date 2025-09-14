package greencity.scheduler;

import greencity.repository.EcoNewsRelevanceRepo;
import greencity.repository.EcoNewsRepo;
import greencity.service.AIServiceImpl;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@Component
@DisallowConcurrentExecution
@RequiredArgsConstructor
public class EcoNewsRelevanceJob implements Job {
    private static ZonedDateTime lastRunTime;

    static {
        updateLastRunTime();
    }

    private final AIServiceImpl aiServiceImpl;
    private final EcoNewsRepo ecoNewsRepo;
    private final EcoNewsRelevanceRepo ecoNewsRelevanceRepo;

    @Override
    public void execute(JobExecutionContext context) {
        EcoNewsRelevanceJob.updateLastRunTime();

        List<Long> outdatedIds = ecoNewsRelevanceRepo.findOutdatedEcoNewsIds();
        List<Long> recentNewsIds = ecoNewsRepo.findIdsCreatedAfter(lastRunTime);

        log.info("Found {} news after {} date", recentNewsIds.size(), lastRunTime.toString());

        aiServiceImpl.updateRelevanceBatch(outdatedIds);
        aiServiceImpl.insertRelevanceBatch(recentNewsIds);
    }

    private static void updateLastRunTime() {
        lastRunTime = ZonedDateTime.now(ZoneId.of("UTC")).minusHours(3);
    }
}
