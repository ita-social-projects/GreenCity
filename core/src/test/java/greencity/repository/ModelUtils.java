package greencity.repository;

import greencity.entity.Language;
import greencity.entity.Notification;
import greencity.entity.User;
import greencity.enums.EmailNotification;
import greencity.enums.NotificationType;
import greencity.enums.ProjectName;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ModelUtils {
    public static Language getLanguage() {
        return Language.builder()
            .id(1L)
            .code("ua")
            .build();
    }

    public static List<String> getAllLanguages() {
        List<String> languages = new ArrayList<>();
        languages.add("ua");
        languages.add("en");
        return languages;
    }

    public static Notification getNotification() {
        return Notification.builder()
            .id(1L)
            .customMessage("111111111")
            .notificationType(NotificationType.EVENT_CREATED)
            .projectName(ProjectName.GREENCITY)
            .targetId(1L)
            .time(LocalDateTime.of(2024, 8, 13, 20, 0))
            .viewed(false)
            .targetUser(getUser())
            .actionUsers(List.of())
            .build();
    }

    public static User getUser() {
        return User.builder()
            .id(1L)
            .name("service")
            .email("test@greencity.ua")
            .role(Role.ROLE_ADMIN)
            .userStatus(UserStatus.ACTIVATED)
            .userFriends(List.of())
            .userAchievements(List.of())
            .dateOfRegistration(LocalDateTime.of(1970, 1, 1, 0, 0))
            .emailNotification(EmailNotification.DISABLED)
            .rating(0.0)
            .firstName("service")
            .socialNetworks(List.of())
            .lastActivityTime(LocalDateTime.of(1970, 1, 1, 0, 0))
            .userActions(List.of())
            .filters(List.of())
            .emailPreference(Set.of())
            .build();
    }
}
