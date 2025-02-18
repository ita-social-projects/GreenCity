package greencity.dto.ratingstatistics;

import greencity.annotations.SortableField;
import greencity.dto.SortableDTO;
import java.time.ZonedDateTime;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class RatingStatisticsDtoForTables implements SortableDTO {
    @SortableField
    private Long id;
    @SortableField
    private ZonedDateTime createDate;
    @SortableField
    private String eventName;
    private float pointsChanged;
    @SortableField
    private float rating;
    private long userId;
    private String userEmail;
}
