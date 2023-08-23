package greencity.dto.eventcomment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class EventCommentDto {
    @NotNull
    @Min(1)
    private Long id;

    @CreatedDate
    private LocalDateTime modifiedDate;

    private EventCommentAuthorDto author;

    private String text;

    private int numberOfReplies;

    private int numberOfLikes;

    private boolean currentUserLiked = false;
}
