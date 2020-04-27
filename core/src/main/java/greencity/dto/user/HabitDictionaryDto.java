package greencity.dto.user;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class HabitDictionaryDto {
    private Long id;
    private String name;
    private String habitItem;
    private String description;
    private String image;
}
