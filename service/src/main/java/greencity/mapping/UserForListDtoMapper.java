package greencity.mapping;

import greencity.client.UserRemoteClient;
import greencity.constant.ErrorMessage;
import greencity.dto.user.UserForListDto;
import greencity.dto.user.UserVOAdvancedDto;
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
        Long id = user.getId();
        UserVOAdvancedDto userVO = userRemoteClient.findNotDeactivatedByIdAdvanced(id)
            .orElseThrow(() -> new WrongEmailException(ErrorMessage.USER_NOT_FOUND_BY_ID + id));

        return UserForListDto.builder()
            .id(userVO.getId())
            .name(userVO.getName())
            .dateOfRegistration(userVO.getDateOfRegistration())
            .email(userVO.getEmail())
            .userStatus(userVO.getUserStatus())
            .role(userVO.getRole())
            .userCredo(user.getUserCredo())
            .build();
    }
}
