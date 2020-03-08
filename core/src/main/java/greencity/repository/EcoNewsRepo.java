package greencity.repository;

import greencity.entity.EcoNews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EcoNewsRepo extends JpaRepository<EcoNews, Long> {
    /**
     * Method for getting three last eco news.
     *
     * @return list of {@link EcoNews} instances.
     */
    @Query(nativeQuery = true, value = "SELECT * FROM eco_news ORDER BY creation_date DESC LIMIT 3")
    List<EcoNews> getThreeLastEcoNews();

    @Query(value = "SELECT e, count(t.name) FROM EcoNews e JOIN e.tags t WHERE t.name in (?1) GROUP BY e.id ORDER BY e.creationDate")
    List<EcoNews> find(List<String> tags);
}
