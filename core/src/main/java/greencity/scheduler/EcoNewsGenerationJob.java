package greencity.scheduler;

import greencity.service.AIService;
import greencity.service.AcceptLanguageDisplayService;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EcoNewsGenerationJob implements Job {
    private final AIService aiService;
    private final AcceptLanguageDisplayService acceptLanguageDisplayService;
    private boolean hasExecuted = false;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        if (!hasExecuted) {
            String language = acceptLanguageDisplayService.resolveLanguage();
            if (language != null && !language.isEmpty()) {
                aiService.generateEcoNewsBasedOnHabits(language);
                hasExecuted = true;
            }
        }
    }
}
