package greencity.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "text_translations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TextTranslation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Language language;

    @ManyToOne
    private TipsAndTricks tipsAndTricks;

    @Column(nullable = false, unique = true, length = 300)
    private String content;
}
