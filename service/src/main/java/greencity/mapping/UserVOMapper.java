package greencity.mapping;

import greencity.client.UserRemoteClient;
import greencity.constant.ErrorMessage;
import greencity.dto.location.UserLocationDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.entity.UserLocation;
import greencity.exception.exceptions.WrongEmailException;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class UserVOMapper extends AbstractConverter<User, UserVO> {
    private final ModelMapper modelMapper;
    private final UserRemoteClient userRemoteClient;

    @Lazy
    public UserVOMapper(ModelMapper modelMapper, UserRemoteClient userRemoteClient) {
        this.modelMapper = modelMapper;
        this.userRemoteClient = userRemoteClient;
    }

    @Override
    protected UserVO convert(User user) {
        String email = user.getEmail();
        UserVO userVO = userRemoteClient.findNotDeactivatedByEmail(email)
            .orElseThrow(() -> new WrongEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));
        UserLocation userLocation = user.getUserLocation();
        if (userLocation != null) {
            UserLocationDto userLocationDto = modelMapper.map(userLocation, UserLocationDto.class);
            userVO.setUserLocation(userLocationDto);
        }
        userVO.setUserCredo(user.getUserCredo());

        return userVO;
    }
}