package greencity.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class HabitLogItemDto {
    private String habitItemName;
    private Integer habitItemAmount;
}
