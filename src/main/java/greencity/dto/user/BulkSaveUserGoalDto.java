package greencity.dto.user;

import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkSaveUserGoalDto {
    @Valid List<@Valid UserGoalDto> userGoalDtos;
}
