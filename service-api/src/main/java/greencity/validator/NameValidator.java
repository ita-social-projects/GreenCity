package greencity.validator;

import greencity.annotations.ValidName;
import greencity.constant.ValidationConstants;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NameValidator implements ConstraintValidator<ValidName, String> {
    private static final String PATTERN = ValidationConstants.USERNAME_REGEXP;

    @Override
    public void initialize(ValidName constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
        if (name == null || name.isEmpty() || name.length() > 30) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("name must have no less than 1 and no more than 30 symbols")
                .addConstraintViolation();
            return false;
        }

        if (!name.matches(PATTERN) || name.startsWith(".") || name.endsWith(".") || name.contains("..")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "name must contain only \"ЁёІіЇїҐґЄєА-Яа-яA-Za-z-'0-9 .\", dot can only be in the center of the name")
                .addConstraintViolation();
            return false;
        }
        return true;
    }
}
