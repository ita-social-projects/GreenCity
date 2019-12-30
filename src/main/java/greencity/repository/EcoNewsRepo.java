package greencity.repository;

import greencity.entity.EcoNews;
import java.util.List;
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
}
