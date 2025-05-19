package greencity.quartz;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

@RequiredArgsConstructor
public class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory {
    private final AutowireCapableBeanFactory factory;

    @NotNull
    @Override
    protected Object createJobInstance(@NotNull TriggerFiredBundle bundle) throws Exception {
        Object jobInstance = super.createJobInstance(bundle);
        factory.autowireBean(jobInstance);
        return jobInstance;
    }
}
