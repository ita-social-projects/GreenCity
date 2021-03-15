package greencity.dto.ratingstatistics;

import java.time.ZonedDateTime;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class RatingStatisticsDtoForTables {
    private Long id;
    private ZonedDateTime createDate;
    private String eventName;
    private float pointsChanged;
    private float rating;
    private long userId;
    private String userEmail;
}
