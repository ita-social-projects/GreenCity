package greencity.filters;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class SearchCriteria {
    private Object value;
    private String key;
    private String type;
}
