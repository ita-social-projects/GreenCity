package greencity.dto.eventcomment;

import greencity.dto.user.UserVO;
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
import java.util.HashSet;
import java.util.Set;

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
    private LocalDateTime createdDate;

    private EventCommentAuthorDto author;

    private String text;

    private boolean currentUserLiked;

    private Set<UserVO> usersLiked = new HashSet<>();
}
