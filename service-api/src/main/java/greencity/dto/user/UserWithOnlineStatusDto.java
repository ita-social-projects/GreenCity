package greencity.dto.user;

import lombok.*;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class UserWithOnlineStatusDto {
    @NotNull
    private Long id;

    private boolean onlineStatus;
}
