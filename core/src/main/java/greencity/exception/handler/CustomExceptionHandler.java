package greencity.exception.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.constant.ErrorMessage;
import greencity.constant.ValidationConstants;
import greencity.exception.exceptions.BadCategoryRequestException;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.BadSocialNetworkLinksException;
import greencity.exception.exceptions.BadUpdateRequestException;
import greencity.exception.exceptions.DuplicatedTagException;
import greencity.exception.exceptions.EventDtoValidationException;
import greencity.exception.exceptions.FileGenerationException;
import greencity.exception.exceptions.FileReadException;
import greencity.exception.exceptions.ImageUrlParseException;
import greencity.exception.exceptions.InvalidNumOfTagsException;
import greencity.exception.exceptions.InvalidStatusException;
import greencity.exception.exceptions.InvalidURLException;
import greencity.exception.exceptions.InvalidUnsubscribeToken;
import greencity.exception.exceptions.JsonResponseParseException;
import greencity.exception.exceptions.LanguageNotFoundException;
import greencity.exception.exceptions.LowRoleLevelException;
import greencity.exception.exceptions.MultipartXSSProcessingException;
import greencity.exception.exceptions.NotCurrentUserException;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.exception.exceptions.NotUpdatedException;
import greencity.exception.exceptions.OpenAIRequestException;
import greencity.exception.exceptions.OpenAIResponseException;
import greencity.exception.exceptions.PlaceAlreadyExistsException;
import greencity.exception.exceptions.PlaceStatusException;
import greencity.exception.exceptions.ResourceNotFoundException;
import greencity.exception.exceptions.TagNotFoundException;
import greencity.exception.exceptions.ToDoListItemNotFoundException;
import greencity.exception.exceptions.UnauthorizedException;
import greencity.exception.exceptions.UnsupportedSortException;
import greencity.exception.exceptions.UserAlreadyHasEnrolledHabitAssign;
import greencity.exception.exceptions.UserAlreadyHasHabitAssignedException;
import greencity.exception.exceptions.UserAlreadyHasMaxNumberOfActiveHabitAssigns;
import greencity.exception.exceptions.UserBlockedException;
import greencity.exception.exceptions.UserHasNoFriendWithIdException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.exception.exceptions.UserHasNoToDoListItemsException;
import greencity.exception.exceptions.UserHasReachedOutOfEnrollRange;
import greencity.exception.exceptions.UserToDoListItemStatusNotUpdatedException;
import greencity.exception.exceptions.WrongEmailException;
import greencity.exception.exceptions.WrongIdException;
import greencity.exception.helper.EndpointValidationHelper;
import jakarta.validation.ConstraintDeclarationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Custom exception handler.
 */
@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
    private final ObjectMapper objectMapper;
    private final EndpointValidationHelper endpointValidationHelper;
    private ErrorAttributes errorAttributes;

    public CustomExceptionHandler(ErrorAttributes errorAttributes, ObjectMapper objectMapper,
        EndpointValidationHelper endpointValidationHelper) {
        this.errorAttributes = errorAttributes;
        this.objectMapper = objectMapper;
        this.endpointValidationHelper = endpointValidationHelper;
    }

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
        log.warn("{} {}", ex.getStatusCode(), message);
        HttpClientErrorExceptionResponse responseBody =
            new HttpClientErrorExceptionResponse(getErrorAttributes(request), message);
        return ResponseEntity.status(ex.getStatusCode()).body(responseBody);
    }

    private Map<String, String> jsonHttpClientErrorExceptionToMap(
        HttpClientErrorException ex) throws JsonProcessingException {
        String exceptionBody = ex.getResponseBodyAsString();
        Map<String, String> httpClientResponseBody;
        if (exceptionBody.startsWith("[")) {
            httpClientResponseBody = objectMapper.readValue(ex.getResponseBodyAsString(),
                new TypeReference<List<Map<String, String>>>() {
                })
                .getFirst();
        } else {
            httpClientResponseBody = objectMapper.readValue(ex.getResponseBodyAsString(), new TypeReference<>() {
            });
        }

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
    public final ResponseEntity<Object> handleTooLargeMultipartFileRequest(MultipartException ex, WebRequest request) {
        log.warn(ex.getMessage());
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(exceptionResponse);
    }

    /**
     * Method interceptor for BadRequest-related exceptions such as
     * {@link BadRequestException}, {@link BadCategoryRequestException},
     * {@link BadUpdateRequestException}, {@link InvalidUnsubscribeToken},
     * {@link UserAlreadyHasEnrolledHabitAssign},
     * {@link UserAlreadyHasHabitAssignedException}.
     * {@link UserAlreadyHasMaxNumberOfActiveHabitAssigns},
     * {@link UserHasReachedOutOfEnrollRange}, {@link WrongEmailException},
     * {@link WrongIdException}.
     *
     * @param request Contains details about the occurred exception.
     * @return ResponseEntity which contains the HTTP status and body with the
     *         message of the exception.
     * @author Nazar Prots
     */
    @ExceptionHandler(BadRequestException.class)
    public final ResponseEntity<Object> handleBadRequestException(BadRequestException ex, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.trace(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Method interceptor for ConstraintDeclaration-related exceptions such as
     * {@link ConstraintDeclarationException}, {@link DuplicatedTagException},
     * {@link InvalidNumOfTagsException}, {@link InvalidURLException},
     * {@link BadSocialNetworkLinksException}.
     *
     * @param request Contains details about the occurred exception.
     * @return ResponseEntity which contains the HTTP status and body with the
     *         message of the exception.
     * @author Nazar Prots
     */
    @ExceptionHandler(ConstraintDeclarationException.class)
    public final ResponseEntity<Object> handleConstraintDeclarationException(ConstraintDeclarationException ex,
        WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.trace(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Method interceptor for Validation-related exceptions such as
     * {@link ValidationException}, {@link EventDtoValidationException}.
     *
     * @param request Contains details about the occurred exception.
     * @return ResponseEntity which contains the HTTP status and body with the
     *         message of the exception.
     * @author Nazar Prots
     */
    @ExceptionHandler(ValidationException.class)
    public final ResponseEntity<Object> handleValidationException(ValidationException ex, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.trace(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Method interceptor exception {@link NotCurrentUserException},
     * {@link UserHasNoPermissionToAccessException}, {@link UserBlockedException},
     * {@link LowRoleLevelException}.
     *
     * @param request contain detail about occur exception.
     * @return ResponseEntity which contain http status and body with message of
     *         exception.
     * @author Nazar Prots
     */
    @ExceptionHandler({
        NotCurrentUserException.class,
        UserHasNoPermissionToAccessException.class,
        UserBlockedException.class,
        LowRoleLevelException.class})
    public final ResponseEntity<Object> handleForbiddenException(WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.trace(exceptionResponse.getMessage(), exceptionResponse.getTrace());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exceptionResponse);
    }

    /**
     * Method interceptor exception {@link NotFoundException},
     * {@link UserHasNoFriendWithIdException}, {@link TagNotFoundException},
     * {@link LanguageNotFoundException}.
     *
     * @param request contain detail about occur exception.
     * @return ResponseEntity which contain http status and body with message of
     *         exception.
     * @author Nazar Prots
     */
    @ExceptionHandler({
        NotFoundException.class,
        UserHasNoFriendWithIdException.class,
    })
    public final ResponseEntity<Object> handleNotFoundException(WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.trace(exceptionResponse.getMessage(), exceptionResponse.getTrace());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse);
    }

    /**
     * Handles exceptions of type {@link UnsupportedSortException} thrown during request processing.
     * This method intercepts the {@code UnsupportedSortException}, logs the error, and prepares
     * a standardized error response containing details about the failed sorting operation.
     * The client receives a response with HTTP status 400 (Bad Request) and a body that includes
     * an error message describing the reason for the failure.
     *
     * @param ex      the {@link UnsupportedSortException} instance containing details about the invalid sorting
     *                parameter(s)
     * @param request the {@link WebRequest} providing context about the web request during which
     *                the exception occurred
     * @return a {@link ResponseEntity} containing an {@link ExceptionResponse} object with the error details
     *         and HTTP status 400 (BAD_REQUEST)
     */
    @ExceptionHandler(UnsupportedSortException.class)
    public final ResponseEntity<Object> handleResourceNotFoundException(UnsupportedSortException ex,
                                                                        WebRequest request) {
        log.trace(ex.getMessage(), ex);

        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        exceptionResponse.setMessage(ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
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
     * Method intercept exception {@link InvalidStatusException}
     * {@link PlaceStatusException}.
     *
     * @param request contain detail about occur exception
     * @return ResponseEntity witch contain http status and body with message of
     *         exception.
     * @author Nazar Prots
     */
    @ExceptionHandler(InvalidStatusException.class)
    public final ResponseEntity<Object> handleStatusException(InvalidStatusException ex, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.trace(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exceptionResponse);
    }

    @ExceptionHandler(PlaceAlreadyExistsException.class)
    public final ResponseEntity<Object> handlePlaceAlreadyExistsException(PlaceAlreadyExistsException ex,
        WebRequest request) {
        log.warn(ex.getMessage());
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Method interceptor for exceptions related to unsuccessful operations such as
     * {@link NotDeletedException}, {@link NotUpdatedException},
     * {@link NotSavedException} .
     *
     * @param request Contains details about the occurred exception.
     * @return ResponseEntity which contains HTTP status and body with the message
     *         of the exception.
     * @author Nazar Prots
     */
    @ExceptionHandler({
        NotDeletedException.class,
        NotUpdatedException.class,
        NotSavedException.class
    })
    public final ResponseEntity<Object> handleOperationException(WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.trace(exceptionResponse.getMessage(), exceptionResponse.getTrace());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exceptionResponse);
    }

    /**
     * Method interceptor for {@link UnsupportedOperationException}.
     *
     * @param ex      Exception which should be intercepted.
     * @param request Contains details about the occurred exception.
     * @return ResponseEntity which contains HTTP status and body with the message
     *         of the exception.
     */
    @ExceptionHandler(UnsupportedOperationException.class)
    public final ResponseEntity<Object> handleUnsupportedOperationException(UnsupportedOperationException ex,
        WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.warn(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(exceptionResponse);
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
     * Method intercept exception {@link BadSocialNetworkLinksException}.
     *
     * @param ex      Exception witch should be intercepted.
     * @param request contain detail about occur exception
     * @return ResponseEntity witch contain http status and body with message of
     *         exception.
     */
    @ExceptionHandler(BadSocialNetworkLinksException.class)
    public final ResponseEntity<Object> handleBadSocialNetworkLinkException(BadSocialNetworkLinksException ex,
        WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.warn(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Method intercept exception {@link InvalidURLException}.
     *
     * @param ex      Exception witch should be intercepted.
     * @param request contain detail about occur exception
     * @return ResponseEntity witch contain http status and body with message of
     *         exception.
     */
    @ExceptionHandler(InvalidURLException.class)
    public final ResponseEntity<Object> handleBadSocialNetworkLinkException(InvalidURLException ex,
        WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.warn(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
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

    /**
     * Method intercept exception {@link ImageUrlParseException}.
     *
     * @param ex      Exception witch should be intercepted.
     * @param request contain detail about occur exception
     * @return ResponseEntity witch contain http status and body with message of
     *         exception.
     */
    @ExceptionHandler(BadCategoryRequestException.class)
    public final ResponseEntity<Object> handleBadCategoryRequestException(BadCategoryRequestException ex,
        WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.warn(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Method intercept exception {@link ToDoListItemNotFoundException}.
     *
     * @param ex      Exception witch should be intercepted.
     * @param request contain detail about occur exception
     * @return ResponseEntity witch contain http status and body with message of
     *         exception.
     */
    @ExceptionHandler(ToDoListItemNotFoundException.class)
    public final ResponseEntity<Object> handleUserToDoListItemWhereNotSavedException(
        ToDoListItemNotFoundException ex,
        WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.warn(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Method intercept exception {@link UserHasNoToDoListItemsException}.
     *
     * @param ex      Exception witch should be intercepted.
     * @param request contain detail about occur exception
     * @return ResponseEntity witch contain http status and body with message of
     *         exception.
     */
    @ExceptionHandler(UserHasNoToDoListItemsException.class)
    public final ResponseEntity<Object> handleUserToDoListItemWhereNotSavedException(
        UserHasNoToDoListItemsException ex,
        WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.warn(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Method intercept exception {@link UserToDoListItemStatusNotUpdatedException}.
     *
     * @param ex      Exception witch should be intercepted.
     * @param request contain detail about occur exception
     * @return ResponseEntity witch contain http status and body with message of
     *         exception.
     */
    @ExceptionHandler(UserToDoListItemStatusNotUpdatedException.class)
    public final ResponseEntity<Object> handleUserToDoListItemWhereNotSavedException(
        UserToDoListItemStatusNotUpdatedException ex,
        WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.warn(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Method intercept exception {@link TagNotFoundException}.
     *
     * @param ex      Exception witch should be intercepted.
     * @param request contain detail about occur exception
     * @return ResponseEntity witch contain http status and body with message of
     *         exception.
     */
    @ExceptionHandler(TagNotFoundException.class)
    public final ResponseEntity<Object> handleTagNotFoundException(TagNotFoundException ex, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.warn(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
        HttpHeaders headers, HttpStatusCode status,
        WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.warn(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Customize the response for WrongIdException.
     *
     * @param ex      the exception
     * @param request the current request
     * @return a {@code ResponseEntity} message
     */
    @ExceptionHandler(WrongIdException.class)
    public final ResponseEntity<Object> handleWrongIdException(
        WrongIdException ex, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.warn(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
        HttpHeaders headers, HttpStatusCode status,
        WebRequest request) {
        List<ValidationExceptionDto> collect =
            ex.getBindingResult().getFieldErrors().stream()
                .map(ValidationExceptionDto::new)
                .collect(Collectors.toList());
        log.warn(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(collect);
    }

    private Map<String, Object> getErrorAttributes(WebRequest webRequest) {
        Map<String, Object> attributes = new HashMap<>(errorAttributes.getErrorAttributes(webRequest,
            ErrorAttributeOptions.of(ErrorAttributeOptions.Include.MESSAGE,
                ErrorAttributeOptions.Include.STACK_TRACE)));
        if (webRequest instanceof ServletWebRequest servletWebRequest) {
            attributes.put("path", servletWebRequest.getRequest().getRequestURI());
        }
        return attributes;
    }

    /**
     * Customize the response for MultipartXSSProcessingException.
     *
     * @param ex      the exception
     * @param request the current request
     * @return a {@code ResponseEntity} message
     */
    @ExceptionHandler(MultipartXSSProcessingException.class)
    public final ResponseEntity<Object> handleMultipartXSSProcessingException(
        MultipartXSSProcessingException ex, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.warn(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Method intercepts exception {@link ResourceNotFoundException}.
     *
     * @param ex      Exception that should be intercepted.
     * @param request Contains details about the occurred exception.
     * @return {@code ResponseEntity} which contains the HTTP status and body with
     *         the exception message.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex,
        WebRequest request) {
        log.error(ex.getMessage(), ex);

        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        exceptionResponse.setMessage(ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
        HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ResponseEntity<Object> response = endpointValidationHelper.response(ex, headers, request);
        if (response == null) {
            return super.handleHttpRequestMethodNotSupported(ex, headers, status, request);
        }
        return response;
    }

    /**
     * Method intercepts exception {@link FileReadException}.
     *
     * @param ex      Exception that should be intercepted.
     * @param request Contains details about the occurred exception.
     * @return {@code ResponseEntity} which contains the HTTP status and body with
     *         the exception message.
     */
    @ExceptionHandler(FileReadException.class)
    public final ResponseEntity<Object> handleFileReadException(FileReadException ex,
        WebRequest request) {
        log.error(ex.getMessage(), ex);

        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        exceptionResponse.setMessage(ex.getMessage());

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(exceptionResponse);
    }

    /**
     * Method intercepts exception {@link FileGenerationException}.
     *
     * @param ex      Exception that should be intercepted.
     * @param request Contains details about the occurred exception.
     * @return {@code ResponseEntity} which contains the HTTP status and body with
     *         the exception message.
     */
    @ExceptionHandler(FileGenerationException.class)
    public final ResponseEntity<Object> handleFileGenerationException(FileGenerationException ex, WebRequest request) {
        log.error(ex.getMessage(), ex);
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        exceptionResponse.setMessage(ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponse);
    }

    /**
     * Method intercepts exception {@link JsonResponseParseException}.
     *
     * @param ex      Exception that should be intercepted.
     * @param request Contains details about the occurred exception.
     * @return {@code ResponseEntity} which contains the HTTP status and body with
     *         the exception message.
     */
    @ExceptionHandler(JsonResponseParseException.class)
    public final ResponseEntity<Object> handleJsonResponseParseException(JsonResponseParseException ex,
        WebRequest request) {
        log.error(ex.getMessage(), ex);
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        exceptionResponse.setMessage(ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponse);
    }

    /**
     * Method intercepts exception {@link OpenAIResponseException}.
     *
     * @param ex      Exception that should be intercepted.
     * @param request Contains details about the occurred exception.
     * @return {@code ResponseEntity} which contains the HTTP status and body with
     *         the exception message.
     */
    @ExceptionHandler(OpenAIRequestException.class)
    public final ResponseEntity<Object> handleOpenAIResponseException(OpenAIRequestException ex, WebRequest request) {
        log.error(ex.getMessage(), ex);
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        exceptionResponse.setMessage(ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponse);
    }

    /**
     * Handles {@link UnauthorizedException} thrown when a user attempts to access a
     * resource without proper authentication credentials (e.g., missing or invalid
     * token).
     *
     * <p>
     * Returns a structured JSON response with HTTP status {@code 401 Unauthorized}.
     * </p>
     *
     * @param ex the thrown {@link UnauthorizedException}
     * @return a {@link ResponseEntity} containing an error description and HTTP 401
     *         status
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorized(UnauthorizedException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "unauthorized");
        body.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }
}
