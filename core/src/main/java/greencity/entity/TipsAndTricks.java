package greencity.entity;

import java.time.ZonedDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "tips_and_tricks")
@ToString(exclude = {"author", "tipsAndTricksTags"})
@EqualsAndHashCode(exclude = {"author", "tipsAndTricksTags"})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TipsAndTricks {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Size(min = 1, max = 170)
    private String title;

    @Column(nullable = false)
    @Size(min = 20, max = 63206)
    private String text;

    @Column(nullable = false)
    private ZonedDateTime creationDate;

    @ManyToOne
    private User author;

    @ManyToMany
    private List<TipsAndTricksTag> tipsAndTricksTags;

    @Column
    private String imagePath;

    @Column
    private String source;
}
