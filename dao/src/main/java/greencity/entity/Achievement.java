package greencity.entity;

import greencity.entity.localization.AchievementTranslation;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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

}
