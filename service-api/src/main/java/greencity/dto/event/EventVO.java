package greencity.dto.event;

import greencity.dto.tag.TagVO;
import greencity.dto.user.UserVO;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class EventVO {
    private Long id;
    private String title;
    private String titleImage;
    private UserVO organizer;
    private String description;
}
