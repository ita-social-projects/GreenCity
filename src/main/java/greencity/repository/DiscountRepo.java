package greencity.repository;

import greencity.entity.Discount;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountRepo  extends JpaRepository<Discount, Long> {
    /**
     * Finds all {@code Discount} records related to the specified {@code Place}.
     *
     * @param placeId to find by.
     * @return a set of the {@code Discount} for the place by id.
     */
    Set<Discount> findAllByPlaceId(Long placeId);

    /**
     * Delete all {@code Discount} records related to the specified {@code Place}.
     *
     * @param placeId to find by.
     */
    void deleteAllByPlaceId(Long placeId);
}
