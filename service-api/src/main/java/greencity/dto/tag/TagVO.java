package greencity.dto.tag;

import greencity.dto.econews.EcoNewsVO;
import greencity.dto.tipsandtricks.TipsAndTricksVO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagVO {
    private Long id;
    private String name;
    private List<EcoNewsVO> ecoNews;
    private List<TipsAndTricksVO> tipsAndTricks;
}
