package greencity.mapping.events;

import greencity.ModelUtils;
import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.AddressDto;
import greencity.entity.event.Event;
import greencity.exception.exceptions.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        AddressDto addressDto = ModelUtils.getAddressDto();

        when(addressDtoMapper.convert(addressDto)).thenReturn(ModelUtils.getAddress());
        assertEquals(expected.getTitle(), mapper.convert(request).getTitle());
        verify(addressDtoMapper).convert(addressDto);
    }

    @Test
    void convertTestWithoutAddress() {
        Event expected = ModelUtils.getEventWithoutAddress();

        AddEventDtoRequest request = ModelUtils.addEventDtoWithoutAddressRequest;

        assertEquals(expected.getTitle(), mapper.convert(request).getTitle());
    }

    @Test
    void convertTestWithoutOnlineLink() {
        Event expected = ModelUtils.getEvent();
        AddEventDtoRequest request = ModelUtils.addEventDtoWithoutLinkRequest;
        AddressDto addressDto = ModelUtils.getAddressDto();

        when(addressDtoMapper.convert(addressDto)).thenReturn(ModelUtils.getAddress());
        assertEquals(expected.getTitle(), mapper.convert(request).getTitle());
        verify(addressDtoMapper).convert(addressDto);
    }

    @Test
    void convertTestWithoutAddressAndOnlineLink() {
        AddEventDtoRequest request = ModelUtils.addEventDtoWithoutAddressAndLinkRequest;
        assertThrows(BadRequestException.class, () -> mapper.convert(request));
    }

    @Test
    void convertTestWithNullStreetUa() {
        AddEventDtoRequest request = ModelUtils.addEventDtoRequestWithNullStreetUa;
        assertThrows(BadRequestException.class, () -> mapper.convert(request));
    }

    @Test
    void convertTestWithNullCityUa() {
        AddEventDtoRequest request = ModelUtils.addEventDtoRequestWithNullCityUa;
        assertThrows(BadRequestException.class, () -> mapper.convert(request));
    }

    @Test
    void convertTestWithNullRegionUa() {
        AddEventDtoRequest request = ModelUtils.addEventDtoRequestWithNullRegionUa;
        assertThrows(BadRequestException.class, () -> mapper.convert(request));
    }

    @Test
    void convertTestWithNullCountryUa() {
        AddEventDtoRequest request = ModelUtils.addEventDtoRequestWithNullCountryUa;
        assertThrows(BadRequestException.class, () -> mapper.convert(request));
    }

    @Test
    void convertTestWhenAddressDtoWithNullData() {
        AddEventDtoRequest request = ModelUtils.addEventDtoRequestWithNullData;
        assertThrows(BadRequestException.class, () -> mapper.convert(request));
    }
}
