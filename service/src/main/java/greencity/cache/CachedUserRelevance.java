package greencity.cache;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public record CachedUserRelevance(
    CachedRelevancePools newsRelevancePools,
    Map<Integer, List<Long>> relevantNewsPages,
    ZonedDateTime lastRequestedDate
) {
}
