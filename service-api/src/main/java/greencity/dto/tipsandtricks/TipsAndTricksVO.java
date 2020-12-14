package greencity.dto.tipsandtricks;

import greencity.dto.tag.TagVO;
import greencity.dto.tipsandtrickscomment.TipsAndTricksCommentVO;
import greencity.dto.user.UserVO;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class TipsAndTricksVO {
    private Long id;
    private List<TitleTranslationVO> titleTranslations;
    private List<TextTranslationVO> textTranslations;
    private ZonedDateTime creationDate;
    private UserVO author;
    private List<TagVO> tags;
    private List<TipsAndTricksCommentVO> tipsAndTricksComments = new ArrayList<>();
    private String imagePath;
    private String source;
}
