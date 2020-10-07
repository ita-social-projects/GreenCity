package greencity.service;

import greencity.entity.User;
import java.util.List;
import java.util.Map;

public interface GraphService {
    /**
     * Get statistic for all users by city.
     *
     * @return {@link List} of {@link User}.
     */
    Map<String, Integer> getGeneralStatisticsForAllUsersByCities();

    /**
     * Get general registration statistics.
     *
     * @return {@link Map} with data for statistics.
     */
    Map<Integer, Long> getRegistrationStatistics();
}
