package greencity.repository;

import greencity.entity.EcoNews;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoNewsRepo extends JpaRepository<EcoNews, Long> {
    /**
     * Method for getting three last eco news.
     *
     * @return list of {@link EcoNews} instances.
     */
    @Query(nativeQuery = true, value = "SELECT * FROM eco_news ORDER BY creation_date DESC LIMIT 3")
    List<EcoNews> getThreeLastEcoNews();

    /**
     * Method returns {@link EcoNews} for specific tags.
     *
     * @param tags list of tags to search.
     * @return {@link EcoNews} for specific tags.
     */
    @Query("SELECT en FROM EcoNews en "
        + "JOIN en.tags t "
        + "WHERE t.name in :tags "
        + "GROUP BY en.id "
        + "ORDER BY en.creationDate DESC")
    Page<EcoNews> find(Pageable pageable, List<String> tags);

    /**
     * Method returns all {@link EcoNews} by page.
     *
     * @param page page of news.
     * @return all {@link EcoNews} by page.
     */
    @Query("SELECT en FROM EcoNews en ORDER BY en.creationDate DESC")
    Page<EcoNews> findAll(Pageable page);
}
