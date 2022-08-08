package greencity.mapping.events;

import greencity.dto.event.EventAttenderDto;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link User} into
 * {@link EventAttenderDto}.
 */
@Component
public class EventAttenderMapper extends AbstractConverter<User, EventAttenderDto> {
    @Override
    protected EventAttenderDto convert(User user) {
        return EventAttenderDto.builder().id(user.getId()).imagePath(user.getProfilePicturePath())
            .name(user.getName()).build();
    }
}
