package greencity.dto.search;

import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class SearchResponseDto {
    List<SearchNewsDto> ecoNews;

    List<SearchTipsAndTricksDto> tipsAndTricks;

    Long countOfResults;
}
