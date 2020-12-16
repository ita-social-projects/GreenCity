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
    @Query(nativeQuery = true, value = "SELECT DISTINCT t.* FROM tips_and_tricks AS t "
        + "INNER JOIN tips_and_tricks_tags AS ttt ON t.id = ttt.tips_and_tricks_id "
        + "INNER JOIN tag_translations AS tt ON tt.tag_id = ttt.tags_id "
        + "WHERE lower(tt.name) IN (:tags)")
    Page<TipsAndTricks> find(Pageable pageable, List<String> tags);

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
    @Query(nativeQuery = true,
        value = " SELECT distinct * FROM public.fn_texttipsandtricks ( :searchQuery, :languageCode) ")
    Page<TipsAndTricks> searchTipsAndTricks(Pageable pageable, String searchQuery, String languageCode);

    /**
     * Method returns {@link TipsAndTricks} by search query and page.
     *
     * @param pageable    {@link Pageable}.
     * @param searchQuery query to search.
     * @return list of {@link TipsAndTricks}.
     */
    @Query(nativeQuery = true,
        value = " SELECT distinct * FROM public.fn_textsearchtipsandtricksforadmin ( :searchQuery, :languageCode) ")
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

    /**
     * Method for finding {@link TipsAndTricks} by query.
     * 
     * @param pageable {@link Pageable}.
     * @param query    query to search,
     * @return list of {@link TipsAndTricks}.
     */
    @Query(value = "SELECT DISTINCT t FROM TipsAndTricks t JOIN FETCH t.titleTranslations AS title "
        + "JOIN FETCH t.author AS user "
        + "WHERE CONCAT(t.id, '') LIKE LOWER(concat(:query, '')) OR "
        + "LOWER(CONCAT(t.creationDate,'')) LIKE LOWER(CONCAT('%', :query, '%')) OR "
        + "LOWER(user.name) LIKE LOWER(CONCAT('%', :query, '%')) OR "
        + "LOWER(title.content) like LOWER(CONCAT('%', :query, '%')) OR "
        + "LOWER(t.imagePath) LIKE LOWER(CONCAT('%', :query, '%')) OR "
        + "LOWER(t.source) LIKE LOWER(CONCAT('%', :query, '%'))",
        countQuery = "SELECT COUNT(a) FROM TipsAndTricks t")
    Page<TipsAndTricks> searchTipsAndTricksBy(Pageable pageable, String query);
}
