package greencity.repository;

import greencity.entity.TipsAndTricks;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TipsAndTricksRepo extends JpaRepository<TipsAndTricks, Long> {
    /**
     * Method returns {@link TipsAndTricks} by specific tags.
     *
     * @param tags list of tags to search by.
     * @return {@link TipsAndTricks} by specific tags.
     */
    @Query("SELECT DISTINCT tt FROM TipsAndTricks tt "
        + "JOIN tt.tipsAndTricksTags ttt "
        + "WHERE ttt.name in :tags "
        + "ORDER BY tt.creationDate DESC")
    Page<TipsAndTricks> find(Pageable pageable, Optional<String> tags);


    /**
     * Method returns all {@link TipsAndTricks} by page.
     *
     * @param page of tips & tricks.
     * @return all {@link TipsAndTricks} by page.
     */
    Page<TipsAndTricks> findAllByOrderByCreationDateDesc(Pageable page);
}
