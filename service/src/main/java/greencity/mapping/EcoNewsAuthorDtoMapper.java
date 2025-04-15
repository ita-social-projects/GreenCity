package greencity.mapping;

import greencity.dto.user.EcoNewsAuthorDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class EcoNewsAuthorDtoMapper extends AbstractConverter<User, EcoNewsAuthorDto> {

    private final ModelMapper modelMapper;

    @Lazy
    public EcoNewsAuthorDtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

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
