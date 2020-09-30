package greencity.filters;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SearchCriteria {
    private Object value;
    private String key;
    private String type;
}
