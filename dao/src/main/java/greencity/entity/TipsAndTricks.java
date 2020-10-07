package greencity.entity;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "tips_and_tricks")
@ToString(exclude = {"author", "tags"})
@EqualsAndHashCode(exclude = {"author", "tags"})
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
    private List<Tag> tags;

    @OneToMany(mappedBy = "tipsAndTricks", fetch = FetchType.LAZY)
    private List<TipsAndTricksComment> tipsAndTricksComments = new ArrayList<>();

    @Column
    private String imagePath;

    @Column
    private String source;
}
