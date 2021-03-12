package greencity.validator;

import greencity.annotations.ValidTipsAndTricksDtoRequest;
import greencity.constant.ErrorMessage;
import greencity.dto.tipsandtricks.TipsAndTricksDtoRequest;
import greencity.enums.TagType;
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
        // Initializes the validator in preparation for #isValid calls
    }

    @Override
    public boolean isValid(TipsAndTricksDtoRequest dto, ConstraintValidatorContext constraintValidatorContext) {
        if (dto.getSource() != null && !dto.getSource().isEmpty()) {
            isUrlValid(dto.getSource());
        }
        if ((tagService.isValidNumOfUniqueTags(dto.getTags())
            && (!tagService.isAllTipsAndTricksValid(dto.getTags(), TagType.TIPS_AND_TRICKS)))) {
            throw new TagNotFoundDuringValidation(ErrorMessage.TAGS_NOT_FOUND);
        }
        return true;
    }
}
