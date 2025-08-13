package greencity.scheduler;

import greencity.service.AIService;
import lombok.RequiredArgsConstructor;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@DisallowConcurrentExecution
public class EcoNewsGenerationJob implements Job {
    private final AIService aiService;
    @Value("${spring.quartz.properties.generation.languages}")
    private String[] generatedLanguages;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        for (String language : generatedLanguages) {
            aiService.generateAndSaveEcoNews(language);
        }
    }
}
