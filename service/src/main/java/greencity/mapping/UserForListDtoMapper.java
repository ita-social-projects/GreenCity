package greencity.mapping;

import greencity.client.UserRemoteClient;
import greencity.constant.ErrorMessage;
import greencity.dto.user.UserForListDto;
import greencity.dto.user.UserVOAdvancedDto;
import greencity.entity.User;
import greencity.exception.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserForListDtoMapper extends AbstractConverter<User, UserForListDto> {
    private final UserRemoteClient userRemoteClient;

    @Override
    protected UserForListDto convert(User user) {
        String email = user.getEmail();
        UserVOAdvancedDto userVO = userRemoteClient.findByEmailAdvanced(email)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));

        return UserForListDto.builder()
            .id(user.getId())
            .name(user.getName())
            .dateOfRegistration(userVO.getDateOfRegistration())
            .email(email)
            .userStatus(user.getStatus())
            .role(userVO.getRole())
            .userCredo(user.getUserCredo())
            .build();
    }
}
