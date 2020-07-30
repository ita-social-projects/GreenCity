package greencity.validator;

import greencity.annotations.ValidLanguage;
import greencity.service.LanguageService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class LanguageValidator implements ConstraintValidator<ValidLanguage, String> {
    private List<String> codes;
    @Autowired
    private LanguageService languageService;

    @Override
    public void initialize(ValidLanguage constraintAnnotation) {
        codes = languageService.findAllLanguageCodes();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return codes.contains(value);
    }
}
