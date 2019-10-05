package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.constant.LogMessage;
import greencity.entity.DiscountValue;
import greencity.exception.NotFoundException;
import greencity.repository.DiscountValuesRepo;
import greencity.service.DiscountService;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service implementation for Discount entity.
 *
 * @version 1.0
 */
@Service
@AllArgsConstructor
@Slf4j
public class DiscountServiceImpl implements DiscountService {
    private DiscountValuesRepo repo;

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Override
    public DiscountValue save(DiscountValue discountValue) {
        return repo.save(discountValue);
    }

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Override
    public DiscountValue findById(Long id) {
        log.info(LogMessage.IN_FIND_BY_ID, id);

        return repo
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException(ErrorMessage.DISCOUNT_NOT_FOUND_BY_ID + id));
    }

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Override
    public Set<DiscountValue> findAllByPlaceId(Long placeId) {
        return repo.findAllByPlaceId(placeId);
    }

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Override
    public void deleteAllByPlaceId(Long placeId) {
        repo.deleteAllByPlaceId(placeId);
    }
}
