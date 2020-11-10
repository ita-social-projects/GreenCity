package greencity.mapping;

import greencity.dto.user.TipsAndTricksAuthorDto;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class TipsAndTricksAuthorDtoMapper extends AbstractConverter<User, TipsAndTricksAuthorDto> {
    /**
     * Method for converting {@link User} into {@link TipsAndTricksAuthorDto}.
     *
     * @param author object to convert.
     * @return converted object.
     */
    @Override
    public TipsAndTricksAuthorDto convert(User author) {
        return new TipsAndTricksAuthorDto(author.getId(), author.getName());
    }
}