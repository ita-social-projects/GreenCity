package greencity.entity;

import greencity.enums.AchievementStatus;
import lombok.*;

import javax.persistence.*;

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

    @Column
    private boolean notified;
}
