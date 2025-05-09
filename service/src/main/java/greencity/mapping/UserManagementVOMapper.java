package greencity.mapping;

import greencity.client.UserRemoteClient;
import greencity.dto.user.UserManagementVO;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class UserManagementVOMapper extends AbstractConverter<User, UserManagementVO> {
    private final ModelMapper modelMapper;
    private final UserRemoteClient userRemoteClient;

    @Lazy
    public UserManagementVOMapper(ModelMapper modelMapper, UserRemoteClient userRemoteClient) {
        this.modelMapper = modelMapper;
        this.userRemoteClient = userRemoteClient;
    }

    @Override
    protected UserManagementVO convert(User user) {
        UserVO userVO = modelMapper.map(user, UserVO.class);

        return UserManagementVO.builder()
            .id(user.getId())
            .name(user.getName())
            .email(userRemoteClient.findUserEmailByUserId(user.getId()))
            .userCredo(user.getUserCredo())
            .role(userVO.getRole())
            .userStatus(userVO.getUserStatus())
            .build();
    }

    /**
     * Method to convert {@link Page} of {@link User} to {@link Page} of
     * {@link UserManagementVO}.
     *
     * @param users {@link Page} of {@link User}
     * @return {@link Page} of {@link UserManagementVO}
     * @author Anton Bondar
     */
    public Page<UserManagementVO> mapAllToPage(Page<User> users) {
        return users.map(this::convert);
    }
}
