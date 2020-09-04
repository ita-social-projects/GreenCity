package greencity.repository;

import greencity.entity.TipsAndTricks;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
        + "JOIN tt.tags ttt "
        + "WHERE lower(ttt.name) in :tags "
        + "ORDER BY tt.creationDate DESC")
    Page<TipsAndTricks> find(Pageable pageable, List<String> tags);

    /**
     * Method returns all {@link TipsAndTricks} by page.
     *
     * @param page of tips & tricks.
     * @return all {@link TipsAndTricks} by page.
     */
    Page<TipsAndTricks> findAllByOrderByCreationDateDesc(Pageable page);

    /**
     * Method returns {@link TipsAndTricks} by search query and page.
     *
     * @param searchQuery query to search
     * @return list of {@link TipsAndTricks}
     */
    @Query("select tt from TipsAndTricks tt "
        + "where lower(tt.title) like lower(CONCAT('%', :searchQuery, '%')) "
        + "or lower(tt.text) like lower(CONCAT('%', :searchQuery, '%')) "
        + "or tt.id in (select tt.id from TipsAndTricks tt inner join tt.tags ttt "
        + "where lower(ttt.name) like lower(CONCAT('%', :searchQuery, '%')))")
    Page<TipsAndTricks> searchTipsAndTricks(Pageable pageable, String searchQuery);

    /**
     * Method returns {@link TipsAndTricks} by search query and page.
     *
     * @param searchQuery query to search
     * @return list of {@link TipsAndTricks}
     */
    @Query("select tt from TipsAndTricks tt "
        + "where CONCAT(tt.id,'') like lower(CONCAT('%', :searchQuery, '%')) "
        + "or lower(tt.title) like lower(CONCAT('%', :searchQuery, '%')) "
        + "or lower(tt.text) like lower(CONCAT('%', :searchQuery, '%')) "
        + "or tt.id in (select tt.id from TipsAndTricks tt inner join tt.author a "
        + "where lower(a.name) like lower(CONCAT('%', :searchQuery, '%')))")
    Page<TipsAndTricks> searchTipsAndTricksManagement(Pageable pageable, String searchQuery);

    /**
     * Method for getting amount of written tips and trick by user id.
     *
     * @param id {@link Long} user id.
     * @return amount of written tips and trick by user id.
     * @author Marian Datsko
     */
    @Query(nativeQuery = true,
        value = " SELECT COUNT(author_id) "
            + " FROM tips_and_tricks WHERE author_id = :userId")
    Long getAmountOfWrittenTipsAndTrickByUserId(@Param("userId") Long id);
}
