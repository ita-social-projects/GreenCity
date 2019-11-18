package greencity.dto.habitstatistic;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitDto {
    private Long id;
    private String habitName;
    private LocalDate createDate;
}
