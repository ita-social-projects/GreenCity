package greencity.validator;

import greencity.ModelUtils;
import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.UpdateEventDto;
import greencity.exception.exceptions.EventDtoValidationException;
import greencity.exception.exceptions.InvalidURLException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class EventDtoRequestValidatorTest {
    @InjectMocks
    private EventDtoRequestValidator validator;

    @Test
    void withoutDatesException() {
        AddEventDtoRequest addEventDtoRequest = ModelUtils.getEventDtoWithoutDates();
        assertThrows(EventDtoValidationException.class, () -> validator.isValid(addEventDtoRequest, null));
    }

    @Test
    void withZeroDatesException() {
        AddEventDtoRequest addEventDtoRequest = ModelUtils.getEventDtoWithZeroDates();
        assertThrows(EventDtoValidationException.class, () -> validator.isValid(addEventDtoRequest, null));
    }

    @Test
    void withTooManyDatesException() {
        AddEventDtoRequest addEventDtoRequest = ModelUtils.getEventDtoWithTooManyDates();
        assertThrows(EventDtoValidationException.class, () -> validator.isValid(addEventDtoRequest, null));
    }

    @Test
    void withStartDateInPastException() {
        AddEventDtoRequest addEventDtoRequest = ModelUtils.getEventWithPastStartDate();
        assertThrows(EventDtoValidationException.class, () -> validator.isValid(addEventDtoRequest, null));
    }

    @Test
    void withStartDateAfterFinishDateException() {
        AddEventDtoRequest addEventDtoRequest = ModelUtils.getEventWithStartDateAfterFinishDate();
        assertThrows(EventDtoValidationException.class, () -> validator.isValid(addEventDtoRequest, null));
    }

    @Test
    void withoutAddressAndLinkException() {
        AddEventDtoRequest addEventDtoRequest = ModelUtils.getEventWithoutAddressAndLink();
        assertThrows(EventDtoValidationException.class, () -> validator.isValid(addEventDtoRequest, null));
    }

    @Test
    void withInvalidLinkException() {
        AddEventDtoRequest addEventDtoRequest = ModelUtils.getEventWithInvalidLink();
        assertThrows(InvalidURLException.class, () -> validator.isValid(addEventDtoRequest, null));
    }

    @Test
    void withTooManyTagsException() {
        AddEventDtoRequest addEventDtoRequest = ModelUtils.getEventWithTooManyTags();
        assertThrows(EventDtoValidationException.class, () -> validator.isValid(addEventDtoRequest, null));
    }

    @Test
    void updateWithTooManyTagsException() {
        UpdateEventDto updateEventDto = ModelUtils.getUpdateEventDtoWithTooManyDates();
        assertThrows(EventDtoValidationException.class, () -> validator.isValid(updateEventDto, null));
    }

    @Test
    void updateWithEmptyDateLocations() {
        UpdateEventDto updateEventDto = ModelUtils.getUpdateEventDtoWithEmptyDateLocations();
        assertThrows(EventDtoValidationException.class, () -> validator.isValid(updateEventDto, null));
    }

    @Test
    void updateEventDtoWithoutDates() {
        UpdateEventDto updateEventDto = ModelUtils.getUpdateEventDtoWithoutDates();
        assertThrows(EventDtoValidationException.class, () -> validator.isValid(updateEventDto, null));

    }

    @Test
    void updateWithInvalidLinkException() {
        UpdateEventDto updateEventDto = ModelUtils.getUpdateEventWithoutAddressAndLink();
        assertThrows(EventDtoValidationException.class, () -> validator.isValid(updateEventDto, null));
    }

    @Test
    void updateWithoutLinkAndCoordinates() {
        UpdateEventDto updateEventDto = ModelUtils.getUpdateEventDto();
        updateEventDto.getDatesLocations().forEach(e -> e.setOnlineLink(null));
        updateEventDto.getDatesLocations().forEach(e -> e.setCoordinates(null));
        assertThrows(EventDtoValidationException.class, () -> validator.isValid(updateEventDto, null));
    }

    @Test
    void validEvent() {
        AddEventDtoRequest addEventDtoRequest = ModelUtils.getAddEventDtoRequest();
        assertTrue(validator.isValid(addEventDtoRequest, null));
    }

    @Test
    void validEventUpdate() {
        UpdateEventDto updateEventDto = ModelUtils.getUpdateEventDto();
        assertTrue(validator.isValid(updateEventDto, null));
    }

    @Test
    void saveEventWithSameDates() {
        AddEventDtoRequest addEventDto = ModelUtils.getAddEventDtoRequest();
        addEventDto.getDatesLocations().forEach(e -> e.setStartDate(ZonedDateTime.now(ZoneOffset.UTC).plusHours(2L)));
        addEventDto.getDatesLocations().forEach(e -> e.setFinishDate(ZonedDateTime.now(ZoneOffset.UTC).plusHours(2L)));
        assertThrows(EventDtoValidationException.class, () -> validator.isValid(addEventDto, null));
    }

    @Test
    void updateEventWithSameDates() {
        UpdateEventDto updateEventDto = ModelUtils.getUpdateEventDto();
        updateEventDto.getDatesLocations().forEach(e -> e.setStartDate(ZonedDateTime.now(ZoneOffset.UTC)));
        assertThrows(EventDtoValidationException.class, () -> validator.isValid(updateEventDto, null));
    }

    @Test
    void invalidObjectType() {
        Object value = new Object();
        assertFalse(validator.isValid(value, null));
    }
}