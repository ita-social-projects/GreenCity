package greencity.mapping;

import greencity.client.UserRemoteClient;
import greencity.constant.ErrorMessage;
import greencity.dto.user.UserManagementVO;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.WrongEmailException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserManagementVOMapper extends AbstractConverter<User, UserManagementVO> {

    private final UserRemoteClient userRemoteClient;

    @Override
    protected UserManagementVO convert(User user) {
        String userEmail = user.getEmail();
        UserVO userVO = userRemoteClient.findNotDeactivatedByEmail(userEmail)
                .orElseThrow(() -> new WrongEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + userEmail));

        return UserManagementVO.builder()
            .id(user.getId())
            .name(userVO.getName())
            .email(user.getEmail())
            .userCredo(userVO.getUserCredo())
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
