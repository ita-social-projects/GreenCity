package greencity.repository;

import greencity.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Provides an interface to manage {@link Category} entity.
 */
@Repository
public interface CategoryRepo extends JpaRepository<Category, Long> {
    /**
     * Finds category by name.
     *
     * @param name to find by.
     * @return a category by name.
     */
    Category findByName(String name);

    /**
     * Find category by name or nameUa.
     *
     * @param name to find by.
     * @return a category by name.
     */
    @Query(nativeQuery = true, value = "select * from categories c "
        + "where lower(c.name) like lower(concat('%', :name, '%')) "
        + "OR lower(c.name_ua) like lower(concat('%', :name, '%'))")
    Category findCategoryByName(String name);
}
