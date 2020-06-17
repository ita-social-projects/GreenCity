package greencity.dto.user;

import javax.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class UserProfilePictureDto {
    @NotNull
    private Long id;

    @NotNull
    private String profilePicturePath;
}
