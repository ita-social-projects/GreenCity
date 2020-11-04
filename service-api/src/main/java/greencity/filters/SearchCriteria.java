package greencity.filters;

import lombok.*;

import java.util.Arrays;
import java.util.Objects;

@Builder
@Getter
@Setter
@ToString
public class SearchCriteria {
    private Object value;
    private String key;
    private String type;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SearchCriteria that = (SearchCriteria) o;
        boolean valueEquals;
        if (this.value instanceof String[]) {
            valueEquals = Arrays.equals((String[]) this.value, (String[]) that.value);
        } else {
            valueEquals = this.value.equals(that.value);
        }
        return valueEquals && key.equals(that.key) && type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, key, type);
    }
}
