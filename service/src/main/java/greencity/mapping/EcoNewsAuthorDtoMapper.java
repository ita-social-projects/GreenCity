package greencity.mapping;

import greencity.dto.user.EcoNewsAuthorDto;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class EcoNewsAuthorDtoMapper extends AbstractConverter<User, EcoNewsAuthorDto> {
    /**
     * Method for converting {@link User} into {@link EcoNewsAuthorDto}.
     *
     * @param author object to convert.
     * @return converted object.
     */
    @Override
    public EcoNewsAuthorDto convert(User author) {
        return new EcoNewsAuthorDto(author.getId(), author.getName());
    }
}
