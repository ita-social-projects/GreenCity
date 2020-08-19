package greencity.service.impl;

import greencity.constant.CacheConstants;
import greencity.constant.ErrorMessage;
import greencity.dto.factoftheday.FactOfTheDayTranslationDTO;
import greencity.entity.FactOfTheDayTranslation;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.FactOfTheDayTranslationRepo;
import greencity.service.FactOfTheDayTranslationService;
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
     * Method find {@link FactOfTheDayTranslation} by id.
     *
     * @param id of {@link FactOfTheDayTranslation}
     * @return Optional of{@link FactOfTheDayTranslation}
     * @author Mykola Lehkyi
     */
    @Override
    public Optional<FactOfTheDayTranslation> getFactOfTheDayById(Long id) {
        return factOfTheDayTranslationRepo.findById(id);
    }

    /**
     * Method saves new {@link FactOfTheDayTranslation}.
     *
     * @param factOfTheDayTranslation {@link FactOfTheDayTranslation}
     * @return instance of {@link FactOfTheDayTranslation}
     * @author Mykola Lehkyi
     */
    @Override
    public FactOfTheDayTranslation save(FactOfTheDayTranslation factOfTheDayTranslation) {
        return factOfTheDayTranslationRepo.save(factOfTheDayTranslation);
    }

    /**
     * Method deletes {@link FactOfTheDayTranslation} by id.
     *
     * @param id Long of {@link FactOfTheDayTranslation}
     * @return id of deleted element
     * @author Mykola Lehkyi
     */
    @Override
    public Long delete(Long id) {
        if (!(factOfTheDayTranslationRepo.findById(id).isPresent())) {
            throw new NotDeletedException(ErrorMessage.FACT_OF_THE_DAY_TRANSLATION_NOT_DELETED);
        }
        factOfTheDayTranslationRepo.deleteById(id);
        return id;
    }

    /**
     * Method returns random fact by given language.
     *
     * @param languageCode String
     * @return {@link FactOfTheDayTranslationDTO}
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
