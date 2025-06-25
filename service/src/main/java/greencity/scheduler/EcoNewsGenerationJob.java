package greencity.scheduler;

import greencity.service.AIService;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EcoNewsGenerationJob implements Job {
    private final AIService aiService;
    private boolean hasExecuted = false;
    @Value("${spring.quartz.properties.generation.languages}")
    private String[] generatedLanguages;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        if (!hasExecuted) {
            for (String language : generatedLanguages) {
                aiService.generateAndSaveEcoNews(language);
            }
            hasExecuted = true;
        }
    }
}
