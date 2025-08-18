package greencity.scheduler;

import greencity.repository.EcoNewsRelevanceRepo;
import greencity.repository.EcoNewsRepo;
import greencity.service.AIServiceImpl;
import lombok.RequiredArgsConstructor;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@DisallowConcurrentExecution
@RequiredArgsConstructor
public class EcoNewsRelevanceJob implements Job {

    private final AIServiceImpl aiServiceImpl;
    private final EcoNewsRepo ecoNewsRepo;
    private final EcoNewsRelevanceRepo ecoNewsRelevanceRepo;

    private static ZonedDateTime lastRunTime = ZonedDateTime.now().minusHours(3);

    @Override
    public void execute(JobExecutionContext context) {
        ZonedDateTime currentTime = ZonedDateTime.now();

        List<Long> outdatedIds = ecoNewsRelevanceRepo.findOutdatedEcoNewsIds();
        List<Long> recentNewsIds = ecoNewsRepo.findIdsCreatedAfter(lastRunTime);

        aiServiceImpl.updateRelevanceBatch(outdatedIds);
        aiServiceImpl.insertRelevanceBatch(recentNewsIds);

        lastRunTime = currentTime;
    }
}
