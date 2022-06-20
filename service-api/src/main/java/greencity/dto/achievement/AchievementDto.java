package greencity.dto.achievement;

import greencity.enums.UserActionType;
import lombok.*;

import java.util.Map;

@Builder
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class AchievementDto {
    private Long id;

    private String icon;

    private String titleUk;
    private String titleEn;

    private String descriptionUk;
    private String descriptionEn;

    private String messageUk;
    private String messageEn;

    private String category;

    private Map<UserActionType, Long> condition;

    private Long usersAchievedTotal;
}
