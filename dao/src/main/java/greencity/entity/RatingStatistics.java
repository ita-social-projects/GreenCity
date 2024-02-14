package greencity.entity;

import java.time.ZonedDateTime;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import greencity.enums.RatingCalculationEnum;
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
