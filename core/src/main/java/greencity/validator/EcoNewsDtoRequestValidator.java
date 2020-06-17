package greencity.validator;

import greencity.annotations.ValidEcoNewsDtoRequest;
import greencity.constant.ErrorMessage;
import greencity.constant.ValidationConstants;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.exception.exceptions.InvalidURLException;
import greencity.exception.exceptions.TagNotFoundDuringValidation;
import greencity.exception.exceptions.WrongCountOfTagsException;
import greencity.service.EcoNewsService;
import org.apache.maven.project.ModelUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.util.List;

import static greencity.validator.UrlValidator.isUrlValid;

public class EcoNewsDtoRequestValidator implements ConstraintValidator<ValidEcoNewsDtoRequest, AddEcoNewsDtoRequest> {
    @Override
    public void initialize(ValidEcoNewsDtoRequest constraintAnnotation) {

    }

    @Override
    public boolean isValid(AddEcoNewsDtoRequest value, ConstraintValidatorContext context) {
        if (isUrlValid(value.getSource())) {
            List<String> tags = value.getTags();
            if (tags.isEmpty() || tags.size() > ValidationConstants.MAX_AMOUNT_OF_TAGS) {
                throw new WrongCountOfTagsException(ErrorMessage.WRONG_COUNT_OF_TAGS_EXCEPTION);
            }
            return true;
        }
        throw new InvalidURLException(ErrorMessage.INVALID_URL);
    }
}
