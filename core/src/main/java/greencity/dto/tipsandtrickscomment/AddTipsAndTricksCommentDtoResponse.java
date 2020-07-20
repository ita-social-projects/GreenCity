package greencity.dto.tipsandtrickscomment;

import java.time.LocalDateTime;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddTipsAndTricksCommentDtoResponse {
    @NotNull
    @Min(1)
    private Long id;

    @NotEmpty
    private TipsAndTricksCommentAuthorDto author;

    @NotEmpty
    private String text;

    @NotEmpty
    private LocalDateTime modifiedDate;
}