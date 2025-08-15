package greencity.dto.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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
