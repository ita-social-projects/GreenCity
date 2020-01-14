package greencity.annotations;

import greencity.events.CustomApplicationEvent;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used for marking methods which triggers events publishing. Object, that marked method returns is used
 * as body of events. Event publishing executes after method execution.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventPublishing {
    /**
     * returns classes of events which will be published.
     *
     * @return class of events which will be published.
     */
    Class<? extends CustomApplicationEvent>[] eventClass();
}
