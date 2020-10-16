package greencity.entity;

import greencity.enums.AchievementStatus;
import java.util.Objects;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@Table(name = "user_achievements")
@NoArgsConstructor
@AllArgsConstructor
@ToString(
    exclude = {"achievement", "user"})
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
    private AchievementStatus achievementStatus;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserAchievement that = (UserAchievement) o;
        return id.equals(that.id)
            && user.equals(that.user)
            && achievement.equals(that.achievement)
            && achievementStatus == that.achievementStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, achievement, achievementStatus);
    }
}
