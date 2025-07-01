package greencity.dto.dailyfact;

import greencity.dto.user.UserVO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DailyFactDtoTest {
    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validDailyFactDto_shouldPassValidation() {
        DailyFactDto dto = DailyFactDto.builder()
            .id(1L)
            .userVO(UserVO.builder().id(1L).build())
            .factEn("This is a valid English fact.")
            .factUk("Це правильний український факт.")
            .build();

        Set<ConstraintViolation<DailyFactDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void nullUser_shouldFailValidation() {
        DailyFactDto dto = DailyFactDto.builder()
            .userVO(null)
            .factEn("Some fact")
            .factUk("Деякий факт")
            .build();

        Set<ConstraintViolation<DailyFactDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("userVO")));
    }

    @Test
    void blankFactEn_shouldFailValidation() {
        DailyFactDto dto = DailyFactDto.builder()
            .userVO(UserVO.builder().id(1L).build())
            .factEn(" ")
            .factUk("Дійсний факт")
            .build();

        Set<ConstraintViolation<DailyFactDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("factEn")));
    }

    @Test
    void blankFactUk_shouldFailValidation() {
        DailyFactDto dto = DailyFactDto.builder()
            .userVO(UserVO.builder().id(1L).build())
            .factEn("Valid")
            .factUk(" ")
            .build();

        Set<ConstraintViolation<DailyFactDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("factUk")));
    }

    @Test
    void tooLongFactEn_shouldFailValidation() {
        String longText = "a".repeat(301);
        DailyFactDto dto = DailyFactDto.builder()
            .userVO(UserVO.builder().id(1L).build())
            .factEn(longText)
            .factUk("Допустимий факт")
            .build();

        Set<ConstraintViolation<DailyFactDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("factEn")));
    }

    @Test
    void tooLongFactUk_shouldFailValidation() {
        String longText = "б".repeat(301);
        DailyFactDto dto = DailyFactDto.builder()
            .userVO(UserVO.builder().id(1L).build())
            .factEn("Valid")
            .factUk(longText)
            .build();

        Set<ConstraintViolation<DailyFactDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("factUk")));
    }
}
