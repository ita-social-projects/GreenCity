package greencity.repository;

import greencity.entity.Specification;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Provides an interface to manage {@link Specification} entity.
 */
@Repository
public interface SpecificationRepo extends JpaRepository<Specification, Long> {
    /**
     * Finds Specification by name.
     *
     * @param name to find by.
     * @return a Specification by name.
     */
    Optional<Specification> findByName(String name);
}
