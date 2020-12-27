package greencity.dto.user;

import lombok.*;

import javax.validation.constraints.NotNull;

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
    private String name;

    @NotNull
    private String profilePicturePath;
}
