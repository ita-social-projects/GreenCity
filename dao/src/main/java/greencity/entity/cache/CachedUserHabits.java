package greencity.entity.cache;

import java.util.List;
import java.util.Set;
import lombok.Getter;

//@Getter
public record CachedUserHabits(List<String> habitNames, Set<String> habitSet) {
}