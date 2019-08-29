package greencity.exception;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@AllArgsConstructor
@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    private ErrorAttributes errorAttributes;

    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity handle(RuntimeException e, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    @ExceptionHandler(BadEmailOrPasswordException.class)
    public final ResponseEntity handle(BadEmailOrPasswordException e, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.trace(exceptionResponse.getTrace());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exceptionResponse);
    }

    @ExceptionHandler(BadRefreshTokenException.class)
    public final ResponseEntity<?> handle(BadRefreshTokenException e, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exceptionResponse);
    }

    @ExceptionHandler(BadEmailException.class)
    public final ResponseEntity handle(BadEmailException e, WebRequest request) {
        ValidationExceptionDto validationExceptionDto =
                new ValidationExceptionDto("email", e.getMessage());
        log.trace(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Collections.singletonList(validationExceptionDto));
    }

    @ExceptionHandler(BadPlaceRequestException.class)
    public final ResponseEntity handle(BadPlaceRequestException e, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.trace(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    @ExceptionHandler(BadCategoryRequestException.class)
    public final ResponseEntity handle(BadCategoryRequestException e, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.trace(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        List<ValidationExceptionDto> collect =
                ex.getBindingResult().getFieldErrors().stream()
                        .map(ValidationExceptionDto::new)
                        .collect(Collectors.toList());
        log.trace(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(collect);
    }

    private Map<String, Object> getErrorAttributes(WebRequest webRequest) {
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.putAll(errorAttributes.getErrorAttributes(webRequest, true));
        return errorMap;
    }
}
