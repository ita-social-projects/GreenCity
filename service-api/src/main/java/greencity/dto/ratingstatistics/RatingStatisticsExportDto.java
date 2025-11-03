package greencity.dto.ratingstatistics;

import lombok.Builder;
import lombok.Data;
import java.time.ZonedDateTime;

@Data
@Builder
public class RatingStatisticsExportDto {
    private Long id;
    private String event;
    private ZonedDateTime date;
    private Long userId;
    private String userEmail;
    private float pointsChanged;
    private float currentRating;
}
