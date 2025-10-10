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
        UserVO userVO = userRemoteClient.findByEmail(email)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));

        return UserManagementDto.builder()
            .id(user.getId())
            .name(user.getName())
            .email(email)
            .userCredo(user.getUserCredo())
            .role(userVO.getRole())
            .status(user.getStatus())
            .build();
    }
}
