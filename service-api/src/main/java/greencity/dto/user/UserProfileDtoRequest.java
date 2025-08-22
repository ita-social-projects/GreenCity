package greencity.dto.user;

import greencity.annotations.ValidName;
import greencity.annotations.ValidSocialNetworkLinks;
import greencity.dto.CoordinatesDto;
import greencity.enums.ProfilePrivacyPolicy;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.Builder;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class UserProfileDtoRequest {
    @ValidName
    @Schema(example = "John")
    private String name;

    @Size(max = 170)
    @Schema(example = "Lets Make The World A Better Place")
    private String userCredo;

    @ValidSocialNetworkLinks
    @Schema(example = "https://www.facebook.com/greencity")
    private List<String> socialNetworks;

    @NotNull
    @Schema(example = "PUBLIC")
    private ProfilePrivacyPolicy showLocation;

    @NotNull
    @Schema(example = "PUBLIC")
    private ProfilePrivacyPolicy showEcoPlace;

    @NotNull
    @Schema(example = "PUBLIC")
    private ProfilePrivacyPolicy showToDoList;

    private CoordinatesDto coordinates;

    private Set<UserNotificationPreferenceVO> emailPreferences;
}
