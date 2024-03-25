package greencity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // Set your desired core pool size
        executor.setMaxPoolSize(10); // Set your desired max pool size
        executor.setQueueCapacity(25); // Set your desired queue capacity
        executor.setThreadNamePrefix("Parallel-Thread-");
        executor.initialize();
        return executor;
    }
}
