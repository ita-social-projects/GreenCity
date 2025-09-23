package greencity.dto.user;

import greencity.enums.UserStatus;

public record GreenCityUserInfoDto(
    Long userId,
    String userEmail,
    String profilePicturePath,
    String userCredo,
    UserStatus status,
    Double rating) {
}