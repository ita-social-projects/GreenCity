package greencity.mapping;

import greencity.client.UserRemoteClient;
import greencity.constant.ErrorMessage;
import greencity.dto.user.UserForListDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.exception.exceptions.WrongEmailException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserForListDtoMapper extends AbstractConverter<User, UserForListDto> {
    private final UserRemoteClient userRemoteClient;

    @Override
    protected UserForListDto convert(User user) {
        Long userId = user.getId();
        UserVO userVO = userRemoteClient.findNotDeactivatedById(userId)
            .orElseThrow(() -> new WrongEmailException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId));

        return UserForListDto.builder()
            .id(userVO.getId())
            .name(userVO.getName())
            .dateOfRegistration(userVO.getDateOfRegistration())
            .email(userVO.getEmail())
            .userStatus(userVO.getUserStatus())
            .role(userVO.getRole())
            .userCredo(userVO.getUserCredo())
            .build();
    }
}
