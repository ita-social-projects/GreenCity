package greencity.service;

import java.util.List;

/**
 * Provides the interface to manage additional functionality.
 */
public interface UtilService {
    /**
     * Convert {@link String} to List of id's.
     *
     * @param ids - {@link String} of id's.
     * @return List of id's.
     */
    List<Long> getIdsFromString(String ids);
}
