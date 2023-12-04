package greencity.config;

import org.springframework.core.task.TaskDecorator;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public class ContextAwareTaskDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(Runnable runnable) {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        return () -> {
            try {
                RequestContextHolder.setRequestAttributes(attributes);
                runnable.run();
            } finally {
                RequestContextHolder.resetRequestAttributes();
            }
        };
    }
}
