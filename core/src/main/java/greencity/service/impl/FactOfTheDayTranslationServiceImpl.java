package greencity.service.impl;

import greencity.constant.CacheConstants;
import greencity.constant.ErrorMessage;
import greencity.dto.factoftheday.FactOfTheDayTranslationDTO;
import greencity.entity.FactOfTheDayTranslation;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.FactOfTheDayTranslationRepo;
import greencity.service.FactOfTheDayTranslationService;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link FactOfTheDayTranslationService}.
 *
 * @author Mykola Lehkyi
 */
@Service
@EnableCaching
public class FactOfTheDayTranslationServiceImpl implements FactOfTheDayTranslationService {
    @Autowired
    private FactOfTheDayTranslationRepo factOfTheDayTranslationRepo;
    @Autowired
    private ModelMapper modelMapper;

    /**
     * Constructor with parameters.
     *
     * @author Mykola Lehkyi
     */
    @Autowired
    public FactOfTheDayTranslationServiceImpl(FactOfTheDayTranslationRepo factOfTheDayTranslationRepo) {
        this.factOfTheDayTranslationRepo = factOfTheDayTranslationRepo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<FactOfTheDayTranslation> getFactOfTheDayById(Long id) {
        return factOfTheDayTranslationRepo.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FactOfTheDayTranslation save(FactOfTheDayTranslation factOfTheDayTranslation) {
        return factOfTheDayTranslationRepo.save(factOfTheDayTranslation);
    }

    @Override
    public List<FactOfTheDayTranslation> saveAll(List<FactOfTheDayTranslation> factOfTheDayTranslation) {
        return factOfTheDayTranslationRepo.saveAll(factOfTheDayTranslation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAll(List<FactOfTheDayTranslation> factOfTheDayTranslations) {
        factOfTheDayTranslationRepo.deleteAll(factOfTheDayTranslations);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = CacheConstants.FACT_OF_THE_DAY_CACHE_NAME, key = "#languageCode")
    public FactOfTheDayTranslationDTO getRandomFactOfTheDayByLanguage(String languageCode) {
        FactOfTheDayTranslation factOfTheDayTranslation =
            factOfTheDayTranslationRepo.getRandomFactOfTheDayTranslation(languageCode).orElseThrow(() ->
                new NotFoundException(ErrorMessage.FACT_OF_THE_DAY_NOT_FOUND));
        return modelMapper.map(factOfTheDayTranslation, FactOfTheDayTranslationDTO.class);
    }
}
