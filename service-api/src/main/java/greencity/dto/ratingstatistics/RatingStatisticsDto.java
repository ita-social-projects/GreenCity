package greencity.dto.ratingstatistics;

import greencity.dto.user.UserVO;
import java.time.ZonedDateTime;
import greencity.enums.RatingCalculationEnum;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

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
    private UserVO user;
}
