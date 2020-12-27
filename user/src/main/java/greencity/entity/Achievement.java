package greencity.entity;

import greencity.entity.localization.AchievementTranslation;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(
    exclude = {"userAchievements"})
@Table(name = "achievements")
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "achievement", fetch = FetchType.LAZY)
    private List<AchievementTranslation> translations;

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "achievement", fetch = FetchType.LAZY)
    private List<UserAchievement> userAchievements;

    @ManyToOne
    private AchievementCategory achievementCategory;

    @Column(nullable = false)
    private Integer condition;
}
