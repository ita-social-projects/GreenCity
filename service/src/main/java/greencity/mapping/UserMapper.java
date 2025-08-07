package greencity.mapping;


import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.entity.UserLocation;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class UserMapper extends AbstractConverter<UserVO, User> {
    private final ModelMapper modelMapper;

    @Lazy
    public UserMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    protected User convert(UserVO userVO) {
        return User.builder()
            .id(userVO.getId())
            .name(userVO.getName())
            .userCredo(userVO.getUserCredo())
            .profilePicturePath(userVO.getProfilePicturePath())
            .userLocation(modelMapper.map(userVO.getUserLocation(), UserLocation.class))
            .build();
    }
}
