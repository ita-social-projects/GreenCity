package greencity.dto.socialnetwork;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class SocialNetworkImageRequestDTO {
    String imagePath;

    String hostPath;
}
