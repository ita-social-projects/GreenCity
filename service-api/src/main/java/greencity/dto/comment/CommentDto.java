package greencity.dto.comment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class CommentDto {
    @NotNull
    @Min(1)
    private Long id;

    @NotEmpty
    private LocalDateTime createdDate;

    @NotEmpty
    private LocalDateTime modifiedDate;

    private CommentAuthorDto author;

    private Long parentCommentId;

    private String text;

    private int replies;

    private int likes;

    @Builder.Default
    private boolean currentUserLiked = false;

    private String status;
}
