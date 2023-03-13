package greencity.dto.habit;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class HabitAssignCustomPropertiesDto {
    private HabitAssignPropertiesDto habitAssignPropertiesDto;
    private List<Long> friendsIdsList;
}
