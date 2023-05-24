package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.user.UserProfilePictureDto;
import greencity.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
class UserProfilePictureDtoMapperTest {
    @InjectMocks
    private UserProfilePictureDtoMapper mapper;

    @Test
    void convertTest() {
        User user = ModelUtils.getUser();

        UserProfilePictureDto expected = UserProfilePictureDto.builder()
            .id(user.getId())
            .name(user.getName())
            .profilePicturePath(user.getProfilePicturePath())
            .build();

        assertEquals(expected, mapper.convert(user));
    }
}
