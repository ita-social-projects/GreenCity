package greencity.validator;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ImageArrayValidatorTest {
    @InjectMocks
    ImageArrayValidator imageArrayValidator;

    @Mock
    private ConstraintValidatorContext constraintValidatorContext;

    @Test
    void isValid_WithValidImageJPEG_True() {
        MockMultipartFile[] image = new MockMultipartFile[]{new MockMultipartFile("image", "image.jpeg", "image/jpeg", new byte[1024])};
        assertTrue(imageArrayValidator.isValid(image, constraintValidatorContext));
    }

    @Test
    void isValid_WithValidImageJPG_True() {
        MockMultipartFile[] image = new MockMultipartFile[]{new MockMultipartFile("image", "image.jpg", "image/jpg", new byte[10 * 1024 * 1024])}; //max size
        assertTrue(imageArrayValidator.isValid(image, constraintValidatorContext));
    }

    @Test
    void isValid_WithValidImagePNG_True() {
        MockMultipartFile[] image = new MockMultipartFile[]{new MockMultipartFile("image", "image.png", "image/png", new byte[1])};
        assertTrue(imageArrayValidator.isValid(image, constraintValidatorContext));
    }

    @Test
    void isValid_WithEmptyImage_True() {
        MockMultipartFile[] image = new MockMultipartFile[]{new MockMultipartFile("image", "image.jpeg", "image/jpeg", new byte[0])};
        assertTrue(imageArrayValidator.isValid(image, constraintValidatorContext));
    }

    @Test
    void isValid_WithNullImage_True() {
        assertTrue(imageArrayValidator.isValid(null, constraintValidatorContext));
    }

    @Test
    void isValid_WithNullElements_True() {
        MockMultipartFile[] image = new MockMultipartFile[1];
        imageArrayValidator.isValid(image, constraintValidatorContext);
        image[0] = null;
        assertTrue(imageArrayValidator.isValid(image, constraintValidatorContext));
    }

    @Test
    void isValid_WithInvalidContentType_False() {
        MockMultipartFile[] image = new MockMultipartFile[]{new MockMultipartFile("image", "image.txt", "text/plain", new byte[0])};
        assertFalse(imageArrayValidator.isValid(image, constraintValidatorContext));
    }

    @Test
    void isValid_WithInvalidSize_False() {
        int invalidSize = (10 * 1024 * 1024) + 1;
        MockMultipartFile[] image = new MockMultipartFile[]{new MockMultipartFile("image", "image.jpeg", "image/jpeg", new byte[invalidSize])};
        assertFalse(imageArrayValidator.isValid(image, constraintValidatorContext));
    }
}
