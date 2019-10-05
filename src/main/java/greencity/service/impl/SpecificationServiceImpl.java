package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.constant.LogMessage;
import greencity.dto.specification.SpecificationNameDto;
import greencity.entity.Specification;
import greencity.exception.NotFoundException;
import greencity.repository.SpecificationRepo;
import greencity.service.SpecificationService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
    public Optional<Specification> findByName(String name) {
        return specificationRepo.findByName(name);
    }

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Override
    public List<SpecificationNameDto> findAllSpecificationDto() {
        List<Specification> specifications = findAll();
        return specifications.stream()
            .map(specification -> modelMapper.map(specification, SpecificationNameDto.class))
            .collect(Collectors.toList());
    }
}
