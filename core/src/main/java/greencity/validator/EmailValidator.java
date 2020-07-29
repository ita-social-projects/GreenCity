package greencity.validator;

import greencity.annotations.ValidEmailConstraint;
import greencity.security.dto.ownsecurity.OwnSignUpDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator<ValidEmailConstraint, OwnSignUpDto> {
    public static final Pattern EMAIL =
            Pattern.compile("^[a-zA-Z0-9._%+-]+@(?!.*?\\.\\.)[^@]+[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");

    @Override
    public void initialize(ValidEmailConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(OwnSignUpDto value, ConstraintValidatorContext context) {
        String email = value.getEmail();
        Matcher matcher = EMAIL.matcher(email);
        return matcher.find();
    }
}