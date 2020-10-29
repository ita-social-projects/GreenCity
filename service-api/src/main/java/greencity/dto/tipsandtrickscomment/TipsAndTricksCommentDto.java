package greencity.dto.tipsandtrickscomment;

import greencity.enums.CommentStatus;
import java.time.LocalDateTime;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TipsAndTricksCommentDto {
    @NotNull
    @Min(1)
    private Long id;

    @NotEmpty
    private LocalDateTime modifiedDate;

    private TipsAndTricksCommentAuthorDto author;

    private String text;

    private int replies;

    private int likes;

    private boolean currentUserLiked;

    private CommentStatus status;
}
