package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.constant.LogMessage;
import greencity.dto.specification.SpecificationValueAddDto;
import greencity.entity.SpecificationValue;
import greencity.exception.BadRequestException;
import greencity.exception.NotFoundException;
import greencity.repository.SpecificationValueRepo;
import greencity.service.SpecificationValueService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

/**
 * The class provides implementation of the {@code SpecificationValueService}.
 */
@Slf4j
@AllArgsConstructor
@Service
public class SpecificationValueServiceImpl implements SpecificationValueService {
    private SpecificationValueRepo repo;
    private ModelMapper modelMapper;

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Override
    public SpecificationValue save(SpecificationValueAddDto dto) {
        log.info(LogMessage.IN_SAVE, dto.getValue());

        SpecificationValue byValue = repo.findByValue(dto.getValue());
        if (byValue != null) {
            throw new BadRequestException(
                ErrorMessage.CATEGORY_ALREADY_EXISTS_BY_THIS_NAME);
        }
        return repo.save(modelMapper.map(dto, SpecificationValue.class));
    }

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Override
    public SpecificationValue save(SpecificationValue specificationValue) {
        log.info(LogMessage.IN_SAVE, specificationValue);

        return repo.save(specificationValue);
    }

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Override
    public SpecificationValue findById(Long id) {
        log.info(LogMessage.IN_FIND_BY_ID, id);

        return repo
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException(ErrorMessage.SPECIFICATION_NOT_FOUND_BY_ID + id));
    }

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Override
    public List<SpecificationValue> findAll() {
        log.info(LogMessage.IN_FIND_ALL);

        return repo.findAll();
    }

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Override
    public Long deleteById(Long id) {
        log.info(LogMessage.IN_DELETE_BY_ID, id);

        repo.delete(findById(id));
        return id;
    }

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Override
    public SpecificationValue findByValue(String value) {
        return repo.findByValue(value);
    }
}
