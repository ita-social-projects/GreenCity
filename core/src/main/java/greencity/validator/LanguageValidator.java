package greencity.validator;

import greencity.annotations.ValidLanguage;

import java.util.Arrays;
import java.util.Locale;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class LanguageValidator implements ConstraintValidator<ValidLanguage, Locale> {
    private List<String> codes;

    @Override
    public void initialize(ValidLanguage constraintAnnotation) {
        this.codes = Arrays.asList("en", "ua");
    }

    @Override
    public boolean isValid(Locale value, ConstraintValidatorContext context) {
        return codes.contains(value.getLanguage());
    }
}
