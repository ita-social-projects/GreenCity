package greencity.dto.comment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddCommentDtoResponse {
    @NotNull
    @Min(1)
    private Long id;

    // todo

    @NotEmpty
    private CommentAuthorDto author;

    @NotEmpty
    private String text;

    @CreatedDate
    private LocalDateTime createdDate;
}
