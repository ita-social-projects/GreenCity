package greencity.cache;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public record CachedUserRelevantNews(
    CachedRelevancePools newsRelevancePools,
    Map<Integer, List<Long>> relevantNewsPages,
    Integer totalPagesCount,
    ZonedDateTime lastRequestedDate
) {
}
