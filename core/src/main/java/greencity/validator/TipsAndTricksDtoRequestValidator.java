package greencity.validator;

import greencity.annotations.ValidTipsAndTricksDtoRequest;
import greencity.constant.ErrorMessage;
import greencity.dto.tipsandtricks.TipsAndTricksDtoRequest;
import greencity.exception.exceptions.TagNotFoundDuringValidation;
import greencity.service.TagsService;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import static greencity.validator.UrlValidator.isUrlValid;

public class TipsAndTricksDtoRequestValidator
    implements ConstraintValidator<ValidTipsAndTricksDtoRequest, TipsAndTricksDtoRequest> {
    @Autowired
    private TagsService tagService;

    @Override
    public void initialize(ValidTipsAndTricksDtoRequest constraintAnnotation) {
    }

    @Override
    public boolean isValid(TipsAndTricksDtoRequest dto, ConstraintValidatorContext constraintValidatorContext) {
        if (dto.getSource() != null && !dto.getSource().isEmpty()) {
            isUrlValid(dto.getSource());
        }
        if (Boolean.TRUE.equals(tagService.isValidNumOfUniqueTags(dto.getTags())) ||
                !Boolean.TRUE.equals(tagService.isAllTipsAndTricksValid(dto.getTags()))) {
                throw new TagNotFoundDuringValidation(ErrorMessage.TAGS_NOT_FOUND);
            }
        return true;
    }
}
