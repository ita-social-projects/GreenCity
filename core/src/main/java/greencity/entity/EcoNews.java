package greencity.entity;

import java.time.ZonedDateTime;
import java.util.List;
import javax.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "eco_news")
@ToString(exclude = {"translations", "author", "tags"})
@EqualsAndHashCode(exclude = {"translations", "author", "tags"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EcoNews {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private ZonedDateTime creationDate;

    @Column(nullable = false)
    private String imagePath;

    @ManyToOne
    private User author;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String text;

    @ManyToMany
    private List<Tag> tags;
}