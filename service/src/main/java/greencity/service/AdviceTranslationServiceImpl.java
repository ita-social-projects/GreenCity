package greencity.service;

import greencity.dto.advice.AdvicePostDto;
import greencity.dto.advice.AdviceTranslationVO;
import greencity.dto.advice.AdviceVO;
import greencity.entity.localization.AdviceTranslation;
import greencity.repository.AdviceTranslationRepo;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public AdviceVO saveAdviceAndAdviceTranslation(AdvicePostDto advicePostDTO) {
        return adviceService.save(advicePostDTO);
    }
}
