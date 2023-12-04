package greencity.dto.eventcomment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddEventCommentDtoResponse {
    @NotNull
    @Min(1)
    private Long id;

    @NotEmpty
    private EventCommentAuthorDto author;

    @NotEmpty
    private String text;

    @CreatedDate
    private LocalDateTime createdDate;
}
