package greencity.dto.user;

import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class RegistrationStatisticsDtoResponse {
    private final Integer month;
    private final Long count;
}
