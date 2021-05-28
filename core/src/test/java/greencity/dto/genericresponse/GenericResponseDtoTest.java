package greencity.dto.genericresponse;

import static greencity.dto.genericresponse.GenericResponseDto.buildGenericResponseDto;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@ExtendWith(MockitoExtension.class)
class GenericResponseDtoTest {
    @Mock
    BindingResult bindingResult;

    @Test
    void buildGenericResponseDtoTest() {
        FieldError fieldError = new FieldError("testError", "field", "message");
        List<FieldError> errorList = Collections.singletonList(fieldError);
        when(bindingResult.getFieldErrors()).thenReturn(errorList);
        GenericResponseDto genericResponseDto = new GenericResponseDto();
        genericResponseDto.getErrors().add(
            new FieldErrorDto(fieldError.getField(), fieldError.getDefaultMessage()));
        GenericResponseDto result = buildGenericResponseDto(bindingResult);
        assertEquals(genericResponseDto, result);
    }

    @Test
    void buildGenericResponseDtoWithEmptyListTest() {
        List<FieldError> errorList = Collections.emptyList();
        when(bindingResult.getFieldErrors()).thenReturn(errorList);
        GenericResponseDto genericResponseDto = new GenericResponseDto();
        GenericResponseDto result = buildGenericResponseDto(bindingResult);
        assertEquals(genericResponseDto, result);
    }
}