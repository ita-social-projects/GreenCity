package greencity.entity.cache;

import greencity.dto.econews.EcoNewsDto;
import java.util.List;
import lombok.Getter;

@Getter
public record CachedAINews(List<EcoNewsDto> newsList) {
}
