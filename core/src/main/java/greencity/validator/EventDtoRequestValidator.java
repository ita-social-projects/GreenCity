package greencity.validator;

import greencity.annotations.ValidEventDtoRequest;
import greencity.constant.ErrorMessage;
import greencity.constant.ValidationConstants;
import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.AddressDto;
import greencity.dto.event.EventDateLocationDto;
import greencity.exception.exceptions.EventDtoValidationException;

import static greencity.validator.UrlValidator.isUrlValid;

import java.time.ZonedDateTime;
import java.util.List;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EventDtoRequestValidator implements ConstraintValidator<ValidEventDtoRequest, AddEventDtoRequest> {
    @Override
    public void initialize(ValidEventDtoRequest constraintAnnotation) {
        // Initializes the validator in preparation for #isValid calls
    }

    @Override
    public boolean isValid(AddEventDtoRequest value, ConstraintValidatorContext context) {
        List<EventDateLocationDto> eventDateLocationDtos = value.getDatesLocations();

        if (eventDateLocationDtos == null || eventDateLocationDtos.isEmpty()
            || eventDateLocationDtos.size() > ValidationConstants.MAX_EVENT_DATES_AMOUNT) {
            throw new EventDtoValidationException(ErrorMessage.WRONG_COUNT_OF_EVENT_DATES);
        }

        for (var eventDateLocationDto : eventDateLocationDtos) {
            if (eventDateLocationDto.getStartDate().isBefore(ZonedDateTime.now())
                || eventDateLocationDto.getStartDate().isAfter(eventDateLocationDto.getFinishDate())) {
                throw new EventDtoValidationException(ErrorMessage.EVENT_START_DATE_AFTER_FINISH_DATE_OR_IN_PAST);
            }
            AddressDto addressDto = eventDateLocationDto.getCoordinates();
            String onlineLink = eventDateLocationDto.getOnlineLink();
            if (onlineLink == null && addressDto == null) {
                throw new EventDtoValidationException(ErrorMessage.NO_EVENT_LINK_OR_ADDRESS);
            }
            if (onlineLink != null) {
                isUrlValid(onlineLink);
            }
        }

        if (value.getTags().size() > ValidationConstants.MAX_AMOUNT_OF_TAGS) {
            throw new EventDtoValidationException(ErrorMessage.WRONG_COUNT_OF_TAGS_EXCEPTION);
        }

        return true;
    }
}