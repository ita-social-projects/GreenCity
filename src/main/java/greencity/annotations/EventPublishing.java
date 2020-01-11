package greencity.annotations;

import greencity.event.CustomApplicationEvent;
import greencity.event.EventMessageResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used for marking methods which triggers event publishing. Object, that marked method returns is used
 * as message of event. Methods, annotated with {@link EventPublishing} should return instances of
 * ${@link EventMessageResponse}. Event publishing executes after method execution.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventPublishing {
    /**
     * returns class of event which will be published.
     *
     * @return class of event which will be published.
     */
    Class<? extends CustomApplicationEvent> eventClass();

    /**
     * returns true if {@code null} message value in annotated method returning triggers event creating, in another case
     * returns false.
     *
     * @return true if {@code null} message value in annotated method returning triggers event creating, in
     *         another case returns false.
     */
    boolean isNullMessageTriggers() default false;
}
