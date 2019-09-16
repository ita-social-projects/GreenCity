package greencity.service.impl;

import greencity.dto.discount.DiscountDtoForAddPlace;
import greencity.entity.Discount;
import greencity.repository.DiscountRepo;
import greencity.service.DiscountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
    private ModelMapper modelMapper;

    @Override
    public Discount save(Discount discount) {
        return repo.save(discount);
    }

    @Override
    public Discount save(DiscountDtoForAddPlace discountDtoForAddPlace) {
        return repo.save(modelMapper.map(discountDtoForAddPlace, Discount.class));
    }

    @Override
    public Discount findByValue(int value) {
        return repo.findByValue(value);
    }

    @Override
    public Discount update(Long id, Discount discount) {
        return null;
    }
}
