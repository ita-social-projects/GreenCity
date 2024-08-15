package greencity.dto.comment;

import greencity.dto.eventcomment.EventCommentAuthorDto;
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
    private LocalDateTime modifiedDate;

    private CommentAuthorDto author;

    private String text;

    private int likes;

    @Builder.Default
    private boolean currentUserLiked = false;

    private String status;
}
