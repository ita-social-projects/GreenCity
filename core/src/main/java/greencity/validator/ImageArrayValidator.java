package greencity.validator;

import greencity.annotations.ImageArrayValidation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;
import java.util.Arrays;
import java.util.List;

public class ImageArrayValidator implements ConstraintValidator<ImageArrayValidation, MultipartFile[]> {
    private final List<String> validType = Arrays.asList("image/jpeg", "image/png", "image/jpg");

    @Override
    public boolean isValid(MultipartFile[] images, ConstraintValidatorContext constraintValidatorContext) {
        if (images == null) {
            return true;
        }
        for (MultipartFile image: images) {
            if (image == null) {
                return true;
            } else {
                long maxSize = 10 * 1024 * 1024; //10mb
                if (image.getSize() > maxSize) {
                    return false;
                }
                if (!validType.contains(image.getContentType())) {
                    return false;
                }
            }
        }
        return true;
    }
}
