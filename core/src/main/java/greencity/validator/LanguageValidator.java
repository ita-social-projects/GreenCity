package greencity.validator;

import greencity.annotations.ValidLanguage;

import java.util.Locale;

import greencity.service.LanguageService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LanguageValidator implements ConstraintValidator<ValidLanguage, Locale> {
    @Autowired
    private LanguageService languageService;
    private List<String> codes;

    @Override
    public void initialize(ValidLanguage constraintAnnotation) {
        this.codes = languageService.findAllLanguageCodes();
    }

    @Override
    public boolean isValid(Locale value, ConstraintValidatorContext context) {
        return codes.contains(value.getLanguage());
    }
}
