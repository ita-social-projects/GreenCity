package greencity.dto.comment;

import greencity.annotations.Sortable;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Sortable(fields = {
    "id",
    "createdDate",
    "modifiedDate",
    "parentCommentId",
    "replies",
    "likes",
    "dislikes",
    "status"
})
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CommentDto {
    @Min(1)
    @NotNull
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

    private int dislikes;

    @Builder.Default
    private boolean currentUserLiked = false;

    @Builder.Default
    private boolean currentUserDisliked = false;

    private String status;

    @Nullable
    @Max(5)
    private List<String> additionalImages;
}
