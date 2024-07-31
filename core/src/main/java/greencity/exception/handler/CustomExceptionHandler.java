package greencity.exception.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.constant.ValidationConstants;
import greencity.exception.exceptions.BadCategoryRequestException;
import greencity.exception.exceptions.BadPlaceRequestException;
import greencity.exception.exceptions.BadRefreshTokenException;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.BadSocialNetworkLinksException;
import greencity.exception.exceptions.EmailNotVerified;
import greencity.exception.exceptions.EventDtoValidationException;
import greencity.exception.exceptions.InvalidStatusException;
import greencity.exception.exceptions.InvalidURLException;
import greencity.exception.exceptions.NotCurrentUserException;
import greencity.exception.exceptions.NotDeleteLastHabit;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.exception.exceptions.NotUpdatedException;
import greencity.exception.exceptions.ShoppingListItemNotFoundException;
import greencity.exception.exceptions.TagNotFoundException;
import greencity.exception.exceptions.UnsupportedSortException;
import greencity.exception.exceptions.UserAlreadyHasEnrolledHabitAssign;
import greencity.exception.exceptions.UserAlreadyHasHabitAssignedException;
import greencity.exception.exceptions.UserAlreadyRegisteredException;
import greencity.exception.exceptions.UserHasNoAvailableHabitTranslationException;
import greencity.exception.exceptions.UserHasNoFriendWithIdException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.exception.exceptions.UserHasNoShoppingListItemsException;
import greencity.exception.exceptions.UserHasReachedOutOfEnrollRange;
import greencity.exception.exceptions.UserShoppingListItemNotSavedException;
import greencity.exception.exceptions.UserShoppingListItemStatusNotUpdatedException;
import greencity.exception.exceptions.WrongEmailOrPasswordException;
import greencity.exception.exceptions.WrongIdException;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final ObjectMapper objectMapper;

    /**
     * ExceptionHandler for intercepting errors from GreenCityUser.
     *
     * @param ex      exception thrown by RestTemplate
     * @param request request with details
     * @return response entity similar to one that came from GreenCityUser
     */
    @ExceptionHandler(HttpClientErrorException.class)
    public final ResponseEntity<Object> handleHttpClientErrorException(
        HttpClientErrorException ex, WebRequest request) throws JsonProcessingException {
        Map<String, String> httpClientResponseBody = jsonHttpClientErrorExceptionToMap(ex);
        String message = httpClientResponseBody.get("message");
        log.warn(ex.getStatusCode() + " " + message);
        HttpClientErrorExceptionResponse responseBody =
            new HttpClientErrorExceptionResponse(getErrorAttributes(request), message);
        return ResponseEntity.status(ex.getStatusCode()).body(responseBody);
    }

    private Map<String, String> jsonHttpClientErrorExceptionToMap(
        HttpClientErrorException ex) throws JsonProcessingException {
        TypeReference<Map<String, String>> responseType = new TypeReference<>() {
        };
        Map<String, String> httpClientResponseBody;
        httpClientResponseBody = objectMapper.readValue(ex.getResponseBodyAsString(), responseType);

        return httpClientResponseBody;
    }

    /**
     * Method intercept exception {@link MultipartException}.
     *
     * @param ex      Exception witch should be intercepted.
     * @param request contain detail about occur exception.
     * @return ResponseEntity which contains http status and body with message of
     *         exception.
     * @author Danylo Hlynskyi
     */
    @ExceptionHandler(MultipartException.class)
    public final ResponseEntity<Object> handleTooLargeMultipartFileRequest(MultipartException ex,
        WebRequest request) {
        log.warn(ex.getMessage());
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(exceptionResponse);
    }

    /**
     * Method interceptor for exceptions such as {@link BadRequestException},
     * {@link UserHasNoAvailableHabitTranslationException},{@link NotDeleteLastHabit}
     * {@link UserAlreadyHasHabitAssignedException}, {@link NotSavedException}
     * {@link UserHasReachedOutOfEnrollRange},{@link WrongIdException},
     * {@link UserAlreadyHasEnrolledHabitAssign}, {@link BadRefreshTokenException},
     * {@link BadPlaceRequestException}, {@link BadSocialNetworkLinksException},
     * {@link InvalidURLException}, {@link BadCategoryRequestException},
     * {@link UserShoppingListItemNotSavedException},{@link NotDeletedException}
     * {@link EventDtoValidationException}, {@link UnsupportedSortException},
     * {@link NotUpdatedException}, {@link UserHasNoShoppingListItemsException},
     * {@link InvalidStatusException},
     * {@link UserShoppingListItemStatusNotUpdatedException}.
     *
     * @param request Contains details about the occurred exception.
     * @return ResponseEntity which contains the HTTP status and body with the
     *         message of the exception.
     */
    @ExceptionHandler({
        BadRequestException.class, UserHasNoAvailableHabitTranslationException.class,
        UserAlreadyHasHabitAssignedException.class, UserHasReachedOutOfEnrollRange.class,
        UserAlreadyHasEnrolledHabitAssign.class, BadRefreshTokenException.class,
        BadPlaceRequestException.class, BadSocialNetworkLinksException.class,
        InvalidURLException.class, BadCategoryRequestException.class,
        UserShoppingListItemNotSavedException.class, EventDtoValidationException.class,
        WrongIdException.class, NotDeletedException.class,
        UnsupportedSortException.class, NotUpdatedException.class,
        UserHasNoShoppingListItemsException.class, InvalidStatusException.class,
        UserShoppingListItemStatusNotUpdatedException.class, NotDeleteLastHabit.class,
        NotSavedException.class
    })
    public final ResponseEntity<Object> handleBadRequestException(WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.trace(exceptionResponse.getMessage(), exceptionResponse.getTrace());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Method interceptor exception {@link UserHasNoFriendWithIdException},
     * {@link NotFoundException}, {@link ShoppingListItemNotFoundException},
     * {@link ShoppingListItemNotFoundException}, {@link TagNotFoundException}.
     *
     * @param request contain detail about occur exception.
     * @return ResponseEntity which contain http status and body with message of
     *         exception.
     */
    @ExceptionHandler({NotFoundException.class,
        UserHasNoFriendWithIdException.class, ShoppingListItemNotFoundException.class,
        ShoppingListItemNotFoundException.class, TagNotFoundException.class})
    public final ResponseEntity<Object> handleNotFoundException(WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.trace(exceptionResponse.getMessage(), exceptionResponse.getTrace());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse);
    }

    /**
     * Method interceptor exception {@link NotCurrentUserException},
     * {@link UserHasNoPermissionToAccessException}, {@link EmailNotVerified}.
     *
     * @param request contain detail about occur exception.
     * @return ResponseEntity which contain http status and body with message of
     *         exception.
     */
    @ExceptionHandler({
        NotCurrentUserException.class, UserHasNoPermissionToAccessException.class,
        EmailNotVerified.class})
    public final ResponseEntity<Object> handleForbiddenException(WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.trace(exceptionResponse.getMessage(), exceptionResponse.getTrace());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exceptionResponse);
    }

    /**
     * Method intercept exception {@link ConstraintViolationException}.
     *
     * @param ex      Exception witch should be intercepted.
     * @param request contain detail about occur exception
     * @return ResponseEntity witch contain http status and body with message of
     *         exception.
     * @author Yurii Savchenko
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex,
        WebRequest request) {
        log.warn(ex.getMessage());
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        String detailedMessage = ex.getConstraintViolations().stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.joining(" "));
        exceptionResponse.setMessage(detailedMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Method intercept exception {@link IllegalArgumentException}.
     *
     * @param ex      Exception witch should be intercepted.
     * @param request contain detail about occur exception
     * @return ResponseEntity witch contain http status and body with message of
     *         exception.
     * @author Volodymyr Mladonov
     */
    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        log.warn(ex.getMessage());
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        if (ex.getMessage().startsWith(ValidationConstants.LOCALE_PART_ERROR_MESSAGE)) {
            exceptionResponse.setMessage(ValidationConstants.LANGUAGE_VALIDATION_EXCEPTION_MESSAGE);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Method intercept exception {@link WrongEmailOrPasswordException}.
     *
     * @param request contain detail about occur exception
     * @return ResponseEntity witch contain http status and body with message of
     *         exception.
     * @author Nazar Stasyuk
     */
    @ExceptionHandler(AuthenticationException.class)
    public final ResponseEntity<Object> authenticationException(WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exceptionResponse);
    }

    /**
     * Method intercept exception {@link MethodArgumentTypeMismatchException}.
     *
     * @param request contain detail about occur exception
     * @return ResponseEntity witch contain http status and body with message of
     *         exception.
     * @author Roman Zahorui
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public final ResponseEntity<Object> handleConversionFailedException(
        @NonNull MethodArgumentTypeMismatchException ex, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        String propName = ex.getName();
        String className = null;
        Class<?> requiredType = ex.getRequiredType();
        if (requiredType != null) {
            className = requiredType.getSimpleName();
        }
        String message = "Wrong %s. Should be '%s'".formatted(propName, className);
        exceptionResponse.setMessage(message);
        log.warn(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Method intercept exception {@link UserAlreadyRegisteredException}.
     *
     * @param ex Exception witch should be intercepted.
     * @return ResponseEntity witch contain http status and body with message of
     *         exception.
     * @author Nazar Stasyuk
     */
    @ExceptionHandler(UserAlreadyRegisteredException.class)
    public final ResponseEntity<Object> handleBadEmailException(UserAlreadyRegisteredException ex) {
        ValidationExceptionDto validationExceptionDto =
            new ValidationExceptionDto(AppConstant.REGISTRATION_EMAIL_FIELD_NAME, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Collections.singletonList(validationExceptionDto));
    }

    /**
     * Method intercept exception {@link DateTimeParseException}.
     *
     * @param ex      Exception witch should be intercepted.
     * @param request contain detail about occur exception
     * @return ResponseEntity witch contain http status and body with message of
     *         exception.
     */
    @ExceptionHandler(DateTimeParseException.class)
    public final ResponseEntity<Object> handleDateTimeParseException(DateTimeParseException ex, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        exceptionResponse.setMessage(ErrorMessage.WRONG_DATE_TIME_FORMAT);
        log.warn(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
        HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.warn(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
        HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<ValidationExceptionDto> collect =
            ex.getBindingResult().getFieldErrors().stream()
                .map(ValidationExceptionDto::new)
                .collect(Collectors.toList());
        log.warn(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(collect);
    }

    private Map<String, Object> getErrorAttributes(WebRequest webRequest) {
        return new HashMap<>(errorAttributes.getErrorAttributes(webRequest,
            ErrorAttributeOptions.of(ErrorAttributeOptions.Include.MESSAGE)));
    }
}
