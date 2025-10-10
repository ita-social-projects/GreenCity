package greencity.mapping;

import greencity.client.UserRemoteClient;
import greencity.constant.ErrorMessage;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievement.UserAchievementVO;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.dto.location.UserLocationDto;
import greencity.dto.user.UserVO;
import greencity.dto.user.UserVOAdvancedDto;
import greencity.entity.User;
import greencity.entity.UserLocation;
import greencity.exception.exceptions.NotFoundException;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import java.util.ArrayList;

@Component
public class UserVOAdvancedDtoMapper extends AbstractConverter<User, UserVOAdvancedDto> {
    private final UserRemoteClient userRemoteClient;
    private final ModelMapper modelMapper;

    @Lazy
    public UserVOAdvancedDtoMapper(UserRemoteClient userRemoteClient, ModelMapper modelMapper) {
        this.userRemoteClient = userRemoteClient;
        this.modelMapper = modelMapper;
    }

    @Override
    protected UserVOAdvancedDto convert(User user) {
        String email = user.getEmail();
        UserVOAdvancedDto userVOAdvancedDto = userRemoteClient.findByEmailAdvanced(email)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));

        userVOAdvancedDto.setUserAchievements(user.getUserAchievements() != null ? user.getUserAchievements()
            .stream().map(userAchievement -> UserAchievementVO.builder()
                .id(userAchievement.getId())
                .user(UserVO.builder()
                    .id(user.getId())
                    .email(userVOAdvancedDto.getEmail())
                    .build())
                .achievement(AchievementVO.builder()
                    .id(userAchievement.getAchievement().getId())
                    .title(userAchievement.getAchievement().getTitle())
                    .nameEn(userAchievement.getAchievement().getNameEn())
                    .nameUk(userAchievement.getAchievement().getNameUk())
                    .achievementCategory(
                        new AchievementCategoryVO(userAchievement.getAchievement().getAchievementCategory().getId(),
                            userAchievement.getAchievement().getAchievementCategory().getName()))
                    .build())
                .build())
            .toList() : new ArrayList<>());

        userVOAdvancedDto.setUserFriends(user.getUserFriends() != null
            ? user.getUserFriends().stream()
                .map(user1 -> (UserVO) UserVO.builder()
                    .id(user1.getId())
                    .name(user1.getName())
                    .build())
                .toList()
            : null);

        userVOAdvancedDto.setId(user.getId());
        userVOAdvancedDto.setUserCredo(user.getUserCredo());
        userVOAdvancedDto.setRating(user.getRating());
        userVOAdvancedDto.setUserCredo(user.getUserCredo());
        userVOAdvancedDto.setStatus(user.getStatus());
        userVOAdvancedDto.setProfilePicturePath(user.getProfilePicturePath());

        UserLocation userLocation = user.getUserLocation();
        if (userLocation != null) {
            UserLocationDto userLocationDto = modelMapper.map(userLocation, UserLocationDto.class);
            userVOAdvancedDto.setUserLocation(userLocationDto);
        }

        return userVOAdvancedDto;
    }
}
