package greencity.dto.econewscomment;

import java.time.LocalDateTime;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import greencity.dto.EmailSendable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddEcoNewsCommentDtoResponse implements EmailSendable {
    @NotNull
    @Min(1)
    private Long id;

    @NotEmpty
    private EcoNewsCommentAuthorDto author;

    @NotEmpty
    private String text;

    @NotEmpty
    private LocalDateTime modifiedDate;
}
