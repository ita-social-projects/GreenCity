package greencity.dto.ratingstatistics;

import greencity.dto.user.UserVO;
import greencity.enums.RatingCalculationEnum;
import java.time.ZonedDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingStatisticsVO {
    private Long id;
    private ZonedDateTime createDate;
    private RatingCalculationEnum ratingCalculationEnum;
    private double pointsChanged;
    private double rating;
    private UserVO user;
}
