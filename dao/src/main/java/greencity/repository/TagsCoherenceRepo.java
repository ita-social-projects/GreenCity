package greencity.repository;

import greencity.entity.TagsCoherence;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TagsCoherenceRepo extends JpaRepository<TagsCoherence, TagsCoherence.TagsCoherenceId> {
    @Query("SELECT tc.coherence FROM TagsCoherence tc WHERE tc.sourceTag.id = :habitTagId AND tc.destinationTag.id = :ecoNewsTagId")
    Double findWeightBetweenTags(@Param("habitTagId") Long habitTagId, @Param("ecoNewsTagId") Long ecoNewsTagId);

    @Override
    @EntityGraph(attributePaths = {"sourceTag", "destinationTag"})
    List<TagsCoherence> findAll();
}
