package greencity.dto.user;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class UserDeactivateDto {
    @NotNull
    private Long id;

    private LocalDateTime lastActivityTime;
}
