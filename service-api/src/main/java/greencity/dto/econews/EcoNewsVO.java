package greencity.dto.econews;

import greencity.dto.econewscomment.EcoNewsCommentVO;
import greencity.dto.tag.TagVO;
import greencity.dto.user.UserVO;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
public class EcoNewsVO {
    private Long id;

    private ZonedDateTime creationDate;

    private String imagePath;

    private String source;

    private UserVO author;

    private String title;

    private String text;

    private List<EcoNewsCommentVO> ecoNewsComments = new ArrayList<>();

    private List<TagVO> tags;
}
