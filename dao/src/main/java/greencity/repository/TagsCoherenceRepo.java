package greencity.repository;

import greencity.entity.Tag;
import greencity.entity.TagsCoherence;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagsCoherenceRepo extends JpaRepository<TagsCoherence, TagsCoherence.TagsCoherenceId> {
    /**
     * Method to find all destination tags and coherence values by the source tag.
     *
     * @param sourceTag the source tag to search by
     * @return a list of TagsCoherence entities
     */
    List<TagsCoherence> findAllBySourceTag(Tag sourceTag);
}
