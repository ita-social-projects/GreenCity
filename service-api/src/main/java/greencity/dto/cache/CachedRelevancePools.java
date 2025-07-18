package greencity.dto.cache;

import java.util.LinkedList;

/**
 * Represents pools of relevant news for user. Pools are divided into strong and
 * weak relevant news. Non-relevant news are news that are not relevant to the
 * user, but the user can see them due to mixing logic.
 *
 * @author Rostyslav Zadyraichuk
 */
public record CachedRelevancePools(
    LinkedList<Long> relevantStrongNewsIds,
    LinkedList<Long> relevantWeakNewsIds,
    LinkedList<Long> nonRelevantNewsIds
) {
}
