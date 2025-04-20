package greencity.mapping.events;

import greencity.dto.event.EventAttenderDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link User} into
 * {@link EventAttenderDto}.
 */
@Component
public class EventAttenderMapper extends AbstractConverter<User, EventAttenderDto> {
    private final ModelMapper modelMapper;

    @Lazy
    public EventAttenderMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    protected EventAttenderDto convert(User user) {
        UserVO userVO = modelMapper.map(user, UserVO.class);

        return EventAttenderDto.builder().id(user.getId()).imagePath(userVO.getProfilePicturePath())
            .name(user.getName()).build();
    }
}
