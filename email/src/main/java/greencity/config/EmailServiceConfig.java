package greencity.config;

import greencity.service.EmailService;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the {@link EmailService}.
 */
@Configuration
public class EmailServiceConfig {
    /**
     * Initial amount of threads for send email thread pool. It is set to 1 to
     * improve the first task in the queue handling performance.
     */
    private static final int BASE_THREADS_AMOUNT = 1;
    /**
     * The maximum amount of threads up to which send email thread pool will be
     * expanded. It is set to amount of processors due to low simultaneous task
     * submission expected but can be changed according to Brian Goetz's law when
     * needed.
     */
    private static final int MAX_THREADS_AMOUNT = Runtime.getRuntime().availableProcessors();
    /**
     * Keep alive time for idle threads. When the amount of threads in the send
     * email thread pool is higher than BASE_THREADS_AMOUNT idling threads will be
     * destroyed after this amount of time.
     */
    private static final IdleTimeout IDLE_TIMEOUT = new IdleTimeout(10, TimeUnit.SECONDS);
    /**
     * The maximum amount of tasks in the send email thread pool queue. It is set to
     * 100 due to low simultaneous task submission expected but can be changed on
     * demand.
     */
    private static final int MAX_TASKS_IN_QUEUE = 100;

    /**
     * Executor that is used for sending MIME emails in separate threads.
     *
     * @return Executor which is a cached thread pool.
     */
    @Bean
    public Executor sendEmailExecutor() {
        return new ThreadPoolExecutor(
            BASE_THREADS_AMOUNT,
            MAX_THREADS_AMOUNT,
            IDLE_TIMEOUT.getIdleTime(),
            IDLE_TIMEOUT.getIdleTimeUnit(),
            new ArrayBlockingQueue<>(MAX_TASKS_IN_QUEUE));
    }

    /**
     * This class represents the amount of time needed for idle thread destruction
     * in the send email thread pool. The main purpose of this class is to ship the
     * amount of time with the according time unit, so the type system can help us
     * not to misuse one of the parameters without any relation to the another one.
     */
    @Getter
    @RequiredArgsConstructor
    private static final class IdleTimeout {
        /**
         * Keep alive time required for thread destruction.
         */
        private final long idleTime;
        /**
         * Time unit that describes the amount of time required for thread destruction.
         */
        private final TimeUnit idleTimeUnit;
    }
}
