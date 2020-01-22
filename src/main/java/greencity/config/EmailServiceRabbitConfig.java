package greencity.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration that is used for managing RabbitMQ-related settings.
 * It is responsible for exchanges and queues declarations as well as
 * binding them together.
 */
@Configuration
@EnableRabbit
public class EmailServiceRabbitConfig {
    @Value("${messaging.rabbit.email.topic}")
    private String emailTopicExchangeName;
    private static final String SEND_EMAIL_QUEUE = "send_email_queue";

    /**
     * Topic exchange declaration that is used for email-related queues.
     *
     * @return topic exchange for email-related messages and queues.
     */
    @Bean
    public TopicExchange emailTopicExchange() {
        return new TopicExchange(emailTopicExchangeName);
    }

    /**
     * Queue that is used for sending email-related messages.
     * It is durable since it can handle security-related messages.
     *
     * @return durable queue that is meant for sending email letters.
     */
    @Bean
    public Queue emailQueue() {
        return new Queue(SEND_EMAIL_QUEUE, true);
    }

    /**
     * The binding that is used for linking email topic exchange to email-related queues.
     * Topic pattern is extended with # symbol, so multiple words can be after topic
     * name when sending messages.
     *
     * @return Binding with topic exchange and email-related queue linked.
     */
    @Bean
    public Binding emailTopicAndQueueBinder(TopicExchange emailTopicExchange, Queue emailQueue) {
        return BindingBuilder
            .bind(emailQueue)
            .to(emailTopicExchange)
            .with(emailTopicExchangeName + "#");
    }
}
