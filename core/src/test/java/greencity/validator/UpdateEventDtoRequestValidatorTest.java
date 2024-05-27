package greencity.validator;

import greencity.ModelUtils;
import greencity.dto.event.UpdateEventRequestDto;
import greencity.exception.exceptions.EventDtoValidationException;
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
class UpdateEventDtoRequestValidatorTest {
    @InjectMocks
    private UpdateEventDtoRequestValidator validator;

    @Test
    void updateWithTooManyTagsException() {
        UpdateEventRequestDto updateEventDto = ModelUtils.getUpdateEventDtoWithTooManyDates();
        assertThrows(EventDtoValidationException.class, () -> validator.isValid(updateEventDto, null));
    }

    @Test
    void updateWithEmptyDateLocations() {
        UpdateEventRequestDto updateEventDto = ModelUtils.getUpdateEventDtoWithEmptyDateLocations();
        assertThrows(EventDtoValidationException.class, () -> validator.isValid(updateEventDto, null));
    }

    @Test
    void updateEventDtoWithoutDates() {
        UpdateEventRequestDto updateEventDto = ModelUtils.getUpdateEventDtoWithoutDates();
        assertThrows(EventDtoValidationException.class, () -> validator.isValid(updateEventDto, null));

    }

    @Test
    void updateWithInvalidLinkException() {
        UpdateEventRequestDto updateEventDto = ModelUtils.getUpdateEventWithoutAddressAndLink();
        assertThrows(EventDtoValidationException.class, () -> validator.isValid(updateEventDto, null));
    }

    @Test
    void updateWithoutLinkAndCoordinates() {
        UpdateEventRequestDto updateEventDto = ModelUtils.getUpdateEventDto();
        updateEventDto.getDatesLocations().forEach(e -> e.setOnlineLink(null));
        updateEventDto.getDatesLocations().forEach(e -> e.setCoordinates(null));
        assertThrows(EventDtoValidationException.class, () -> validator.isValid(updateEventDto, null));
    }

    @Test
    void validEventUpdate() {
        UpdateEventRequestDto updateEventDto = ModelUtils.getUpdateEventDto();
        assertTrue(validator.isValid(updateEventDto, null));
    }

    @Test
    void updateEventWithSameDates() {
        UpdateEventRequestDto updateEventDto = ModelUtils.getUpdateEventDto();
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneOffset.UTC).plusHours(2L);
        updateEventDto.getDatesLocations().forEach(e -> e.setStartDate(zonedDateTime));
        updateEventDto.getDatesLocations().forEach(e -> e.setFinishDate(zonedDateTime));
        assertThrows(EventDtoValidationException.class, () -> validator.isValid(updateEventDto, null));
    }

    @Test
    void invalidObjectType() {
        Object value = new Object();
        assertFalse(validator.isValid(value, null));
    }
}