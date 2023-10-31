package greencity.mapping;

import greencity.dto.user.UserTagDto;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class UserTagDtoMapper extends AbstractConverter<User, UserTagDto> {
    @Override
    protected UserTagDto convert(User user) {
        return UserTagDto.builder()
            .userId(user.getId())
            .userName(user.getName())
            .profilePicture(user.getProfilePicturePath())
            .build();
    }
}
