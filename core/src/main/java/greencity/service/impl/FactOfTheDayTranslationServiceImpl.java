package greencity.service.impl;

import greencity.entity.FactOfTheDayTranslation;
import greencity.repository.FactOfTheDayTranslationRepo;
import greencity.service.FactOfTheDayTranslationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link FactOfTheDayTranslationService}.
 *
 * @author Mykola Lehkyi
 */
@AllArgsConstructor
@Service
public class FactOfTheDayTranslationServiceImpl implements FactOfTheDayTranslationService {

    private final FactOfTheDayTranslationRepo factOfTheDayTranslationRepo;

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
}
