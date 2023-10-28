package greencity.mapping;

import greencity.dto.user.TagFriendDto;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class TagFriendDtoMapper extends AbstractConverter<User, TagFriendDto> {
    @Override
    protected TagFriendDto convert(User user) {
        return TagFriendDto.builder()
            .friendId(user.getId())
            .friendName(user.getName())
            .profilePicture(user.getProfilePicturePath())
            .build();
    }
}
