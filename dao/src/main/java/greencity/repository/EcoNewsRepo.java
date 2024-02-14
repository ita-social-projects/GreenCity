package greencity.repository;

import greencity.entity.EcoNews;
import java.util.List;
import java.util.Optional;
import greencity.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoNewsRepo extends JpaRepository<EcoNews, Long>, JpaSpecificationExecutor<EcoNews> {
    /**
     * Method for getting three last eco news.
     *
     * @return list of {@link EcoNews} instances.
     */
    @Query(nativeQuery = true, value = "SELECT * FROM eco_news ORDER BY creation_date DESC LIMIT 3")
    List<EcoNews> getThreeLastEcoNews();

    /**
     * Method for deleting eco news by list of ids.
     *
     * @param ids list of deleted eco news ids.
     */

    @Modifying
    @Query("DELETE FROM EcoNews e WHERE e.id IN (?1)")
    void deleteEcoNewsWithIds(List<Long> ids);

    /**
     * Method for getting three recommended eco news. Query is based on database
     * function fn_Recommended_EcoNews_By_Opened_Eco_News
     *
     * @param openedEcoNewsId id of opened eco news.
     * @return list of three recommended {@link EcoNews} instances.
     */
    @Query(nativeQuery = true,
        value = "SELECT * FROM fn_Recommended_EcoNews_By_Opened_Eco_News(:openedEcoNewsId)")
    List<EcoNews> getThreeRecommendedEcoNews(Long openedEcoNewsId);

    /**
     * Method returns {@link EcoNews} for specific tags.
     *
     * @param tags list of tags to search.
     * @return {@link EcoNews} for specific tags.
     */
    @Query(nativeQuery = true, value = "SELECT DISTINCT en.* FROM eco_news AS en "
        + "INNER JOIN eco_news_tags AS entag "
        + "ON en.id = entag.eco_news_id "
        + "INNER JOIN tag_translations AS t ON entag.tags_id = t.tag_id "
        + "WHERE lower(t.name) IN (:tags) "
        + "ORDER BY en.creation_date DESC")
    Page<EcoNews> findByTags(Pageable pageable, List<String> tags);

    /**
     * Method returns all {@link EcoNews} by page.
     *
     * @param page page of news.
     * @return all {@link EcoNews} by page.
     */
    Page<EcoNews> findAllByOrderByCreationDateDesc(Pageable page);

    /**
     * Method returns all users {@link EcoNews} by page.
     *
     * @param user author of news.
     * @param page page of news.
     * @return all {@link EcoNews} by page.
     */
    Page<EcoNews> findAllByAuthorOrderByCreationDateDesc(User user, Pageable page);

    /**
     * Method that finds {@link EcoNews} by id.
     *
     * @param id {@link Long}.
     * @return {@link Optional} of {@link EcoNews}
     */
    @Query("SELECT e FROM EcoNews e LEFT JOIN FETCH e.tags WHERE e.id = :id")
    Optional<EcoNews> findById(Long id);

    /**
     * Method returns {@link EcoNews} by search query and page.
     *
     * @param searchQuery query to search
     * @return list of {@link EcoNews}
     */
    @Query(nativeQuery = true, value = " SELECT distinct * FROM public.fn_searcheconews "
        + "( :searchQuery, :languageCode) ")
    Page<EcoNews> searchEcoNews(Pageable pageable, String searchQuery, String languageCode);

    /**
     * Method for getting all published news by user id.
     *
     * @param userId {@link Long} user id.
     * @return list of {@link EcoNews} instances.
     * @author Vira Maksymets
     */
    @Query(nativeQuery = true,
        value = "SELECT * FROM eco_news WHERE author_id = :userId")
    List<EcoNews> findAllByUserId(@Param("userId") Long userId);

    /**
     * Method for getting amount of published news by user id.
     *
     * @param id {@link Long} user id.
     * @return amount of published news by user id.
     * @author Marian Datsko
     */
    @Query(nativeQuery = true,
        value = " SELECT COUNT(author_id) "
            + " FROM eco_news WHERE author_id = :userId")
    Long getAmountOfPublishedNewsByUserId(@Param("userId") Long id);

    /**
     * Method returns {@link EcoNews} by search query and page.
     *
     * @param paging {@link Pageable}.
     * @param query  query to search.
     * @return list of {@link EcoNews}.
     */
    @Query(nativeQuery = true,
        value = "Select e.id, e.title, e.author_id, e.text, u.name, e.creation_date, e.source, tt.name, e.image_path "
            + "FROM eco_news e "
            + "JOIN users u on u.id = e.author_id "
            + "JOIN eco_news_tags ent on e.id = ent.eco_news_id "
            + "JOIN tag_translations tt on tt.tag_id = ent.tags_id "
            + "WHERE concat(e.id,'') like :query or "
            + "    lower(e.title) like lower(concat('%', :query, '%')) or "
            + "    lower(e.text) like lower(concat('%', :query, '%')) or "
            + "    lower(u.name) like lower(concat('%', :query, '%')) or "
            + "    lower(concat(e.creation_date,'')) like lower(concat('%', :query, '%')) or "
            + "    lower(e.source) like lower(concat('%', :query, '%')) or "
            + "    lower(tt.name) like lower(concat('%', :query, '%'))")
    Page<EcoNews> searchEcoNewsBy(Pageable paging, String query);

    /**
     * Method for get total Eco News count.
     *
     * @return {@link int} total count of Eco News
     */
    @Query(nativeQuery = true,
        value = "select count(id) from eco_news")
    int totalCountOfCreationNews();
}