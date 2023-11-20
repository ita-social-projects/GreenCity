package greencity.annotations;

import greencity.enums.TypeOfEmailNotification;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotificationType {
    TypeOfEmailNotification type();
}
