package greencity.repositories;

import greencity.entities.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Provides an interface to manage {@link Location} entity.
 * */
@Repository
public interface LocationRepo extends JpaRepository<Location, Long> {}
