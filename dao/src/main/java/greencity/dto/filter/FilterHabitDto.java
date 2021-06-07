package greencity.dto.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterHabitDto {
    @Valid
    private String searchReg;
    private Integer durationFrom;
    private Integer durationTo;
    private Integer complexity;
    boolean withoutImage;
    boolean withImage;
}
