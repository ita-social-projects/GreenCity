package greencity.config;

import org.springframework.core.task.TaskDecorator;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public class ContextAwareTaskDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(Runnable runnable) {
        return () -> {
            try {
                RequestContextHolder.setRequestAttributes(RequestContextHolder.currentRequestAttributes());
                runnable.run();
            } finally {
                RequestContextHolder.resetRequestAttributes();
            }
        };
    }
}
