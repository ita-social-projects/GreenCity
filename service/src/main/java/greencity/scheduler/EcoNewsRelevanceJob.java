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
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@DisallowConcurrentExecution
@RequiredArgsConstructor
public class EcoNewsRelevanceJob implements Job {

    @Autowired
    private AIServiceImpl aiServiceImpl;

    @Autowired
    private EcoNewsRepo ecoNewsRepo;

    @Autowired
    private EcoNewsRelevanceRepo ecoNewsRelevanceRepo;

    private static Instant lastRunTime = Instant.now().minus(3, ChronoUnit.HOURS);

    @Override
    public void execute(JobExecutionContext context) {
        Instant currentTime = Instant.now();

        List<Long> outdatedIds = ecoNewsRelevanceRepo.findOutdatedEcoNewsIds();
        List<Long> recentNewsIds = ecoNewsRepo.findIdsCreatedAfter(lastRunTime);

        Set<Long> uniqueIds = new HashSet<>();
        uniqueIds.addAll(outdatedIds);
        uniqueIds.addAll(recentNewsIds);

        aiServiceImpl.getRelevanceForEcoNewsBatch(uniqueIds);

        lastRunTime = currentTime;
    }
}
