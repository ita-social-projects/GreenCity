package greencity.validator;

import greencity.annotations.LanguageTranslationConstraint;
import greencity.dto.language.LanguageDTO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.service.LanguageService;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class LanguageTranslationValidator implements
    ConstraintValidator<LanguageTranslationConstraint, List<? extends LanguageTranslationDTO>> {
    private List<LanguageDTO> languageDTOS;
    @Autowired
    private LanguageService languageService;

    @Override
    public void initialize(LanguageTranslationConstraint constraintAnnotation) {
        this.languageDTOS = languageService.getAllLanguages();
        this.languageDTOS.sort(Comparator.comparing(LanguageDTO::getCode));
    }

    @Override
    public boolean isValid(List<? extends LanguageTranslationDTO> value, ConstraintValidatorContext context) {
        List<LanguageDTO> valueLanguageDTOS = value
            .stream()
            .map(LanguageTranslationDTO::getLanguage)
            .sorted(Comparator.comparing(LanguageDTO::getCode))
            .collect(Collectors.toList());

        return (value.size() == 3)
            && (languageDTOS.equals(valueLanguageDTOS));
    }
}
