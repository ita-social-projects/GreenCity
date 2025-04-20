package greencity.mapping;

import greencity.dto.user.UserProfilePictureDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class UserProfilePictureDtoMapper extends AbstractConverter<User, UserProfilePictureDto> {
    private final ModelMapper modelMapper;

    @Lazy
    public UserProfilePictureDtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    protected UserProfilePictureDto convert(User user) {
        UserVO userVO = modelMapper.map(user, UserVO.class);

        return UserProfilePictureDto.builder()
            .id(user.getId())
            .name(user.getName())
            .profilePicturePath(userVO.getProfilePicturePath())
            .build();
    }
}
