package greencity.dto.relevance;

import greencity.entity.Tag;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;

@Getter
public abstract class EntityWithTagsVector {
    protected Float[] tagsVector;

    protected EntityWithTagsVector(Collection<Tag> tags, Map<Long, Integer> tagsIndexes) {
        if (tags != null) {
            this.tagsVector = new Float[tagsIndexes.size()];
            Arrays.fill(this.tagsVector, 0.0f);
            tags.forEach(tag ->
                Optional.ofNullable(tagsIndexes.get(tag.getId())).ifPresent(
                    index -> tagsVector[index] = 1f
                ));
        }
    }
}
