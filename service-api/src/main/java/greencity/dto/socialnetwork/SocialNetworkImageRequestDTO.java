package greencity.dto.socialnetwork;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Data;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class SocialNetworkImageRequestDTO {
    @NotBlank
    @NotNull
    String imagePath;

    @NotBlank
    @NotNull
    String hostPath;
}
