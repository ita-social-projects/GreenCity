package greencity.service.impl;

import greencity.dto.advice.AdviceDto;
import greencity.dto.advice.AdviceGeneralDto;
import greencity.dto.advice.AdvicePostDto;
import greencity.dto.advice.AdviceTranslationGeneralDto;
import greencity.entity.Advice;
import greencity.entity.localization.AdviceTranslation;
import greencity.repository.AdviceTranslationRepo;
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
    public List<AdviceTranslationGeneralDto> saveAdviceTranslation(
        List<AdviceTranslationGeneralDto> adviceTranslations) {
        List<AdviceTranslation> collect = modelMapper.map(adviceTranslations,
            new TypeToken<List<AdviceTranslation>>() {
            }.getType());
        List<AdviceTranslation> saved = adviceTranslationRepo.saveAll(collect);
        return modelMapper.map(saved, new TypeToken<List<AdviceTranslationGeneralDto>>() {
        }.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AdviceTranslationGeneralDto> saveAdviceAndAdviceTranslation(AdvicePostDto advicePostDTO) {
        AdviceGeneralDto save = adviceService.save(advicePostDTO);
        List<AdviceTranslationGeneralDto> list = modelMapper.map(advicePostDTO.getTranslations(),
            new TypeToken<List<AdviceTranslationGeneralDto>>() {
            }.getType());
        list.forEach(a -> a.setAdvice(save));

        return saveAdviceTranslation(list);
    }
}
