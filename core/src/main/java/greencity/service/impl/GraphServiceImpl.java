package greencity.service.impl;

import greencity.entity.User;
import greencity.repository.UserRepo;
import greencity.service.GraphService;
import java.time.LocalDate;
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
        List<User> users = userRepo.findAll();
        Map<String, Integer> map = new HashMap<>();
        map.put(OTHER_CITY, 0);

        for (User tempUser : users) {
            String city = tempUser.getCity();
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
    Map<String, Integer> sortMapByValue(Map<String, Integer> map) {
        return map.entrySet()
            .stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    Map<String, Integer> findTopFiveByPopularityCities(Map<String, Integer> sortedMap) {
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
        List<User> users = userRepo.findAll();
        Map<Integer, Integer> map = initializeMapWithMonths(new LinkedHashMap<>());
        int currentYear = LocalDate.now().getYear();

        for (User tempUser : users) {
            if (tempUser.getDateOfRegistration().getYear() == currentYear) {
                int month = tempUser.getDateOfRegistration().getMonth().ordinal();
                map.put(month, map.get(month) + 1);
            }
        }

        return map;
    }

    /**
     * Populates map with keys that represent months.
     *
     * @param map {@link Map}
     * @return populated {@link Map}
     */
    Map<Integer, Integer> initializeMapWithMonths(Map<Integer, Integer> map) {
        map.put(0, 0);
        map.put(1, 0);
        map.put(2, 0);
        map.put(3, 0);
        map.put(4, 0);
        map.put(5, 0);
        map.put(6, 0);
        map.put(7, 0);
        map.put(8, 0);
        map.put(9, 0);
        map.put(10, 0);
        map.put(11, 0);
        return map;
    }
}
