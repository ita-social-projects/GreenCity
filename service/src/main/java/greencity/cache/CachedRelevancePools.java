package greencity.cache;

import java.util.List;

public record CachedRelevancePools(
    List<Long> relevantStrongNewsIds,
    List<Long> relevantWeakNewsIds,
    List<Long> nonRelevantNewsIds
) {
}
