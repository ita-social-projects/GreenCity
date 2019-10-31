package greencity.exception;

import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Custom exception handler.
 *
 * @author Marian Milian
 */

@AllArgsConstructor
@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
    private ErrorAttributes errorAttributes;

    /**
     * Method intercept exception {@link RuntimeException}.
     *
     * @param ex      Exception witch should be intercepted.
     * @param request contain  detail about occur exception
     * @return ResponseEntity witch  contain http status and body  with message of exception.
     * @author Marian Milian
     */
    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity handleRuntimeException(RuntimeException ex, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.trace(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Method intercept exception {@link BadEmailOrPasswordException}.
     *
     * @param request contain  detail about occur exception
     * @return ResponseEntity witch  contain http status and body  with message of exception.
     * @author Nazar Stasyuk
     */
    @ExceptionHandler(AuthenticationException.class)
    public final ResponseEntity authenticationException(WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exceptionResponse);
    }

    /**
     * Method intercept exception {@link MethodArgumentTypeMismatchException}.
     *
     * @param request contain  detail about occur exception
     * @return ResponseEntity witch  contain http status and body  with message of exception.
     * @author Roman Zahorui
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public final ResponseEntity handleConversionFailedException(
        MethodArgumentTypeMismatchException ex, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        String propName = ex.getName();
        String className = null;
        if (ex.getRequiredType() != null) {
            className = ex.getRequiredType().getSimpleName();
        }
        String message = String.format("Wrong %s. Should be '%s'", propName, className);
        exceptionResponse.setMessage(message);
        log.trace(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Method intercept exception {@link BadRefreshTokenException}.
     *
     * @param request contain  detail about occur exception
     * @return ResponseEntity witch  contain http status and body  with message of exception.
     * @author Nazar Stasyuk
     */
    @ExceptionHandler(BadRefreshTokenException.class)
    public final ResponseEntity handleBadRefreshTokenException(WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exceptionResponse);
    }

    /**
     * Method intercept exception {@link UserAlreadyRegisteredException}.
     *
     * @param ex Exception witch should be intercepted.
     * @return ResponseEntity witch  contain http status and body  with message of exception.
     * @author Nazar Stasyuk
     */
    @ExceptionHandler(UserAlreadyRegisteredException.class)
    public final ResponseEntity handleBadEmailException(UserAlreadyRegisteredException ex) {
        ValidationExceptionDto validationExceptionDto =
            new ValidationExceptionDto(AppConstant.REGISTRATION_EMAIL_FIELD_NAME, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Collections.singletonList(validationExceptionDto));
    }

    /**
     * Method intercept exception {@link BadPlaceRequestException}.
     *
     * @param ex      Exception witch should be intercepted.
     * @param request contain  detail about occur exception
     * @return ResponseEntity witch  contain http status and body  with message of exception.
     */
    @ExceptionHandler(BadPlaceRequestException.class)
    public final ResponseEntity handleBadPlaceRequestException(BadPlaceRequestException ex, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.trace(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Method intercept exception {@link DateTimeParseException}.
     *
     * @param ex      Exception witch should be intercepted.
     * @param request contain  detail about occur exception
     * @return ResponseEntity witch  contain http status and body  with message of exception.
     */
    @ExceptionHandler(DateTimeParseException.class)
    public final ResponseEntity handleDateTimeParseException(DateTimeParseException ex, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        exceptionResponse.setMessage(ErrorMessage.WRONG_DATE_TIME_FORMAT);
        log.trace(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Method intercept exception {@link BadCategoryRequestException}.
     *
     * @param ex      Exception witch should be intercepted.
     * @param request contain  detail about occur exception
     * @return ResponseEntity witch  contain http status and body  with message of exception.
     */
    @ExceptionHandler(BadCategoryRequestException.class)
    public final ResponseEntity handleBadCategoryRequestException(BadCategoryRequestException ex, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.trace(ex.getMessage(), ex);
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
        return new HashMap<>(errorAttributes.getErrorAttributes(webRequest, true));
    }
}
