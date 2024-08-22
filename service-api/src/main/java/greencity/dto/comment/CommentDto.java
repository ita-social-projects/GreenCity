package greencity.dto.comment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CommentDto {
    @NotNull
    @Min(1)
    private Long id;

    @NotNull
    private LocalDateTime createdDate;

    @NotNull
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
