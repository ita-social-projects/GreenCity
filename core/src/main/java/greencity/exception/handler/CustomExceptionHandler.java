package greencity.exception.handler;

import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.exception.exceptions.*;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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
     * Method intercept exception {@link UserHasNoAvailableHabitTranslationException}.
     *
     * @param ex      Exception witch should be intercepted.
     * @param request contain detail about occur exception.
     * @return ResponseEntity which contains http status and body with message of exception.
     * @author Yurii Savchenko
     */
    @ExceptionHandler(UserHasNoAvailableHabitTranslationException.class)
    public final ResponseEntity<Object> handleUserHasNoAvailableHabitDictionaryException(
        UserHasNoAvailableHabitTranslationException ex, WebRequest request) {
        log.info(ex.getMessage());
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Method intercept exception {@link UserAlreadyHasHabitAssignedException}.
     *
     * @param ex      Exception witch should be intercepted.
     * @param request contain  detail about occur exception
     * @return ResponseEntity which contain http status and body with message of exception.
     */
    @ExceptionHandler(UserAlreadyHasHabitAssignedException.class)
    public final ResponseEntity<Object> handleUserAlreadyHasHabitAssignedException(
        UserAlreadyHasHabitAssignedException ex, WebRequest request) {
        log.info(ex.getMessage());
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Method intercept exception {@link ConstraintViolationException}.
     *
     * @param ex      Exception witch should be intercepted.
     * @param request contain  detail about occur exception
     * @return ResponseEntity witch  contain http status and body  with message of exception.
     * @author Yurii Savchenko
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex,
                                                                           WebRequest request) {
        log.info(ex.getMessage());
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        String detailedMessage = ex.getConstraintViolations().stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.joining(" "));
        exceptionResponse.setMessage(detailedMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Method intercept exception {@link BadRequestException}.
     *
     * @param ex      Exception witch should be intercepted.
     * @param request contain  detail about occur exception
     * @return ResponseEntity witch  contain http status and body  with message of exception.
     * @author Yurii Savchenko
     */
    @ExceptionHandler(BadRequestException.class)
    public final ResponseEntity<Object> handleBadRequestException(BadRequestException ex, WebRequest request) {
        log.info(ex.getMessage());
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Method intercept exception {@link NotFoundException}.
     *
     * @param ex      Exception witch should be intercepted.
     * @param request contain  detail about occur exception
     * @return ResponseEntity witch  contain http status and body  with message of exception.
     * @author Marian Milian
     */

    @ExceptionHandler(NotFoundException.class)
    public final ResponseEntity<Object> handleNotFoundException(NotFoundException ex, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.trace(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse);
    }

    /**
     * Method intercept exception {@link WrongEmailOrPasswordException}.
     *
     * @param request contain  detail about occur exception
     * @return ResponseEntity witch  contain http status and body  with message of exception.
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
     * @param request contain  detail about occur exception
     * @return ResponseEntity witch  contain http status and body  with message of exception.
     * @author Roman Zahorui
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public final ResponseEntity<Object> handleConversionFailedException(
        @NonNull MethodArgumentTypeMismatchException ex, WebRequest request) {
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
    public final ResponseEntity<Object> handleBadRefreshTokenException(WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Method intercept exception {@link UserAlreadyRegisteredException}.
     *
     * @param ex Exception witch should be intercepted.
     * @return ResponseEntity witch  contain http status and body  with message of exception.
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
     * Method intercept exception {@link BadPlaceRequestException}.
     *
     * @param ex      Exception witch should be intercepted.
     * @param request contain  detail about occur exception
     * @return ResponseEntity witch  contain http status and body  with message of exception.
     */
    @ExceptionHandler(BadPlaceRequestException.class)
    public final ResponseEntity<Object> handleBadPlaceRequestException(BadPlaceRequestException ex,
                                                                       WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.trace(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Method intercept exception {@link BadSocialNetworkLinksException}.
     *
     * @param ex      Exception witch should be intercepted.
     * @param request contain  detail about occur exception
     * @return ResponseEntity witch  contain http status and body  with message of exception.
     */
    @ExceptionHandler(BadSocialNetworkLinksException.class)
    public final ResponseEntity<Object> handleBadSocialNetworkLinkException(BadSocialNetworkLinksException ex,
                                                                            WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.trace(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Method intercept exception {@link InvalidURLException}.
     *
     * @param ex      Exception witch should be intercepted.
     * @param request contain  detail about occur exception
     * @return ResponseEntity witch  contain http status and body  with message of exception.
     */
    @ExceptionHandler(InvalidURLException.class)
    public final ResponseEntity<Object> handleBadSocialNetworkLinkException(InvalidURLException ex,
                                                                            WebRequest request) {
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
    public final ResponseEntity<Object> handleDateTimeParseException(DateTimeParseException ex, WebRequest request) {
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
    public final ResponseEntity<Object> handleBadCategoryRequestException(BadCategoryRequestException ex,
                                                                          WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.trace(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Method intercept exception {@link NotCurrentUserException}.
     *
     * @param ex      Exception witch should be intercepted.
     * @param request contain  detail about occur exception
     * @return ResponseEntity witch  contain http status and body  with message of exception.
     */
    @ExceptionHandler(NotCurrentUserException.class)
    public final ResponseEntity<Object> handleUserGoalsWhereNotSavedException(NotCurrentUserException ex,
                                                                              WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.trace(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Method intercept exception {@link UserGoalNotSavedException}.
     *
     * @param ex      Exception witch should be intercepted.
     * @param request contain  detail about occur exception
     * @return ResponseEntity witch  contain http status and body  with message of exception.
     */
    @ExceptionHandler(GoalNotFoundException.class)
    public final ResponseEntity<Object> handleUserGoalsWhereNotSavedException(GoalNotFoundException ex,
                                                                              WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.trace(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Method intercept exception {@link UserGoalNotSavedException}.
     *
     * @param ex      Exception witch should be intercepted.
     * @param request contain  detail about occur exception
     * @return ResponseEntity witch  contain http status and body  with message of exception.
     */
    @ExceptionHandler(UserGoalNotSavedException.class)
    public final ResponseEntity<Object> handleUserGoalsWhereNotSavedException(UserGoalNotSavedException ex,
                                                                              WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.trace(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Method intercept exception {@link UserHasNoGoalsException}.
     *
     * @param ex      Exception witch should be intercepted.
     * @param request contain  detail about occur exception
     * @return ResponseEntity witch  contain http status and body  with message of exception.
     */
    @ExceptionHandler(UserHasNoGoalsException.class)
    public final ResponseEntity<Object> handleUserGoalsWhereNotSavedException(UserHasNoGoalsException ex,
                                                                              WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.trace(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }


    /**
     * Method intercept exception {@link UserGoalStatusNotUpdatedException}.
     *
     * @param ex      Exception witch should be intercepted.
     * @param request contain  detail about occur exception
     * @return ResponseEntity witch  contain http status and body  with message of exception.
     */
    @ExceptionHandler(UserGoalStatusNotUpdatedException.class)
    public final ResponseEntity<Object> handleUserGoalsWhereNotSavedException(UserGoalStatusNotUpdatedException ex,
                                                                              WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.trace(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Method intercept exception {@link NotDeleteLastHabit}.
     *
     * @param ex      Exception witch should be intercepted.
     * @param request contain  detail about occur exception.
     * @return ResponseEntity witch  contain http status and body  with message of exception.
     */
    @ExceptionHandler(NotDeleteLastHabit.class)
    public final ResponseEntity<Object> handleUserHabitWhereNotDeleteException(NotDeleteLastHabit ex,
                                                                               WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.trace(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.OK).body(exceptionResponse);
    }

    /**
     * Method interceptor exception {@link EmailNotVerified}.
     *
     * @param ex      Exception witch should be intercepted.
     * @param request contain  detail about occur exception
     * @return ResponseEntity witch  contain http status and body  with message of exception.
     */
    @ExceptionHandler(EmailNotVerified.class)
    public final ResponseEntity<Object> handleEmailNotVerified(EmailNotVerified ex, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.trace(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exceptionResponse);
    }

    /**
     * Method intercept exception {@link TagNotFoundException}.
     *
     * @param ex      Exception witch should be intercepted.
     * @param request contain  detail about occur exception
     * @return ResponseEntity witch  contain http status and body  with message of exception.
     */
    @ExceptionHandler(TagNotFoundException.class)
    public final ResponseEntity<Object> handleTagNotFoundException(TagNotFoundException ex, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.trace(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Customize the response for HttpMessageNotReadableException.
     *
     * @param ex      the exception
     * @param headers the headers to be written to the response
     * @param status  the selected response status
     * @param request the current request
     * @return a {@code ResponseEntity} message
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
        HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
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
