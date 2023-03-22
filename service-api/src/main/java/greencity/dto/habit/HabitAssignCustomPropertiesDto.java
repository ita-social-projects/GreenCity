package greencity.dto.habit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitAssignCustomPropertiesDto {
    private HabitAssignPropertiesDto habitAssignPropertiesDto;
    private List<Long> friendsIdsList;
}
