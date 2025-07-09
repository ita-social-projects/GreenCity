package greencity.cache;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * This class represents cached relevant news for user.
 *
 * @author Rostyslav Zadyraichuk
 */
@AllArgsConstructor
@Getter
@Setter
public class CachedUserRelevantNews {
    private CachedRelevancePools newsRelevancePools;
    private Map<Integer, List<Long>> relevantNewsPages;
    private Integer lastGeneratedPage;
    private Integer totalPagesCount;
    private ZonedDateTime lastRequestedDate;
}
