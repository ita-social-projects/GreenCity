package greencity.repository;

import greencity.entity.NewsSubscriber;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsSubscriberRepo extends JpaRepository<NewsSubscriber, Long> {
    /**
     * Method for finding newsSubscriber by email.
     *
     * @param email email for searching.
     * @return optional of {@link NewsSubscriber}
     */
    Optional<NewsSubscriber> findByEmail(String email);
}
