package greencity.annotations;

import static greencity.constant.ErrorMessage.SELECT_CORRECT_LANGUAGE;
import greencity.validator.LanguageValidator;
import java.lang.annotation.*;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = LanguageValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ValidLanguage {
    /**
     * Defines the message that will be showed when the input data is not valid.
     *
     * @return message
     */
    String message() default SELECT_CORRECT_LANGUAGE;

    /**
     * Let you select to split the annotations into different groups to apply
     * different validations to each group.
     *
     * @return groups
     */
    Class<?>[] groups() default {};

    /**
     * Payloads are typically used to carry metadata information consumed by a
     * validation client.
     *
     * @return payload
     */
    Class<? extends Payload>[] payload() default {};
}
