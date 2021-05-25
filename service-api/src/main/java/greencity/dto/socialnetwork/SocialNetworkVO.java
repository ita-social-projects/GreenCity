package greencity.dto.socialnetwork;

import greencity.dto.user.UserVO;
import lombok.*;

import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SocialNetworkVO {
    private Long id;

    @Size(min = 1, max = 500)
    String url;

    SocialNetworkImageVO socialNetworkImage;

    UserVO user;
}
