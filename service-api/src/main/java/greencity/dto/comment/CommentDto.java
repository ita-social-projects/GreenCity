package greencity.dto.comment;

import greencity.annotations.SortableField;
import greencity.dto.SortableDTO;
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

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CommentDto implements SortableDTO {
    @NotNull
    @Min(1)
    @SortableField
    private Long id;

    @NotNull
    @SortableField
    private LocalDateTime createdDate;

    @NotNull
    @SortableField
    private LocalDateTime modifiedDate;

    private CommentAuthorDto author;

    @SortableField
    private Long parentCommentId;

    private String text;

    @SortableField
    private int replies;

    @SortableField
    private int likes;

    @SortableField
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
