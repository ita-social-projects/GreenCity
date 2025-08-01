package greencity.repository;

import greencity.entity.TagsCoherence;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagsCoherenceRepo extends JpaRepository<TagsCoherence, TagsCoherence.TagsCoherenceId> {
    @Override
    @EntityGraph(attributePaths = {"sourceTag", "destinationTag"})
    List<TagsCoherence> findAll();
}
