package greencity.validator;

import greencity.annotations.ValidUpdateEventDtoRequest;
import greencity.constant.ErrorMessage;
import greencity.constant.ValidationConstants;
import greencity.dto.event.UpdateEventDateLocationDto;
import greencity.dto.event.UpdateEventRequestDto;
import greencity.exception.exceptions.EventDtoValidationException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static greencity.validator.UrlValidator.isUrlValid;

public class UpdateEventDtoRequestValidator implements ConstraintValidator<ValidUpdateEventDtoRequest, Object> {
    private static final int MAX_YEARS_OF_PLANNING = 10;

    /**
     * Validates whether the provided value adheres to the constraints defined for
     * an event DTO. The validation includes checking event date locations, ensuring
     * they fall within specified bounds, validating the tags count, and checking
     * the validity of online links. Time zones of the event start dates are
     * converted to Greenwich Mean Time (GMT) for comparison.
     *
     * @param value   The object to be validated, expected to be an instance of
     *                AddEventDtoRequest or UpdateEventDto.
     * @param context The context in which the validation is performed.
     * @return True if the value is valid according to the defined constraints,
     *         otherwise false.
     * @throws EventDtoValidationException if the value does not meet the validation
     *                                     criteria.
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        List<UpdateEventDateLocationDto> eventDateLocationDtos;

        if (value instanceof UpdateEventRequestDto updateEventDto) {
            if (updateEventDto.getDatesLocations() == null || updateEventDto.getDatesLocations().isEmpty()
                || updateEventDto.getDatesLocations().size() > ValidationConstants.MAX_EVENT_DATES_AMOUNT) {
                throw new EventDtoValidationException(ErrorMessage.WRONG_COUNT_OF_EVENT_DATES);
            } else {
                eventDateLocationDtos = convertToUTC(updateEventDto.getDatesLocations());
            }
        } else {
            return false;
        }
        validateEventDateLocations(eventDateLocationDtos);
        validateTags(value);
        return true;
    }

    private List<UpdateEventDateLocationDto> convertToUTC(List<UpdateEventDateLocationDto> dates) {
        return dates.stream()
            .map(e -> e.setStartDate(e.getStartDate().withZoneSameInstant(ZoneOffset.UTC)))
            .map(e -> e.setFinishDate(e.getFinishDate().withZoneSameInstant(ZoneOffset.UTC)))
            .toList();
    }

    private void validateEventDateLocations(List<UpdateEventDateLocationDto> eventDateLocationDtos) {
        Set<LocalDate> startDateSet = new HashSet<>();
        Set<LocalDate> finishDateSet = new HashSet<>();

        for (var eventDateLocationDto : eventDateLocationDtos) {
            LocalDate startDate = eventDateLocationDto.getStartDate().toLocalDate();
            LocalDate finishDate = eventDateLocationDto.getFinishDate().toLocalDate();

            if (!startDateSet.add(startDate) || !finishDateSet.add(finishDate)) {
                throw new EventDtoValidationException(ErrorMessage.SAME_EVENT_DATES);
            }

            if (eventDateLocationDto.getStartDate().isBefore(ZonedDateTime.now(ZoneOffset.UTC))
                || eventDateLocationDto.getStartDate().isBefore(eventDateLocationDto.getFinishDate().minusDays(1L))
                || eventDateLocationDto.getStartDate().isAfter(eventDateLocationDto.getFinishDate())
                || eventDateLocationDto.getStartDate().isAfter(ZonedDateTime.now(ZoneOffset.UTC)
                    .plusYears(MAX_YEARS_OF_PLANNING))) {
                throw new EventDtoValidationException(ErrorMessage.EVENT_START_DATE_AFTER_FINISH_DATE_OR_IN_PAST);
            }

            if (eventDateLocationDto.getOnlineLink() == null && eventDateLocationDto.getCoordinates() == null) {
                throw new EventDtoValidationException(ErrorMessage.NO_EVENT_LINK_OR_ADDRESS);
            }

            if (eventDateLocationDto.getOnlineLink() != null) {
                isUrlValid(eventDateLocationDto.getOnlineLink());
            }
        }
    }

    private void validateTags(Object value) {
        int tagsSize = ((UpdateEventRequestDto) value).getTags().size();

        if (tagsSize > ValidationConstants.MAX_AMOUNT_OF_TAGS) {
            throw new EventDtoValidationException(ErrorMessage.WRONG_COUNT_OF_TAGS_EXCEPTION);
        }
    }
}