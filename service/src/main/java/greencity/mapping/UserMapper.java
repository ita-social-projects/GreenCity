package greencity.mapping;

import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.entity.UserLocation;
import greencity.repository.UserRepo;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class UserMapper extends AbstractConverter<UserVO, User> {
    private final ModelMapper modelMapper;
    private final UserRepo userRepo;

    public UserMapper(@Lazy ModelMapper modelMapper,
        UserRepo userRepo) {
        this.modelMapper = modelMapper;
        this.userRepo = userRepo;
    }

    @Override
    protected User convert(UserVO userVO) {
        Long greencityUserId = userRepo.findByEmail(userVO.getEmail())
            .map(User::getId)
            .orElse(userVO.getId());

        return User.builder()
            .id(greencityUserId)
            .name(userVO.getName())
            .email(userVO.getEmail())
            .userCredo(userVO.getUserCredo())
            .profilePicturePath(userVO.getProfilePicturePath())
            .userLocation(userVO.getUserLocation() == null
                ? null
                : modelMapper.map(userVO.getUserLocation(), UserLocation.class))
            .build();
    }
}
