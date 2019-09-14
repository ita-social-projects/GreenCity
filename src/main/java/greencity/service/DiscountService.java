package greencity.service;

import greencity.entity.Discount;

/**
 * Provides the interface to manage {@code Discount} entity.
 */
public interface DiscountService {
    /**
     * Method for saving new Category to database.
     *
     * @param discount - Discount entity.
     * @return a category.
     */
    Discount save(Discount discount);

    Discount findByValue(int value);

    Discount update(Long id, Discount discount);
}
