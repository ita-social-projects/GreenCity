package greencity.dto.eventcomment;

import greencity.dto.event.EventVO;
import greencity.dto.user.UserVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class EventCommentVO {
    private Long id;

    @Size(min = 1, max = 8000)
    private String text;

    @CreatedDate
    private LocalDateTime createdDate;

    @NotEmpty
    private LocalDateTime modifiedDate;

    private EventCommentVO parentComment;

    @Builder.Default
    private List<EventCommentVO> comments = new ArrayList<>();

    private UserVO user;

    private EventVO event;

    private boolean currentUserLiked;

    @Builder.Default
    private Set<UserVO> usersLiked = new HashSet<>();

    private String status;
}
