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
//    private final AcceptLanguageDisplayService acceptLanguageDisplayService;
    private boolean hasExecuted = false;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        if (!hasExecuted) {
            String language = "uk";
            aiService.generateEcoNews(language);
            hasExecuted = true;
        }
    }
}
