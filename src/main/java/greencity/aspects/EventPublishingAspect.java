package greencity.aspects;

import greencity.annotations.EventPublishing;
import greencity.constant.ErrorMessage;
import greencity.event.CustomApplicationEvent;
import greencity.event.EventMessageResponse;
import greencity.exception.exceptions.EventCreationException;
import java.lang.reflect.InvocationTargetException;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Aspect used for publishing event.
 */
@Aspect
@Component
public class EventPublishingAspect {
    private ApplicationEventPublisher publisher;

    /**
     * All args constructor.
     *
     * @param publisher object, used for publishing event.
     */
    @Autowired
    public EventPublishingAspect(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    /**
     * pointcut, that selects method, annotated with {@link EventPublishing}.
     */
    @Pointcut("@annotation(eventAnnotation)")
    public void myAnnotationPointcut(EventPublishing eventAnnotation) {
        // This method is empty because method with @Pointcut annotation should be empty.
    }

    /**
     * advice, that builds and publishes event.
     *
     * @param eventAnnotation      annotation, that is over method, that triggered event publishing.
     * @param eventMessageResponse object, that triggered event method returns.
     *                             When {@link EventMessageResponse#message} is {@code null}. Event will not be
     *                             triggered.
     */
    @AfterReturning(pointcut = "myAnnotationPointcut(eventAnnotation)", returning = "eventMessageResponse")
    public void eventPublishingAdvice(EventPublishing eventAnnotation, EventMessageResponse eventMessageResponse) {
        if (checkMessage(eventMessageResponse) || eventAnnotation.isNullMessageTriggers()) {
            publisher.publishEvent(buildEvent(eventAnnotation.eventClass(), eventMessageResponse.getMessage(),
                eventMessageResponse.getSource()));
        }
    }

    /**
     * Method for creating instance of event.
     *
     * @param eventClass class of needed event.
     * @param message    data, that will be put into event.
     * @param source     the object on which the event initially occurred (never {@code null}).
     * @return instance instance of event.
     */
    private ApplicationEvent buildEvent(
        Class<? extends CustomApplicationEvent> eventClass, Object message, Object source) {
        try {
            return eventClass.getConstructor(Object.class, message.getClass()).newInstance(source, message);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException
            | NoSuchMethodException e) {
            throw new EventCreationException(ErrorMessage.CAN_NOT_CREATE_EVENT_INSTANCE);
        }
    }

    /**
     * Method, that checks whether event should be published.
     *
     * @return {@code true} if event should be published, in another case - {@code false}.
     */
    private boolean checkMessage(EventMessageResponse eventMessageResponse) {
        return eventMessageResponse.getMessage() != null;
    }
}