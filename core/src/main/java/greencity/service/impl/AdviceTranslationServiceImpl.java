package greencity.service.impl;

import greencity.dto.advice.AdviceVO;
import greencity.dto.advice.AdvicePostDto;
import greencity.dto.advice.AdviceTranslationVO;
import greencity.entity.localization.AdviceTranslation;
import greencity.repository.AdviceTranslationRepo;
import greencity.service.AdviceServiceImpl;
import greencity.service.AdviceTranslationService;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link AdviceTranslationService}.
 *
 * @author Vitaliy Dzen
 */
@Service
public class AdviceTranslationServiceImpl implements AdviceTranslationService {
    private final AdviceTranslationRepo adviceTranslationRepo;
    private final AdviceServiceImpl adviceService;
    private final ModelMapper modelMapper;

    /**
     * Constructor with parameters.
     *
     * @author Vitaliy Dzen
     */
    @Autowired
    public AdviceTranslationServiceImpl(AdviceTranslationRepo adviceTranslationRepo,
                                        AdviceServiceImpl adviceService, ModelMapper modelMapper) {
        this.adviceTranslationRepo = adviceTranslationRepo;
        this.adviceService = adviceService;
        this.modelMapper = modelMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AdviceTranslationVO> saveAdviceTranslation(
        List<AdviceTranslationVO> adviceTranslations) {
        List<AdviceTranslation> collect = modelMapper.map(adviceTranslations,
            new TypeToken<List<AdviceTranslation>>() {
            }.getType());
        List<AdviceTranslation> saved = adviceTranslationRepo.saveAll(collect);
        return modelMapper.map(saved, new TypeToken<List<AdviceTranslationVO>>() {
        }.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AdviceTranslationVO> saveAdviceAndAdviceTranslation(AdvicePostDto advicePostDTO) {
        AdviceVO save = adviceService.save(advicePostDTO);
        List<AdviceTranslationVO> list = modelMapper.map(advicePostDTO.getTranslations(),
            new TypeToken<List<AdviceTranslationVO>>() {
            }.getType());
        list.forEach(a -> a.setAdvice(save));

        return saveAdviceTranslation(list);
    }
}
