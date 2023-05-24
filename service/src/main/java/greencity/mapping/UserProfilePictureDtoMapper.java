package greencity.mapping;

import greencity.dto.user.UserProfilePictureDto;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class UserProfilePictureDtoMapper extends AbstractConverter<User, UserProfilePictureDto> {
    @Override
    protected UserProfilePictureDto convert(User user) {
        return UserProfilePictureDto.builder()
            .id(user.getId())
            .name(user.getName())
            .profilePicturePath(user.getProfilePicturePath())
            .build();
    }
}
