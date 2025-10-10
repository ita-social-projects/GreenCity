package greencity.mapping;

import greencity.client.UserRemoteClient;
import greencity.constant.ErrorMessage;
import greencity.dto.user.UserManagementVO;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.exception.exceptions.NotFoundException;
import org.modelmapper.AbstractConverter;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class UserManagementVOMapper extends AbstractConverter<User, UserManagementVO> {
    private final UserRemoteClient userRemoteClient;

    @Lazy
    public UserManagementVOMapper(UserRemoteClient userRemoteClient) {
        this.userRemoteClient = userRemoteClient;
    }

    @Override
    protected UserManagementVO convert(User user) {
        String email = user.getEmail();
        UserVO userVO = userRemoteClient.findByEmail(email)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));

        return UserManagementVO.builder()
            .id(user.getId())
            .name(user.getName())
            .email(email)
            .userCredo(user.getUserCredo())
            .role(userVO.getRole())
            .status(user.getStatus())
            .rating(user.getRating())
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
