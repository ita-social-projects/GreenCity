package greencity.validator;

import greencity.annotations.ImageArrayValidation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import java.util.Arrays;
import java.util.List;

public class ImageArrayValidator implements ConstraintValidator<ImageArrayValidation, MultipartFile[]> {
    private final List<String> validType = Arrays.asList("image/jpeg", "image/png", "image/jpg", "image/gif");

    @Value("${max-multipart-comment-image-size}")
    private String maxImageSize;

    @Override
    public boolean isValid(MultipartFile[] images, ConstraintValidatorContext context) {
        if (images == null) {
            return true;
        }
        for (MultipartFile image : images) {
            if (image == null) {
                return true;
            } else {
                if (image.getSize() > getMaxSizeInBytes()) {
                    context.disableDefaultConstraintViolation();
                    context
                        .buildConstraintViolationWithTemplate(
                            "Download PNG or JPEG or GIF only. Max size of " + maxImageSize + " each.")
                        .addConstraintViolation();
                    return false;
                }
                if (!validType.contains(image.getContentType())) {
                    return false;
                }
            }
        }
        return true;
    }

    long getMaxSizeInBytes() {
        if (maxImageSize.endsWith("KB")) {
            return Long.parseLong(maxImageSize.replace("KB", "").trim()) * 1024;
        } else if (maxImageSize.endsWith("MB")) {
            return Long.parseLong(maxImageSize.replace("MB", "").trim()) * 1024 * 1024;
        } else if (maxImageSize.endsWith("GB")) {
            return Long.parseLong(maxImageSize.replace("GB", "").trim()) * 1024 * 1024 * 1024;
        } else {
            throw new IllegalArgumentException("Invalid file size unit: " + maxImageSize);
        }
    }
}
