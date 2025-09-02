package greencity.utils;

import greencity.filters.SearchCriteria;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
public class SpecificationUtils {
    public static void setValueIfNotEmpty(List<SearchCriteria> searchCriteria, String key, Object value) {
        boolean isInvalid = value == null
            || (value instanceof String && StringUtils.isEmpty(((String) value).trim()));
        if (!isInvalid) {
            searchCriteria.add(SearchCriteria.builder()
                .key(key)
                .type(key)
                .value(value)
                .build());
        }
    }
}
