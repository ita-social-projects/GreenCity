package greencity.dto.user;

import greencity.ModelUtils;
import greencity.enums.ShoppingListItemStatus;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserShoppingListItemResponseDtoTest {

    void testValid(UserShoppingListItemResponseDto dto) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();

        Set<ConstraintViolation<UserShoppingListItemResponseDto>> constraintViolations =
            validator.validate(dto);

        assertTrue(constraintViolations.isEmpty());
    }

    void testInvalid(UserShoppingListItemResponseDto dto) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();

        Set<ConstraintViolation<UserShoppingListItemResponseDto>> constraintViolations =
            validator.validate(dto);

        assertFalse(constraintViolations.isEmpty());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {1L, 100L, Long.MAX_VALUE})
    void validIdTest(Long id) {
        var dto = ModelUtils.getUserShoppingListItemResponseDto();
        dto.setId(id);

        testValid(dto);
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L, -100L, Long.MIN_VALUE})
    void invalidIdTest(Long id) {
        var dto = ModelUtils.getUserShoppingListItemResponseDto();
        dto.setId(id);

        testInvalid(dto);
    }

    @ParameterizedTest
    @ValueSource(strings = {"t", "text"})
    void validTextTest(String text) {
        var dto = ModelUtils.getUserShoppingListItemResponseDto();
        dto.setText(text);

        testValid(dto);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void invalidTextTest(String text) {
        var dto = ModelUtils.getUserShoppingListItemResponseDto();
        dto.setText(text);

        testInvalid(dto);
    }

    @ParameterizedTest
    @EnumSource(ShoppingListItemStatus.class)
    void validStatusTest(ShoppingListItemStatus status) {
        var dto = ModelUtils.getUserShoppingListItemResponseDto();
        dto.setStatus(status);

        testValid(dto);
    }

    @ParameterizedTest
    @NullSource
    void invalidStatusTest(ShoppingListItemStatus status) {
        var dto = ModelUtils.getUserShoppingListItemResponseDto();
        dto.setStatus(status);

        testInvalid(dto);
    }
}
