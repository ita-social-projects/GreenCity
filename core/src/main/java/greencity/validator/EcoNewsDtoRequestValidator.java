package greencity.validator;

import greencity.annotations.ValidEcoNewsDtoRequest;
import greencity.constant.ErrorMessage;
import greencity.constant.ValidationConstants;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.exception.exceptions.WrongCountOfTagsException;
import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static greencity.validator.UrlValidator.isUrlValid;

public class EcoNewsDtoRequestValidator implements ConstraintValidator<ValidEcoNewsDtoRequest, AddEcoNewsDtoRequest> {
    @Override
    public void initialize(ValidEcoNewsDtoRequest constraintAnnotation) {
    }

    @Override
    public boolean isValid(AddEcoNewsDtoRequest value, ConstraintValidatorContext context) {
        if (value.getSource() != null && !value.getSource().isEmpty()) {
            isUrlValid(value.getSource());
        }
        List<String> tags = value.getTags();
        if (tags.isEmpty() || tags.size() > ValidationConstants.MAX_AMOUNT_OF_TAGS) {
            throw new WrongCountOfTagsException(ErrorMessage.WRONG_COUNT_OF_TAGS_EXCEPTION);
        }
        return true;
    }
}