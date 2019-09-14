package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.constant.LogMessage;
import greencity.dto.specification.SpecificationAddDto;
import greencity.entity.Specification;
import greencity.exception.BadRequestException;
import greencity.exception.NotFoundException;
import greencity.repository.SpecificationRepo;
import greencity.service.SpecificationService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

/**
 * The class provides implementation of the {@code SpecificationService}.
 */
@Slf4j
@AllArgsConstructor
@Service
public class SpecificationServiceImpl implements SpecificationService {
    private SpecificationRepo specificationRepo;
    private ModelMapper modelMapper;

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Override
    public Specification save(SpecificationAddDto dto) {
        log.info(LogMessage.IN_SAVE);

        Specification byName = specificationRepo.findByName(dto.getName());

        if (byName != null) {
            throw new BadRequestException(
                ErrorMessage.SPECIFICATION_ALREADY_EXISTS_BY_THIS_NAME);
        }
        return specificationRepo.save(modelMapper.map(dto, Specification.class));
    }

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Override
    public Specification save(Specification specification) {
        log.info(LogMessage.IN_SAVE, specification);

        return specificationRepo.save(specification);
    }

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Override
    public Specification findById(Long id) {
        log.info(LogMessage.IN_FIND_BY_ID, id);

        return specificationRepo
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException(ErrorMessage.SPECIFICATION_VALUE_NOT_FOUND_BY_ID + id));
    }

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Override
    public List<Specification> findAll() {
        log.info(LogMessage.IN_FIND_ALL);

        return specificationRepo.findAll();
    }

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Override
    public Long deleteById(Long id) {
        log.info(LogMessage.IN_DELETE_BY_ID, id);

        specificationRepo.delete(findById(id));
        return id;
    }

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Override
    public Specification findByName(String name) {
        return specificationRepo.findByName(name);
    }
}
