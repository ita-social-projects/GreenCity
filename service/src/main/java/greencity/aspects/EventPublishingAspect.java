package greencity.aspects;

import greencity.annotations.EventPublishing;
import greencity.constant.ErrorMessage;
import greencity.event.CustomApplicationEvent;
import greencity.exception.exceptions.EventCreationException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Aspect used for publishing events and messages.
 */
@Aspect
@Component
public class EventPublishingAspect {
    private ApplicationEventPublisher publisher;
    private RabbitTemplate rabbitTemplate;

    /**
     * All args constructor.
     *
     * @param publisher object, used for publishing events.
     */
    @Autowired
    public EventPublishingAspect(ApplicationEventPublisher publisher,
        RabbitTemplate rabbitTemplate) {
        this.publisher = publisher;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * pointcut, that selects method, annotated with {@link EventPublishing}.
     */
    @Pointcut("@annotation(eventAnnotation)")
    public void myAnnotationPointcut(EventPublishing eventAnnotation) {
        // This method is empty because method with @Pointcut annotation should be
        // empty.
    }

    /**
     * advice, that builds and publishes events and messages.
     *
     * @param eventAnnotation annotation, that is over method, that triggered events
     *                        and message publishing.
     * @param body            object, that triggered events(message) method returns.
     */
    @AfterReturning(pointcut = "myAnnotationPointcut(eventAnnotation)", returning = "body")
    public void eventPublishingAdvice(JoinPoint jp, EventPublishing eventAnnotation, Object body) {
        if (eventAnnotation.rabbitEnabled()) {
            rabbitTemplate.convertAndSend(eventAnnotation.exchange(), eventAnnotation.routingKey(), body);
        }

        if ((body != null || eventAnnotation.isNullTriggers())) {
            for (Class<? extends CustomApplicationEvent> eventClass : eventAnnotation.eventClass()) {
                publisher.publishEvent(buildEvent(eventClass, body,
                    jp.getTarget()));
            }
        }
    }

    /**
     * Method for creating instance of event.
     *
     * @param eventClass class of needed event.
     * @param body       data, that will be put into event.
     * @param source     the object on which the event initially occurred (never
     *                   {@code null}).
     * @return instance instance of event.
     */
    private ApplicationEvent buildEvent(
        Class<? extends CustomApplicationEvent> eventClass, Object body, Object source) {
        try {
            if (body == null) {
                return eventClass.getConstructor(Object.class).newInstance(source);
            }
            return eventClass.getConstructor(Object.class, body.getClass()).newInstance(source, body);
        } catch (ReflectiveOperationException e) {
            try {
                return eventClass.getConstructor(Object.class).newInstance(source);
            } catch (ReflectiveOperationException ex) {
                throw new EventCreationException(ErrorMessage.CAN_NOT_CREATE_EVENT_INSTANCE);
            }
        }
    }
}