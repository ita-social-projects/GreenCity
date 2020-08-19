package greencity.service;

import greencity.dto.factoftheday.FactOfTheDayTranslationDTO;
import greencity.entity.FactOfTheDay;
import greencity.entity.FactOfTheDayTranslation;
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
     * Method delete {@link FactOfTheDayTranslation} by id.
     *
     * @param id Long
     * @return id of deleted {@link FactOfTheDayTranslation}
     * @author Mykola Lehkyi
     */
    Long delete(Long id);

    /**
     * Method return random {@link FactOfTheDayTranslation} by languageCode.
     *
     * @param languageCode String
     * @return FactOfTheDayTranslation
     * @author Mykola Lehkyi
     */
    FactOfTheDayTranslationDTO getRandomFactOfTheDayByLanguage(String languageCode);
}
