package greencity.dto.econews;

import greencity.dto.user.SubscribersDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class InterestingEcoNewsDto {
    private List<ShortEcoNewsDto> ecoNewsList;
    private List<SubscribersDto> subscribers;
}
