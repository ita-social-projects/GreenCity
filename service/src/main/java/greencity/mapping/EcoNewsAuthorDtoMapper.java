package greencity.mapping;

import greencity.dto.user.EcoNewsAuthorDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EcoNewsAuthorDtoMapper extends AbstractConverter<User, EcoNewsAuthorDto> {

    private final ModelMapper modelMapper;

    /**
     * Method for converting {@link User} into {@link EcoNewsAuthorDto}.
     *
     * @param author object to convert.
     * @return converted object.
     */
    @Override
    public EcoNewsAuthorDto convert(User author) {
        UserVO authorVO = modelMapper.map(author, UserVO.class);
        return new EcoNewsAuthorDto(author.getId(), authorVO.getName());
    }
}
