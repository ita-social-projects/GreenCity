package greencity.service;

import greencity.dto.factoftheday.FactOfTheDayTranslationVO;
import greencity.dto.factoftheday.FactOfTheDayVO;
import java.util.List;
import java.util.Optional;

public interface FactOfTheDayTranslationService {
    /**
     * Method find {@link FactOfTheDayVO} by id.
     *
     * @param id of {@link FactOfTheDayVO}
     * @return {@link FactOfTheDayVO}
     * @author Mykola Lehkyi
     */
    Optional<FactOfTheDayTranslationVO> getFactOfTheDayById(Long id);

    /**
     * Method saves new {@link FactOfTheDayTranslationVO}.
     *
     * @param factOfTheDayTranslation {@link FactOfTheDayTranslationVO}
     * @return instance of {@link FactOfTheDayTranslationVO}
     * @author Mykola Lehkyi
     */
    FactOfTheDayTranslationVO save(FactOfTheDayTranslationVO factOfTheDayTranslation);

    /**
     * Method saves all new {@link FactOfTheDayTranslationVO}.
     *
     * @param factOfTheDayTranslation {@link FactOfTheDayTranslationVO}
     * @return instance of {@link FactOfTheDayTranslationVO}
     * @author Mykola Lehkyi
     */
    List<FactOfTheDayTranslationVO> saveAll(List<FactOfTheDayTranslationVO> factOfTheDayTranslation);

    /**
     * Method deletes all {@link FactOfTheDayTranslationVO}.
     *
     * @param factOfTheDayTranslation list of translations
     * @author Mykola Lehkyi
     */
    void deleteAll(List<FactOfTheDayTranslationVO> factOfTheDayTranslation);
}
