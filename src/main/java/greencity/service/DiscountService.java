package greencity.service;

import greencity.dto.discount.DiscountDtoForAddPlace;
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

    Discount save(DiscountDtoForAddPlace discountDtoForAddPlace);

    Discount findByValue(int value);

    Discount update(Long id, Discount discount);
}
