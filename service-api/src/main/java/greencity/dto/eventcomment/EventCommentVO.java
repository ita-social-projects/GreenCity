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

import javax.validation.constraints.Size;
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

    private EventCommentVO parentComment;

    private List<EventCommentVO> comments = new ArrayList<>();

    private UserVO user;

    private EventVO event;

    private boolean deleted;

    private boolean currentUserLiked;

    private Set<UserVO> usersLiked = new HashSet<>();
}
