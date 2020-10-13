package greencity.repository;

import greencity.entity.EcoNews;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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
     * Method for getting three recommended eco news.
     * Query is based on database function fn_Recommended_EcoNews_By_Opened_Eco_News
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
    @Query(nativeQuery = true, value =
        "SELECT en.* FROM eco_news AS en "
            + "INNER JOIN eco_news_tags AS entag "
            + "ON en.id = entag.eco_news_id "
            + "INNER JOIN tags AS t ON entag.tags_id = t.id "
            + "WHERE lower(t.name) IN (:tags) "
            + "ORDER BY  en.creation_date DESC")
    Page<EcoNews> find(Pageable pageable, List<String> tags);

    /**
     * Method returns all {@link EcoNews} by page.
     *
     * @param page page of news.
     * @return all {@link EcoNews} by page.
     */
    Page<EcoNews> findAllByOrderByCreationDateDesc(Pageable page);

    /**
     * Method returns {@link EcoNews} by search query and page.
     *
     * @param searchQuery query to search
     * @return list of {@link EcoNews}
     */
    @Query(nativeQuery = true, value = " SELECT distinct * FROM public.fn_textsearcheconews ( :searchQuery ) ")
    Page<EcoNews> searchEcoNews(Pageable pageable, String searchQuery);

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
     *  Method returns {@link EcoNews} by search query and page.
     *
     * @param paging {@link Pageable}.
     * @param query  query to search.
     * @return list of {@link EcoNews}.
     */
    @Query(nativeQuery = true, value =
        "Select e.id, e.title, e.author_id, e.text, u.name, e.creation_date, e.source, t.name, e.image_path "
            + "FROM eco_news e "
            + "JOIN users u on u.id = e.author_id "
            + "JOIN eco_news_tags ent on e.id = ent.eco_news_id "
            + "JOIN tags t on t.id = ent.tags_id "
            + "WHERE concat(e.id,'') like :query or "
            + "    lower(e.title) like lower(concat('%', :query, '%')) or "
            + "    lower(e.text) like lower(concat('%', :query, '%')) or "
            + "    lower(u.name) like lower(concat('%', :query, '%')) or "
            + "    lower(concat(e.creation_date,'')) like lower(concat('%', :query, '%')) or "
            + "    lower(e.source) like lower(concat('%', :query, '%')) or "
            + "    lower(t.name) like lower(concat('%', :query, '%'))")
    Page<EcoNews> searchEcoNewsBy(Pageable paging, String query);
}
