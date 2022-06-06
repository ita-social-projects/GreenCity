package greencity.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import greencity.entity.localization.AchievementTranslation;
import greencity.enums.AchievementStatus;
import greencity.enums.UserActionType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = {"userAchievements"})
@Table(name = "achievements")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "achievement")
    private List<AchievementTranslation> translations;

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "achievement")
    private List<UserAchievement> userAchievements;

    @ManyToOne
    private AchievementCategory achievementCategory;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AchievementStatus achievementStatus = AchievementStatus.ACTIVE;

    @Column(columnDefinition = "json")
    @Type(type = "jsonb")
    private Map<UserActionType, Long> condition;

    @Column
    private String icon;
}
