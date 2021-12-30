package greencity.dto.socialnetwork;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode
@Builder
public class SocialNetworkImageVO {
    private Long id;
    private String imagePath;
    private String hostPath;
}
