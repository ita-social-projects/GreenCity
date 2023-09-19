package greencity.entity.localization;

import greencity.entity.Achievement;
import greencity.entity.Language;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Table(name = "achievement_translations")
@SuperBuilder
@EqualsAndHashCode(exclude = "achievement")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class AchievementTranslation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 300)
    private String name;
    @Column(nullable = false, length = 300)
    private String nameEng;

    @ManyToOne
    private Achievement achievement;
}
