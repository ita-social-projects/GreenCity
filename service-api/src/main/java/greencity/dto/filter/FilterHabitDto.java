package greencity.dto.filter;

import greencity.annotations.Sortable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Sortable
@AllArgsConstructor
@NoArgsConstructor
public class FilterHabitDto {
    private String searchReg;
    private Integer durationFrom;
    private Integer durationTo;
    private Integer complexity;
    boolean withoutImage;
    boolean withImage;
}
