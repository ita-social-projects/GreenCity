package greencity.validator;

import greencity.ModelUtils;
import jakarta.validation.ConstraintValidatorContext;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NameValidatorTest {
    @Mock
    private ConstraintValidatorContext context;
    @InjectMocks
    private NameValidator validator;

    @ParameterizedTest
    @MethodSource("provideNames")
    void invalidNames(String name) {

        when(context.buildConstraintViolationWithTemplate(any()))
            .thenReturn(ModelUtils.getConstraintViolationBuilder());

        assertFalse(validator.isValid(name, context));
    }

    @Test
    void validName() {
        assertTrue(validator.isValid("Valid", context));
    }

    private static Stream<String> provideNames() {
        return Stream.of(
            "",
            "ThisNameIsWayTooLongForTheValidation",
            "Invalid@Name",
            ".InvalidName",
            "InvalidName.",
            "Invalid..Name",
            null);
    }
}
