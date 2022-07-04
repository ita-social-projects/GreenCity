package greencity.dto.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFilterDto {
    private String searchCriteria;
    private String role;
    private String status;
}
