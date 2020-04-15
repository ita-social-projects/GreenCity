package greencity.validator;

import greencity.annotations.ValidTags;
import greencity.constant.ValidationConstants;
import greencity.service.TagService;
import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class TagsValidator implements ConstraintValidator<ValidTags, List<String>> {
    @Autowired
    private TagService tagService;

    @Override
    public void initialize(ValidTags constraintAnnotation) {
    }

    @Override
    public boolean isValid(List<String> tags, ConstraintValidatorContext constraintValidatorContext) {
        if (tags.size() > ValidationConstants.MAX_AMOUNT_OF_TAGS) {
            return false;
        }
        return tagService.isAllValid(tags);
    }
}
