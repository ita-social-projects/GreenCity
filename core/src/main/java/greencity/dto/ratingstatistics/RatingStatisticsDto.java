package greencity.dto.ratingstatistics;

import greencity.annotations.RatingCalculationEnum;
import greencity.entity.User;
import java.time.ZonedDateTime;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class RatingStatisticsDto {
    private Long id;
    private ZonedDateTime createDate;
    private RatingCalculationEnum ratingCalculationEnum;
    private float pointsChanged;
    private float rating;
    private User user;
}
