package greencity.mapping;

import greencity.client.UserRemoteClient;
import greencity.constant.ErrorMessage;
import greencity.dto.user.UserManagementDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.exception.exceptions.WrongEmailException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserManagementDtoMapper extends AbstractConverter<User, UserManagementDto> {
    private final UserRemoteClient userRemoteClient;

    @Override
    protected UserManagementDto convert(User user) {
        Long userId = user.getId();
        UserVO userVO = userRemoteClient.findNotDeactivatedById(userId)
                .orElseThrow(() -> new WrongEmailException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId));

        return UserManagementDto.builder()
                .id(userVO.getId())
                .name(userVO.getName())
                .email(userVO.getEmail())
                .userCredo(userVO.getUserCredo())
                .role(userVO.getRole())
                .userStatus(userVO.getUserStatus())
                .build();
    }
}
