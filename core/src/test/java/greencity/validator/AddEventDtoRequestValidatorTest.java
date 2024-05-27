package greencity.validator;

import greencity.ModelUtils;
import greencity.dto.event.AddEventDtoRequest;
import greencity.exception.exceptions.EventDtoValidationException;
import greencity.exception.exceptions.InvalidURLException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
class AddEventDtoRequestValidatorTest {
    @InjectMocks
    private AddEventDtoRequestValidator validator;

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
    void validEvent() {
        AddEventDtoRequest addEventDtoRequest = ModelUtils.getAddEventDtoRequest();
        assertTrue(validator.isValid(addEventDtoRequest, null));
    }

    @Test
    void saveEventWithSameDates() {
        AddEventDtoRequest addEventDto = ModelUtils.getAddEventDtoRequest();
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneOffset.UTC).plusHours(2L);
        addEventDto.getDatesLocations().forEach(e -> e.setStartDate(zonedDateTime));
        addEventDto.getDatesLocations().forEach(e -> e.setFinishDate(zonedDateTime));
        assertThrows(EventDtoValidationException.class, () -> validator.isValid(addEventDto, null));
    }

    @Test
    void invalidObjectType() {
        Object value = new Object();
        assertFalse(validator.isValid(value, null));
    }
}