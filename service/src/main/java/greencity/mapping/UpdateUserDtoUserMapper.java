package greencity.mapping;

import greencity.dto.user.UpdateUserDto;
import greencity.entity.Language;
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
            if (updateUserDto.getUserLocation() != null) {
                user.setUserLocation(null);
            }
            if (updateUserDto.getUserCredo() != null) {
                user.setUserCredo(null);
            }
            if (updateUserDto.getName() != null) {
                user.setName(null);
            }
            if (updateUserDto.getProfilePicturePath() != null) {
                user.setProfilePicturePath(null);
            }
            if (updateUserDto.getEventOrganizerRating() != null) {
                user.setEventOrganizerRating(null);
            }
            if (updateUserDto.getLanguage() != null) {
                user.setLanguage(null);
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
            if (updateUserDto.getUserCredo() != null) {
                user.setUserCredo(updateUserDto.getUserCredo());
            }
            if (updateUserDto.getLanguage() != null) {
                user.setLanguage(Language.builder()
                    .id(updateUserDto.getLanguage().getId())
                    .code(updateUserDto.getLanguage().getCode())
                    .build());
            }
        }
        return user;
    }
}
