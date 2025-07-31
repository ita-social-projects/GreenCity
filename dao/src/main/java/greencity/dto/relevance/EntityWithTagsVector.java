package greencity.dto.relevance;

import greencity.entity.Tag;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;

/**
 * An abstract class that is used to store a vector of tags for some entity with
 * {@link Tag}s.
 *
 * @author Rostyslav Zadyraichuk
 */
@Getter
public abstract class EntityWithTagsVector {
    protected Float[] tagsVector;

    /**
     * The vector is built from the given collection of tags and the given mapping
     * of tag ids to their indexes in the vector. The vector is initialized with all
     * elements set to 0.0f and then tags from the given collection are set to 1.0f
     * in the vector.
     *
     * @param tags        a collection of tags
     * @param tagsIndexes a map that associates tag IDs with their respective
     *                    positions in the vector
     */
    protected EntityWithTagsVector(Collection<Tag> tags, Map<Long, Integer> tagsIndexes) {
        this.tagsVector = new Float[tagsIndexes.size()];
        Arrays.fill(this.tagsVector, 0.0f);

        if (tags != null) {
            tags.forEach(tag -> Optional.ofNullable(tagsIndexes.get(tag.getId())).ifPresent(
                index -> tagsVector[index] = 1f));
        }
    }
}
