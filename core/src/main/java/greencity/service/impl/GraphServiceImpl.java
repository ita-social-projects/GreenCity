package greencity.service.impl;

import greencity.repository.UserRepo;
import greencity.service.GraphService;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GraphServiceImpl implements GraphService {
    private static final String OTHER_CITY = "Other";
    private final UserRepo userRepo;

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Integer> getGeneralStatisticsForAllUsersByCities() {
        List<String> userCities = userRepo.findAllUsersCities();
        Map<String, Integer> map = new HashMap<>();
        map.put(OTHER_CITY, 0);

        for (String city : userCities) {
            if (city == null) {
                city = OTHER_CITY;
            }
            if (map.containsKey(city)) {
                map.put(city, map.get(city) + 1);
            } else {
                map.put(city, 1);
            }
        }

        Map<String, Integer> sortedMap = sortMapByValue(map);
        Map<String, Integer> topFive = findTopFiveByPopularityCities(sortedMap);

        int sumOfOtherCities = sortedMap.values()
            .stream()
            .skip(5)
            .reduce(0, Integer::sum);

        topFive.put(OTHER_CITY, sortedMap.get(OTHER_CITY) + sumOfOtherCities);

        return topFive;
    }

    /**
     * Sorts {@link Map} by values. Values - number of users that live in the city.
     *
     * @param map {@link Map} that will be sorted.
     * @return {@link Map} sorted map.
     */
    private Map<String, Integer> sortMapByValue(Map<String, Integer> map) {
        return map.entrySet()
            .stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    private Map<String, Integer> findTopFiveByPopularityCities(Map<String, Integer> sortedMap) {
        return sortedMap.entrySet()
            .stream()
            .filter(v -> !v.getKey().equals(OTHER_CITY))
            .limit(5)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, HashMap::new));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Integer, Integer> getRegistrationStatistics() {
        List<Integer> months = userRepo.findAllRegistrationDates();
        Map<Integer, Integer> map = initializeMapWithMonths(new LinkedHashMap<>());
        months.forEach(month -> map.put(month - 1, map.get(month - 1) + 1));
        return map;
    }

    /**
     * Populates map with keys that represent months.
     *
     * @param map {@link Map}
     * @return populated {@link Map}
     */
    Map<Integer, Integer> initializeMapWithMonths(Map<Integer, Integer> map) {
        for (int i = 0; i < 12; i++) {
            map.put(i, 0);
        }
        return map;
    }
}
