package greencity.service.impl;

import greencity.dto.language.LanguageDTO;
import greencity.entity.Language;
import greencity.repository.LanguageRepo;
import greencity.service.AdviceService;
import greencity.service.LanguageService;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link AdviceService}.
 *
 * @author Vitaliy Dzen
 */
@Service
public class LanguageServiceImpl implements LanguageService {
    private final LanguageRepo languageRepo;
    private final ModelMapper modelMapper;

    /**
     * Constructor with parameters.
     *
     * @author Vitaliy Dzen
     */
    @Autowired
    public LanguageServiceImpl(LanguageRepo languageRepo, ModelMapper modelMapper) {
        this.languageRepo = languageRepo;
        this.modelMapper = modelMapper;
    }

    /**
     * Method finds all {@link Language}.
     *
     * @return List of all {@link LanguageDTO}
     * @author Vitaliy Dzen
     */
    @Override
    public List<LanguageDTO> getAllLanguages() {
        return modelMapper.map(languageRepo.findAll(), new TypeToken<List<LanguageDTO>>() {
        }.getType());
    }
}
