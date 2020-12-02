package greencity.service.impl;

import greencity.dto.advice.AdvicePostDTO;
import greencity.entity.Advice;
import greencity.entity.localization.AdviceTranslation;
import greencity.repository.AdviceTranslationRepo;
import greencity.service.AdviceTranslationService;
import java.util.List;
import java.util.stream.Collectors;
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
     * Method saves new {@link AdviceTranslation}.
     *
     * @param adviceTranslations {@link AdviceTranslation}
     * @return List of {@link AdviceTranslation}
     * @author Vitaliy Dzen
     */
    @Override
    public List<AdviceTranslation> saveAdviceTranslation(List<AdviceTranslation> adviceTranslations) {
        return adviceTranslationRepo.saveAll(adviceTranslations);
    }

    /**
     * Method saves new {@link Advice} and list of new {@link AdviceTranslation} with relationship to {@link Advice}.
     *
     * @param advicePostDTO {@link AdvicePostDTO}
     * @return List of {@link AdviceTranslation}
     * @author Vitaliy Dzen
     */
    @Override
    public List<AdviceTranslation> saveAdviceAndAdviceTranslation(AdvicePostDTO advicePostDTO) {
        Advice advice = adviceService.save(advicePostDTO);
        List<AdviceTranslation> adviceTranslations = modelMapper.map(advicePostDTO.getTranslations(),
            new TypeToken<List<AdviceTranslation>>() {
            }.getType());
        adviceTranslations = adviceTranslations
            .stream()
            .peek(a -> a.setAdvice(advice))
            .collect(Collectors.toList());
        return saveAdviceTranslation(adviceTranslations);
    }
}
