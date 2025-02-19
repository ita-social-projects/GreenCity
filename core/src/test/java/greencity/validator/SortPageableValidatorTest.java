package greencity.validator;

import greencity.TestConst;
import greencity.dto.friends.UserFriendDto;
import greencity.exception.exceptions.UnsupportedSortException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class SortPageableValidatorTest {

    private SortPageableValidator validator;

    @BeforeEach
    void setUp() {
        validator = new SortPageableValidator();
    }

    @Test
    void validateSortParameterAllFieldsValidTest() {
        Sort sort = Sort.by(Sort.Order.asc(TestConst.FIELD_ID));

        assertDoesNotThrow(() -> validator.validateSortParameter(UserFriendDto.class, sort));
    }

    @Test
    void validateSortParameterInvalidFieldThrowsExceptionTest() {
        Sort sort = Sort.by(Sort.Order.asc(TestConst.INVALID_FIELD));

        UnsupportedSortException exception = assertThrows(UnsupportedSortException.class, () ->
            validator.validateSortParameter(UserFriendDto.class, sort)
        );

        assertTrue(exception.getMessage().contains(TestConst.INVALID_FIELD));
    }
}