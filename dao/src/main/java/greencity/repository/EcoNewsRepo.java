package greencity.repository;

import greencity.entity.EcoNews;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoNewsRepo extends JpaRepository<EcoNews, Long>, JpaSpecificationExecutor<EcoNews> {
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
     * Method returns all {@link EcoNews} by page.
     *
     * @param page page of news.
     * @return all {@link EcoNews} by page.
     */
    Page<EcoNews> findAllByOrderByCreationDateDesc(Pageable page);

    /**
     * Method that finds {@link EcoNews} by id.
     *
     * @param id {@link Long}.
     * @return {@link Optional} of {@link EcoNews}
     */
    @Query("SELECT e FROM EcoNews e LEFT JOIN FETCH e.tags WHERE e.id = :id")
    Optional<EcoNews> findById(Long id);

    /**
     * Method for getting all published news by user id.
     *
     * @param authorId {@link Long} user id.
     * @return list of {@link EcoNews} instances.
     * @author Vira Maksymets
     */
    List<EcoNews> findAllByAuthorId(Long authorId);

    /**
     * Method for getting amount of published news.
     *
     * @return amount of published news.
     * @author Ilia Rozhko
     */
    long count();

    /**
     * Method for getting amount of published news by user id.
     *
     * @param authorId {@link Long} user id.
     * @return amount of published news by user id.
     * @author Marian Datsko
     */
    long countByAuthorId(Long authorId);

    /**
     * Method returns {@link EcoNews} by search query and page.
     *
     * @param paging {@link Pageable}.
     * @param query  query to search.
     * @return list of {@link EcoNews}.
     */
    @Query(nativeQuery = true,
        value = "Select DISTINCT e.* "
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

    /**
     * Method for getting 3 most liked and commented eco-news.
     *
     * @return list of {@link EcoNews}.
     */
    @Query(nativeQuery = true,
        value = """
            SELECT e.*
            FROM eco_news e
            LEFT JOIN (SELECT eco_news_id, count(*) AS count_likes
                       FROM eco_news_users_likes
                       GROUP BY eco_news_id) likes ON e.id = likes.eco_news_id
            LEFT JOIN (SELECT article_id, count(*) AS count_comments
                       FROM comments
                       WHERE article_type = 'ECO_NEWS'
                       GROUP BY article_id) comments ON e.id = comments.article_id
            WHERE e.creation_date > now() - INTERVAL '7 DAY'
            ORDER BY likes.count_likes DESC NULLS LAST,
                     comments.count_comments DESC NULLS LAST
            LIMIT 3;
            """)
    List<EcoNews> findThreeInterestingEcoNews();

    /**
     * Method for finding eco news by title containing the given keyword.
     *
     * @param title the keyword to search in the title
     * @return list of {@link EcoNews} instances.
     */
    List<EcoNews> findByTitleContaining(String title);
}
