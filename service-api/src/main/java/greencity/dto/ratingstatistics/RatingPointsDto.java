package greencity.dto.ratingstatistics;

import greencity.annotations.SortableField;
import greencity.dto.SortableDTO;
import greencity.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RatingPointsDto implements SortableDTO {
    @SortableField
    private Long id;
    @SortableField
    private String name;
    @SortableField
    private Integer points;
    private Status status;
}
