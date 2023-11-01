package greencity.mapping;

import greencity.dto.user.UserTagDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static greencity.ModelUtils.getTagUser;

import static greencity.ModelUtils.getUserTagDto;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UserTagDtoMapperTest {
    @InjectMocks
    UserTagDtoMapper mapper;

    @Test
    void convertTest() {
        var user = getTagUser();
        var expected = getUserTagDto();
        UserTagDto actual = mapper.convert(user);
        assertEquals(expected, actual);
    }
}
