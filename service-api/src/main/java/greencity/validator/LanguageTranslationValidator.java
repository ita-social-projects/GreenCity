package greencity.validator;

import greencity.annotations.LanguageTranslationConstraint;
import greencity.dto.language.LanguageDTO;
import greencity.dto.language.LanguageTranslationVO;
import greencity.service.LanguageService;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class LanguageTranslationValidator implements
    ConstraintValidator<LanguageTranslationConstraint, List<LanguageTranslationVO>> {
    private List<LanguageDTO> languageDTOS;
    @Autowired
    private LanguageService languageService;

    @Override
    public void initialize(LanguageTranslationConstraint constraintAnnotation) {
        this.languageDTOS = languageService.getAllLanguages();
        this.languageDTOS.sort(Comparator.comparing(LanguageDTO::getCode));
    }

    @Override
    public boolean isValid(List<LanguageTranslationVO> value, ConstraintValidatorContext context) {
        List<LanguageDTO> valueLanguageDTOS = value
            .stream()
            .map(LanguageTranslationVO::getLanguage)
            .sorted(Comparator.comparing(LanguageDTO::getCode))
            .collect(Collectors.toList());

        return (value.size() == 3)
            && (languageDTOS.equals(valueLanguageDTOS));
    }
}
