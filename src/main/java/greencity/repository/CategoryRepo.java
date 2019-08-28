package greencity.repository;

import greencity.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Provides an interface to manage {@link Category} entity. */
@Repository
public interface CategoryRepo extends JpaRepository<Category, Long> {

    Boolean existsByName(String name);
    /**
     * Finds category by name.
     *
     * @param name to find by.
     * @return a category by name.
     */
    Category findByName(String name);
}
