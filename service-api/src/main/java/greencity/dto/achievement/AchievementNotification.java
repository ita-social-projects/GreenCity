package greencity.dto.achievement;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@Getter
public class AchievementNotification {
    private Long id;
    private String title;
    private String description;
    private String message;
}
