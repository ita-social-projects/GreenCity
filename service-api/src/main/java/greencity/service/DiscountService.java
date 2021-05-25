package greencity.service;

import greencity.dto.discount.DiscountValueVO;
import java.util.Set;

/**
 * Provides the interface to manage {@code Discount} entity.
 */
public interface DiscountService {
    /**
     * Method for saving new Discount to database.
     *
     * @param discountValueVO - Discount entity.
     * @return a discount.
     */
    DiscountValueVO save(DiscountValueVO discountValueVO);

    /**
     * Find Discount entity by id.
     *
     * @param id - Discount id.
     * @return Discount entity.
     */
    DiscountValueVO findById(Long id);

    /**
     * Finds all {@code Discount} records related to the specified {@code Place}.
     *
     * @param placeId to find by.
     * @return a set of the {@code Discount} for the place by id.
     */
    Set<DiscountValueVO> findAllByPlaceId(Long placeId);

    /**
     * Delete all {@code Discount} records related to the specified {@code Place}.
     *
     * @param placeId to find by.
     */
    void deleteAllByPlaceId(Long placeId);
}
