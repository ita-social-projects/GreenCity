package greencity.dto.socialnetwork;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class SocialNetworkImageResponseDTO {
    private Long id;

    String imagePath;

    String hostPath;
}
