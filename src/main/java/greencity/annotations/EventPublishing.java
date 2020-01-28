package greencity.annotations;

import greencity.event.CustomApplicationEvent;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationEvent;

/**
 * Annotation used for marking methods which triggers events publishing. Object, that marked method returns is used
 * as body of events. Event publishing executes after method execution.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventPublishing {
    /**
     * returns classes of events which will be published. If array is empty {@link ApplicationEvent}
     * will not be published.
     *
     * @return class of events which will be published.
     */
    Class<? extends CustomApplicationEvent>[] eventClass() default {};

    /**
     * returns whether null value as a method returning triggers event publishing.
     *
     * @return whether null value as a method returning triggers event publishing.
     */
    boolean isNullTriggers() default false;

    /**
     * defines whether method invocation triggers {@link RabbitTemplate#convertAndSend(String, String, Object)} with
     * returning object as a body.
     *
     * @return {@code true} if method invocation triggers {@link RabbitTemplate#convertAndSend(String, String, Object)}
     *          and {@code false} if it doesn't trigger.
     */
    boolean rabbitEnabled() default false;

    /**
     * exchange used for {@link RabbitTemplate#convertAndSend(String, String, Object)}. Ignored when
     * {@link EventPublishing#rabbitEnabled()} is {@code false}.
     *
     * @return exchange used for {@link RabbitTemplate#convertAndSend(String, String, Object)}.
     */
    String exchange() default "";

    /**
     * routing key used for {@link RabbitTemplate#convertAndSend(String, String, Object)}. Ignored when
     * {@link EventPublishing#rabbitEnabled()} is {@code false}.
     *
     * @return routing key used for {@link RabbitTemplate#convertAndSend(String, String, Object)}.
     */
    String routingKey() default "";
}
