package greencity.repository;

import greencity.entity.localization.TagTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagTranslationRepo extends JpaRepository<TagTranslation, Long> {
    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM tag_translations WHERE tag_id IN (:tagIds)")
    void bulkDeleteByTagId(List<Long> tagIds);
}
