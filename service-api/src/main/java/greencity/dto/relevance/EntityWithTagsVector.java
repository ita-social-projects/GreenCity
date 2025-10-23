package greencity.dto.relevance;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;

/**
 * An abstract class that is used to store a vector of tags for some entity with
 * tags.
 *
 * @author Rostyslav Zadyraichuk
 */
@Getter
public abstract class EntityWithTagsVector {
    protected Float[] tagsVector;

    /**
     * The vector is built from the given collection of tagIds and the given mapping
     * of tag ids to their indexes in the vector. The vector is initialized with all
     * elements set to 0.0f and then tagIds from the given collection are set to 1.0f
     * in the vector.
     *
     * @param tagIds        a collection of tagIds
     * @param tagsIndexes a map that associates tag IDs with their respective
     *                    positions in the vector
     */
    protected EntityWithTagsVector(Collection<Long> tagIds, Map<Long, Integer> tagsIndexes) {
        this.tagsVector = new Float[tagsIndexes.size()];
        Arrays.fill(this.tagsVector, 0.0f);

        if (tagIds != null) {
            tagIds.forEach(tagId -> Optional.ofNullable(tagsIndexes.get(tagId)).ifPresent(
                index -> tagsVector[index] = 1f));
        }
    }
}
