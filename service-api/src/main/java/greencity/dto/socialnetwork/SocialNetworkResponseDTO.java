package greencity.dto.socialnetwork;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Data;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
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
