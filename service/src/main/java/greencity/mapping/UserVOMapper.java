package greencity.mapping;

import greencity.client.UserRemoteClient;
import greencity.constant.ErrorMessage;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.exception.exceptions.WrongEmailException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserVOMapper extends AbstractConverter<User, UserVO> {

    private final UserRemoteClient userRemoteClient;

    @Override
    protected UserVO convert(User user) {
        String userEmail = user.getEmail();
        return userRemoteClient.findNotDeactivatedByEmail(userEmail)
                .orElseThrow(() -> new WrongEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + userEmail));
    }
}