package greencity.dto.tipsandtrickscomment;

import greencity.dto.tipsandtricks.TipsAndTricksVO;
import greencity.dto.user.UserVO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class TipsAndTricksCommentVO {
    private Long id;
    private String text;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private TipsAndTricksCommentVO parentComment;
    private List<TipsAndTricksCommentVO> comments = new ArrayList<>();
    private UserVO user;
    private TipsAndTricksVO tipsAndTricks;
    private boolean deleted;
    private boolean currentUserLiked = false;
    private Set<UserVO> usersLiked;
}
