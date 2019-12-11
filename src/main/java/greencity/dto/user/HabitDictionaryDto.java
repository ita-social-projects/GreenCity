package greencity.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HabitDictionaryDto {
    private Long id;
    private String name;
    private String habitItem;
    private String description;
}