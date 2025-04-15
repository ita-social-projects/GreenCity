package greencity.mapping;

import greencity.dto.user.UserProfilePictureDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserProfilePictureDtoMapper extends AbstractConverter<User, UserProfilePictureDto> {

    private final ModelMapper modelMapper;

    @Override
    protected UserProfilePictureDto convert(User user) {
        UserVO userVO = modelMapper.map(user, UserVO.class);

        return UserProfilePictureDto.builder()
            .id(user.getId())
            .name(userVO.getName())
            .profilePicturePath(userVO.getProfilePicturePath())
            .build();
    }
}
