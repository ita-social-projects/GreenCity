package greencity.dto.user;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class UserFilterDtoResponse {
    private Long id;
    private String name;
    private String searchCriteria;
    private String userStatus;
    private String userRole;
}
