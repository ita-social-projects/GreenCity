package greencity.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.EqualsAndHashCode;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class RegistrationStatisticsDtoResponse {
    private final Integer month;
    private final Long count;
}
