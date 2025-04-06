package greencity.scheduler;

import greencity.service.AIService;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EcoNewsGenerationJob implements Job {
    private final AIService aiService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        String language = jobExecutionContext.getMergedJobDataMap().getString("language");
        aiService.generateEcoNewsBasedOnHabits(language);
    }
}
