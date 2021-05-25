package greencity.service;

import java.util.Map;

public interface GraphService {
    /**
     * Get statistic for all users by city.
     *
     * @return {@link Map} with data for statistics.
     */
    Map<String, Integer> getGeneralStatisticsForAllUsersByCities();

    /**
     * Get general registration statistics.
     *
     * @return {@link Map} with data for statistics.
     */
    Map<Integer, Long> getRegistrationStatistics();
}
