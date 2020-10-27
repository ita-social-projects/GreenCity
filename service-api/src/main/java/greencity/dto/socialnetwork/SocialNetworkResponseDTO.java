package greencity.dto.socialnetwork;

import lombok.*;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class SocialNetworkResponseDTO {
    private Long id;

    @Length(max = 500)
    String url;

    SocialNetworkImageResponseDTO socialNetworkImage;
}
