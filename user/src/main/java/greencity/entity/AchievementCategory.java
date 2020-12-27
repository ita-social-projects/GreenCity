package greencity.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "achievement_categories")
@EqualsAndHashCode
@Builder
public class AchievementCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @OneToMany(mappedBy = "achievementCategory")
    private List<Achievement> achievementList;

    @OneToMany(mappedBy = "achievementCategory")
    private List<UserAction> userActions;
}
