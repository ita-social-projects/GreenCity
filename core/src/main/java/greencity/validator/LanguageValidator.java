package greencity.validator;

import greencity.annotations.ValidLanguage;
import greencity.service.LanguageService;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;

public class LanguageValidator implements ConstraintValidator<ValidLanguage, Locale> {
    private List<String> codes;
    @Autowired
    private LanguageService languageService;

    @Override
    public void initialize(ValidLanguage constraintAnnotation) {
        codes = languageService.findAllLanguageCodes();
    }

    @Override
    public boolean isValid(Locale value, ConstraintValidatorContext context) {
        return codes.contains(value.getLanguage());
    }
}
