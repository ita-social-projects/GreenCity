package greencity.mapping;

import greencity.dto.friends.TagFriendDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static greencity.ModelUtils.getTagFriendDto;
import static greencity.ModelUtils.getTagUser;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class TagFriendDtoMapperTest {
    @InjectMocks
    TagFriendDtoMapper mapper;

    @Test
    void convertTest() {
        var user = getTagUser();
        var expected = getTagFriendDto();
        TagFriendDto actual = mapper.convert(user);
        assertEquals(expected, actual);
    }
}
