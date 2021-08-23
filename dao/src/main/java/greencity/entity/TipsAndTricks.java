package greencity.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(cascade = {CascadeType.REMOVE, CascadeType.REFRESH}, mappedBy = "tipsAndTricks", fetch = FetchType.LAZY)
    private List<TitleTranslation> titleTranslations;

    @OneToMany(cascade = {CascadeType.REMOVE, CascadeType.REFRESH}, mappedBy = "tipsAndTricks", fetch = FetchType.LAZY)
    private List<TextTranslation> textTranslations;

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
