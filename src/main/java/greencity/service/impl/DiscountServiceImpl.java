package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.constant.LogMessage;
import greencity.entity.Discount;
import greencity.exception.NotFoundException;
import greencity.repository.DiscountRepo;
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
    private DiscountRepo repo;

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Override
    public Discount save(Discount discount) {
        return repo.save(discount);
    }

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Override
    public Discount findById(Long id) {
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
    public Set<Discount> findAllByPlaceId(Long placeId) {
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
