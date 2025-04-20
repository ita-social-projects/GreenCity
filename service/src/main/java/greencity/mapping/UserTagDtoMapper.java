package greencity.mapping;

import greencity.dto.user.UserTagDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class UserTagDtoMapper extends AbstractConverter<User, UserTagDto> {

    private final ModelMapper modelMapper;

    @Lazy
    public UserTagDtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    protected UserTagDto convert(User user) {
        UserVO userVO = modelMapper.map(user, UserVO.class);

        return UserTagDto.builder()
            .userId(user.getId())
            .userName(user.getName())
            .profilePicture(userVO.getProfilePicturePath())
            .build();
    }
}
