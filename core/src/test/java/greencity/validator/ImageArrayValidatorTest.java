package greencity.validator;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageArrayValidatorTest {
    @InjectMocks
    ImageArrayValidator imageArrayValidator;

    @Mock
    ConstraintValidatorContext constraintValidatorContext;

    @Mock
    ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(imageArrayValidator, "maxImageSize", "10MB");
        ReflectionTestUtils.setField(imageArrayValidator, "messageTemplate", "Upload %s only. Max size of %s each.");
        ReflectionTestUtils.setField(imageArrayValidator, "allowedTypes",
            List.of("image/jpeg", "image/png", "image/jpg", "image/gif"));
    }

    @Test
    void validWithValidImageJPEGReturnsTrue() {
        MockMultipartFile[] image = new MockMultipartFile[] {
            new MockMultipartFile("image", "image.jpeg", "image/jpeg", new byte[1024])};

        then(imageArrayValidator.isValid(image, constraintValidatorContext)).isTrue();
    }

    @Test
    void validWithValidImageJPGReturnsTrue() {
        MockMultipartFile[] image = new MockMultipartFile[] {
            new MockMultipartFile("image", "image.jpg", "image/jpg", new byte[10 * 1024 * 1024])}; // max size

        then(imageArrayValidator.isValid(image, constraintValidatorContext)).isTrue();
    }

    @Test
    void validWithValidImagePNGReturnsTrue() {
        MockMultipartFile[] image = new MockMultipartFile[] {
            new MockMultipartFile("image", "image.png", "image/png", new byte[1])};

        then(imageArrayValidator.isValid(image, constraintValidatorContext)).isTrue();
    }

    @Test
    void validWithValidImageGIFReturnsTrue() {
        MockMultipartFile[] image = new MockMultipartFile[] {
            new MockMultipartFile("image", "image.gif", "image/gif", new byte[1024])};

        then(imageArrayValidator.isValid(image, constraintValidatorContext)).isTrue();
    }

    @Test
    void isValid_WithEmptyImageReturnsTrue() {
        MockMultipartFile[] image = new MockMultipartFile[] {
            new MockMultipartFile("image", "image.jpeg", "image/jpeg", new byte[0])};

        then(imageArrayValidator.isValid(image, constraintValidatorContext)).isTrue();
    }

    @Test
    void isValid_WithNullImageReturnsTrue() {
        then(imageArrayValidator.isValid(null, constraintValidatorContext)).isTrue();
    }

    @Test
    void isValid_WithNullElementsReturnsTrue() {
        MockMultipartFile[] image = new MockMultipartFile[1];
        image[0] = null;

        then(imageArrayValidator.isValid(image, constraintValidatorContext)).isTrue();
    }

    @Test
    void validWithInvalidContentTypeReturnsFalse() {
        MockMultipartFile[] image = new MockMultipartFile[] {
            new MockMultipartFile("image", "image.txt", "text/plain", new byte[0])};
        when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString()))
            .thenReturn(violationBuilder);

        then(imageArrayValidator.isValid(image, constraintValidatorContext)).isFalse();
    }

    @Test
    void validWithInvalidSizeReturnsFalse() {
        int invalidSize = (10 * 1024 * 1024) + 1;
        MockMultipartFile[] image = new MockMultipartFile[] {
            new MockMultipartFile("image", "image.jpeg", "image/jpeg", new byte[invalidSize])};

        when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString()))
            .thenReturn(violationBuilder);

        then(imageArrayValidator.isValid(image, constraintValidatorContext)).isFalse();

        verify(constraintValidatorContext).disableDefaultConstraintViolation();
        verify(constraintValidatorContext).buildConstraintViolationWithTemplate(anyString());
    }

    @Test
    void validWithGifExceedingSizeReturnsFalse() {
        int invalidSize = 15 * 1024 * 1024;
        MockMultipartFile[] image = new MockMultipartFile[] {
            new MockMultipartFile("image", "image.gif", "image/gif", new byte[invalidSize])};

        when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString()))
            .thenReturn(violationBuilder);

        then(imageArrayValidator.isValid(image, constraintValidatorContext)).isFalse();

        verify(constraintValidatorContext).disableDefaultConstraintViolation();
        verify(constraintValidatorContext)
            .buildConstraintViolationWithTemplate(anyString());
    }

    @Test
    void getMaxSizeInBytes_WithInvalidUnit_ThrowsException() {
        // given
        ReflectionTestUtils.setField(imageArrayValidator, "maxImageSize", "500TB"); // Invalid unit

        // when
        // then
        thenThrownBy(() -> imageArrayValidator.getMaxSizeInBytes())
            .isExactlyInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid file size unit: 500TB");
    }

    private static Stream<Arguments> getMaxSizeArgs() {
        return Stream.of(
            Arguments.of(1024 * 1024, "1024KB"),
            Arguments.of(5 * 1024 * 1024, "5MB"),
            Arguments.of(1024 * 1024 * 1024, "1GB"));
    }

    @ParameterizedTest
    @MethodSource("getMaxSizeArgs")
    void getMaxSizeInBytes_WithDifferentUnits_CorrectConversion(long sizeBytes, String sizeWithPrefix) {
        // given
        ReflectionTestUtils.setField(imageArrayValidator, "maxImageSize", sizeWithPrefix);

        // when
        final long size = imageArrayValidator.getMaxSizeInBytes();

        // then
        then(size).isEqualTo(sizeBytes);
    }

    @Test
    void getMaxSizeIncorrect() {
        // given
        ReflectionTestUtils.setField(imageArrayValidator, "maxImageSize", "500TB");

        // when
        // then
        thenThrownBy(() -> imageArrayValidator.getMaxSizeInBytes())
            .isExactlyInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid file size unit: 500TB");
    }
}
