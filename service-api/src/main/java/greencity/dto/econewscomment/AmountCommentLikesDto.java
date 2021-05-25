package greencity.dto.econewscomment;

import javax.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class AmountCommentLikesDto {
    @NotEmpty
    private Long id;

    private Integer amountLikes;

    private Long userId;

    private boolean isLiked;
}
