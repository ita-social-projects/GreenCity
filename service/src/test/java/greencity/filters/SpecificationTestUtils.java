package greencity.filters;

import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class SpecificationTestUtils {
    public static void setValue(List<SearchCriteria> searchCriteria, String key, String value) {
        searchCriteria.add(SearchCriteria.builder()
            .key(key)
            .type(key)
            .value(value)
            .build());
    }
}
