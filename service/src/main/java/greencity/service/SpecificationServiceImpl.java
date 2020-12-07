package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.constant.LogMessage;
import greencity.dto.specification.SpecificationNameDto;
import greencity.dto.specification.SpecificationVO;
import greencity.entity.Specification;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.SpecificationRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

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
    public SpecificationVO save(SpecificationVO specificationVO) {
        log.info(LogMessage.IN_SAVE, specificationVO);

        Specification specification = specificationRepo.save(modelMapper.map(specificationVO, Specification.class));
        return modelMapper.map(specification, SpecificationVO.class);
    }

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Override
    public SpecificationVO findById(Long id) {
        log.info(LogMessage.IN_FIND_BY_ID, id);

        return modelMapper.map(specificationRepo
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException(ErrorMessage.SPECIFICATION_VALUE_NOT_FOUND_BY_ID + id)),
            SpecificationVO.class);
    }

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Override
    public List<SpecificationVO> findAll() {
        log.info(LogMessage.IN_FIND_ALL);
        return modelMapper.map(specificationRepo.findAll(), new TypeToken<List<SpecificationVO>>() {
        }.getType());
    }

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Override
    public Long deleteById(Long id) {
        log.info(LogMessage.IN_DELETE_BY_ID, id);

        specificationRepo.delete(modelMapper.map(findById(id), Specification.class));
        return id;
    }

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Override
    public SpecificationVO findByName(String specificationName) {
        return modelMapper.map(specificationRepo
            .findByName(specificationName)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.SPECIFICATION_NOT_FOUND_BY_NAME)),
            SpecificationVO.class);
    }

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Override
    public List<SpecificationNameDto> findAllSpecificationDto() {
        List<SpecificationVO> specificationVOs = findAll();

        return specificationVOs.stream()
            .map(specification -> modelMapper.map(specification, SpecificationNameDto.class))
            .collect(Collectors.toList());
    }
}
