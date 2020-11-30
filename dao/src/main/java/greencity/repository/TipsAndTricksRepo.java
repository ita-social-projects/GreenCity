package greencity.repository;

import greencity.entity.TipsAndTricks;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TipsAndTricksRepo extends JpaRepository<TipsAndTricks, Long>,
    JpaSpecificationExecutor<TipsAndTricks> {
    /**
     * Method returns {@link TipsAndTricks} by specific tags.
     *
     * @param tags list of tags to search by.
     * @return {@link TipsAndTricks} by specific tags.
     */
    @Query("SELECT DISTINCT tt FROM TipsAndTricks tt "
        + "left JOIN tt.titleTranslations title "
        + "left JOIN tt.tags ttt "
        + "WHERE lower(ttt.name) in :tags "
        + "and title.language.code = :languageCode "
        + "ORDER BY tt.creationDate DESC")
    Page<TipsAndTricks> find(String languageCode, Pageable pageable, List<String> tags);

    /**
     * Method returns all {@link TipsAndTricks} by id and languageCode.
     *
     * @param id           of {@link TipsAndTricks}
     * @param languageCode of titleTranslation.
     * @return all {@link TipsAndTricks} by languageCode and page.
     */
    Optional<TipsAndTricks> findByIdAndTitleTranslationsLanguageCode(Long id, String languageCode);

    /**
     * Method returns all {@link TipsAndTricks} by page.
     *
     * @param page of tips & tricks.
     * @return all {@link TipsAndTricks} by page.
     */
    Page<TipsAndTricks> findAllByOrderByCreationDateDesc(Pageable page);

    /**
     * Method returns all {@link TipsAndTricks} by languageCode and page.
     *
     * @param page         of tips & tricks.
     * @param languageCode of titleTranslation.
     * @return all {@link TipsAndTricks} by languageCode and page.
     */
    Page<TipsAndTricks> findByTitleTranslationsLanguageCodeOrderByCreationDateDesc(String languageCode, Pageable page);

    /**
     * Method returns {@link TipsAndTricks} by search query and page.
     *
     * @param searchQuery query to search
     * @return list of {@link TipsAndTricks}
     */
    @Query("select distinct tt from TipsAndTricks tt "
        + "left JOIN tt.titleTranslations title "
        + "left JOIN tt.textTranslations textTrans "
        + "where ("
        + "lower(title.content) like lower(CONCAT('%', :searchQuery, '%')) "
        + "or lower(textTrans.content) like lower(CONCAT('%', :searchQuery, '%')) "
        + "or tt.id in (select tt.id from TipsAndTricks tt inner join tt.tags ttt "
        + "where lower(ttt.name) like lower(CONCAT('%', :searchQuery, '%')))"
        + ")"
        + "and title.language.code = :languageCode")
    Page<TipsAndTricks> searchTipsAndTricks(Pageable pageable, String searchQuery, String languageCode);

    /**
     * Method returns {@link TipsAndTricks} by search query and page.
     *
     * @param pageable    {@link Pageable}.
     * @param searchQuery query to search.
     * @return list of {@link TipsAndTricks}.
     */
    @Query("select distinct tt from TipsAndTricks tt "
        + "left JOIN tt.titleTranslations title "
        + "left JOIN tt.textTranslations textTrans "
        + "where (CONCAT(tt.id,'') like lower(CONCAT('%', :searchQuery, '%')) "
        + "or lower(title.content) like lower(CONCAT('%', :searchQuery, '%')) "
        + "or lower(textTrans.content) like lower(CONCAT('%', :searchQuery, '%')) "
        + "or tt.id in (select tt.id from TipsAndTricks tt inner join tt.author a "
        + "where lower(a.name) like lower(CONCAT('%', :searchQuery, '%'))) "
        + "and title.language.code = :languageCode)")
    Page<TipsAndTricks> searchBy(Pageable pageable, String searchQuery, String languageCode);

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
