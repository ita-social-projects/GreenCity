package greencity.dto.search;

import greencity.dto.econews.EcoNewsDto;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchResponseDto {

    List<EcoNewsDto> ecoNews;
}
