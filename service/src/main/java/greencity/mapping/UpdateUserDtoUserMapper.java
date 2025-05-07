package greencity.mapping;

import greencity.dto.user.UpdateUserDto;
import greencity.entity.User;
import greencity.enums.UserUpdateType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class UpdateUserDtoUserMapper {
    public User merge(UpdateUserDto updateUserDto, User user) {
        if (Objects.equals(updateUserDto.getUserUpdateType(), UserUpdateType.DELETE)) {
            if (updateUserDto.getId() != null) {
                user.setId(null);
            }
            if (updateUserDto.getName() != null) {
                user.setName(null);
            }
            if (updateUserDto.getEmail() != null) {
                user.setEmail(null);
            }
            if (updateUserDto.getProfilePicturePath() != null) {
                user.setProfilePicturePath(null);
            }
        } else {
            if (updateUserDto.getId() != null) {
                user.setId(user.getId());
            }
            if (updateUserDto.getName() != null) {
                user.setName(updateUserDto.getName());
            }
            if (updateUserDto.getEmail() != null) {
                user.setEmail(updateUserDto.getEmail());
            }
            if (updateUserDto.getProfilePicturePath() != null) {
                user.setProfilePicturePath(updateUserDto.getProfilePicturePath());
            }
        }
        return user;
    }
}
