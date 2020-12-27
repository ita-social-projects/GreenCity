package greencity.validator;

import greencity.annotations.ValidLanguage;
import greencity.constant.RestTemplateLinks;
import lombok.AllArgsConstructor;
import org.springframework.web.client.RestTemplate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@AllArgsConstructor
public class LanguageValidator implements ConstraintValidator<ValidLanguage, Locale> {
    private List<String> codes;
    private final RestTemplate restTemplate;

    @Override
    public void initialize(ValidLanguage constraintAnnotation) {
        String[] restTemplateForObject = restTemplate.getForObject(RestTemplateLinks.GREEN_CITY_SERVER_PORT
            + RestTemplateLinks.LANGUAGE, String[].class);
        assert restTemplateForObject != null;
        codes = Arrays.asList(restTemplateForObject);
    }

    @Override
    public boolean isValid(Locale value, ConstraintValidatorContext context) {
        return codes.contains(value.getLanguage());
    }
}
