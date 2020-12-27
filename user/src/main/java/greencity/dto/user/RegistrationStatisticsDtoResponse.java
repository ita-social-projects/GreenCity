package greencity.dto.user;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class RegistrationStatisticsDtoResponse {
    private final Integer month;
    private final Long count;
}
