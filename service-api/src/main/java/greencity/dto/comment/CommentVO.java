package greencity.dto.comment;

import greencity.dto.user.UserVO;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class CommentVO {
    private Long id;

    private String articleType;

    private Long articleId;

    @Size(min = 1, max = 8000)
    private String text;

    @CreatedDate
    private LocalDateTime createdDate;

    @NotEmpty
    private LocalDateTime modifiedDate;

    private CommentVO parentComment;

    @Builder.Default
    private List<CommentVO> comments = new ArrayList<>();

    private UserVO user;

    private boolean currentUserLiked;

    @Builder.Default
    private Set<UserVO> usersLiked = new HashSet<>();

    private String status;
}
