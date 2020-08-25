package greencity.dto.user;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class UserBlockDto {
    @NotNull
    private Long id;

    private LocalDateTime timeToBeDeleted;
}
