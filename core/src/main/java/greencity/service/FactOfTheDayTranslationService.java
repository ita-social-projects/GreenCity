package greencity.service;

import greencity.dto.factoftheday.FactOfTheDayTranslationDTO;
import greencity.entity.FactOfTheDay;
import greencity.entity.FactOfTheDayTranslation;
import java.util.List;
import java.util.Optional;

public interface FactOfTheDayTranslationService {
    /**
     * Method find {@link FactOfTheDay} by id.
     *
     * @param id of {@link FactOfTheDay}
     * @return {@link FactOfTheDay}
     * @author Mykola Lehkyi
     */
    Optional<FactOfTheDayTranslation> getFactOfTheDayById(Long id);

    /**
     * Method saves new {@link FactOfTheDayTranslation}.
     *
     * @param factOfTheDayTranslation {@link FactOfTheDayTranslation}
     * @return instance of {@link FactOfTheDayTranslation}
     * @author Mykola Lehkyi
     */
    FactOfTheDayTranslation save(FactOfTheDayTranslation factOfTheDayTranslation);

    /**
     * Method saves all new {@link FactOfTheDayTranslation}.
     *
     * @param factOfTheDayTranslation {@link FactOfTheDayTranslation}
     * @return instance of {@link FactOfTheDayTranslation}
     * @author Mykola Lehkyi
     */
    List<FactOfTheDayTranslation> saveAll(List<FactOfTheDayTranslation> factOfTheDayTranslation);

    /**
     * Method deletes all {@link FactOfTheDayTranslation}.
     *
     * @param factOfTheDayTranslation list of translations
     * @author Mykola Lehkyi
     */
    void deleteAll(List<FactOfTheDayTranslation> factOfTheDayTranslation);

    /**
     * Method return random {@link FactOfTheDayTranslation} by languageCode.
     *
     * @param languageCode String
     * @return FactOfTheDayTranslation
     * @author Mykola Lehkyi
     */
    FactOfTheDayTranslationDTO getRandomFactOfTheDayByLanguage(String languageCode);
}
