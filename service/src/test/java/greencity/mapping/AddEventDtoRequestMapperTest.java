package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.event.AddEventDtoRequest;
import greencity.entity.Event;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
class AddEventDtoRequestMapperTest {
    @InjectMocks
    AddEventDtoRequestMapper mapper;

    @Test
    void convertTest() {
        Event expected = ModelUtils.getEvent();

        AddEventDtoRequest request = ModelUtils.addEventDtoRequest;

        assertEquals(expected.getTitle(), mapper.convert(request).getTitle());
    }
}
