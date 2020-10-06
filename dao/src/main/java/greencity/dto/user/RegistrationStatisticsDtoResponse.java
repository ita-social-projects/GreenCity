package greencity.dto.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class RegistrationStatisticsDtoResponse {
    private Integer month;
    private Long count;
}
