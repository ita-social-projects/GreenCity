package greencity.entity;

import greencity.enums.AchievementStatus;
import javax.persistence.*;

import lombok.*;

@Entity
@Data
@Table(name = "user_achievements")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class UserAchievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Achievement achievement;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AchievementStatus achievementStatus = AchievementStatus.INACTIVE;
}
