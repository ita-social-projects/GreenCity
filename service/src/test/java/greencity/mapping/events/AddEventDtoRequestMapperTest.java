package greencity.mapping.events;

import greencity.ModelUtils;
import greencity.dto.event.AddEventDtoRequest;
import greencity.entity.event.Event;
import greencity.exception.exceptions.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
class AddEventDtoRequestMapperTest {
    @Mock
    private AddressDtoMapper addressDtoMapper;
    @InjectMocks
    private AddEventDtoRequestMapper mapper;

    @Test
    void convertTest() {
        Event expected = ModelUtils.getEvent();

        AddEventDtoRequest request = ModelUtils.addEventDtoRequest;

        assertEquals(expected.getTitle(), mapper.convert(request).getTitle());
    }

    @Test
    void convertTestWithoutAddress() {
        Event expected = ModelUtils.getEventWithoutAddress();

        AddEventDtoRequest request = ModelUtils.addEventDtoWithoutAddressRequest;

        assertEquals(expected.getTitle(), mapper.convert(request).getTitle());
    }

    @Test
    void convertTestWithoutAddressOrOnlineLink(){
        AddEventDtoRequest request = ModelUtils.addEventDtoWithoutAddressRequest;
        request.getDatesLocations().get(0).setOnlineLink(null);
        assertThrows(BadRequestException.class,()-> mapper.convert(request));
    }
}
