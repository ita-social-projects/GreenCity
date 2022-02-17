package greencity.repository;

import greencity.entity.EcoNews;
import greencity.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface EventRepo extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    /**
     * Method for getting three last eco news.
     *
     * @return list of {@link EcoNews} instances.
     */
    @Query(value = "SELECT * FROM events ORDER BY title", nativeQuery = true)
    Page<Event> getAll(Pageable page);
}
