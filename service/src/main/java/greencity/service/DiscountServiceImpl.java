package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.constant.LogMessage;
import greencity.dto.discount.DiscountValueVO;
import greencity.entity.DiscountValue;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.DiscountValuesRepo;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
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
    private ModelMapper modelMapper;

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Override
    public DiscountValueVO save(DiscountValueVO discountValueVO) {
        DiscountValue discountValue = repo.save(modelMapper.map(discountValueVO, DiscountValue.class));
        return modelMapper.map(discountValue, DiscountValueVO.class);
    }

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Override
    public DiscountValueVO findById(Long id) {
        log.info(LogMessage.IN_FIND_BY_ID, id);
        DiscountValue discountValue = repo
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException(ErrorMessage.DISCOUNT_NOT_FOUND_BY_ID + id));
        return modelMapper.map(discountValue, DiscountValueVO.class);
    }

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Override
    public Set<DiscountValueVO> findAllByPlaceId(Long placeId) {
        return modelMapper.map(repo.findAllByPlaceId(placeId), new TypeToken<Set<DiscountValueVO>>() {
        }.getType());
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
