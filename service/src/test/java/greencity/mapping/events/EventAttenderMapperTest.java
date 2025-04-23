package greencity.mapping.events;

import greencity.ModelUtils;
import greencity.dto.event.EventAttenderDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class EventAttenderMapperTest {

    @InjectMocks
    EventAttenderMapper mapper;

    @Test
    void convertTest() {
        User user = ModelUtils.getUser();
        EventAttenderDto expected = ModelUtils.getEventAttenderDto();

        assertEquals(expected, mapper.convert(user));
    }
}
