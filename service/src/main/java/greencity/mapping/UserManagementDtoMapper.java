package greencity.mapping;

import greencity.client.UserRemoteClient;
import greencity.constant.ErrorMessage;
import greencity.dto.user.UserManagementDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.exception.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserManagementDtoMapper extends AbstractConverter<User, UserManagementDto> {
    private final UserRemoteClient userRemoteClient;

    @Override
    protected UserManagementDto convert(User user) {
        String email = user.getEmail();
        UserVO userVO = userRemoteClient.findNotDeactivatedByEmail(email)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));
        userVO.setId(user.getId());
        userVO.setUserCredo(user.getUserCredo());

        return UserManagementDto.builder()
            .id(userVO.getId())
            .name(userVO.getName())
            .email(userVO.getEmail())
            .userCredo(user.getUserCredo())
            .role(userVO.getRole())
            .userStatus(userVO.getUserStatus())
            .build();
    }
}
