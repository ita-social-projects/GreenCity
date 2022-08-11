package greencity.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Data
@MappedSuperclass
@EqualsAndHashCode
@SuperBuilder
@NoArgsConstructor
@ToString
public class Translation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Language language;

    @Column(nullable = false, unique = true, length = 4000)
    private String content;
}
