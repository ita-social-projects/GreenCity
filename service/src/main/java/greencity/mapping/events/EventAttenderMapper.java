package greencity.mapping.events;

import greencity.dto.event.EventAttenderDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link User} into
 * {@link EventAttenderDto}.
 */
@Component
@RequiredArgsConstructor
public class EventAttenderMapper extends AbstractConverter<User, EventAttenderDto> {

    private final ModelMapper modelMapper;

    @Override
    protected EventAttenderDto convert(User user) {
        UserVO userVO = modelMapper.map(user, UserVO.class);

        return EventAttenderDto.builder().id(user.getId()).imagePath(userVO.getProfilePicturePath())
            .name(userVO.getName()).build();
    }
}
