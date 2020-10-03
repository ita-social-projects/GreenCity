package greencity.dto.ratingstatistics;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class RatingStatisticsViewDto {
    private String id;
    private String eventName;
    private String userId;
    private String userEmail;
    private String startDate;
    private String endDate;
    private String pointsChanged;
    private String currentRating;
}
