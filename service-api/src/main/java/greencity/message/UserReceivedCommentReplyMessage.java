package greencity.message;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
public class UserReceivedCommentReplyMessage extends CommentMessage implements Serializable {
    @NotEmpty(message = "Author name cannot be empty")
    private String authorName;

    @NotEmpty(message = "Parent comment cannot be empty")
    private String parentCommentText;

    @NotEmpty(message = "Parent comment author name cannot be empty")
    private String parentCommentAuthorName;

    @NotNull(message = "Parent comment creation date cannot be null")
    private LocalDateTime parentCommentCreationDate;
}
