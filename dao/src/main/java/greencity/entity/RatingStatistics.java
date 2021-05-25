package greencity.entity;

import greencity.annotations.RatingCalculationEnum;
import java.time.ZonedDateTime;
import javax.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "rating_statistics")
@EqualsAndHashCode(exclude = "user")
@ToString(exclude = "user")
public class RatingStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "create_date", nullable = false)
    private ZonedDateTime createDate;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "event")
    private RatingCalculationEnum ratingCalculationEnum;

    @Column(name = "points_changed")
    private double pointsChanged;

    @Column(name = "current_rating")
    private double rating;

    @ManyToOne
    private User user;
}
