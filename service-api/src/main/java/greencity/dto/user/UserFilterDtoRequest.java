package greencity.dto.user;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class UserFilterDtoRequest {
    private String name;
    private String searchCriteria;
    private String userStatus;
    private String userRole;
}
