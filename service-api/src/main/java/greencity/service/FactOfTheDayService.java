package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.factoftheday.*;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface FactOfTheDayService {
    /**
     * Method finds all {@link FactOfTheDayVO} with pageable configuration.
     *
     * @param pageable {@link Pageable}
     * @return {@link PageableDto} with list of all {@link FactOfTheDayDTO}
     * @author Mykola Lehkyi
     */
    PageableDto<FactOfTheDayDTO> getAllFactsOfTheDay(Pageable pageable);

    /**
     * Method find {@link FactOfTheDayVO} by id.
     *
     * @param id of {@link FactOfTheDayVO}
     * @return {@link FactOfTheDayVO}
     * @author Mykola Lehkyi
     */
    FactOfTheDayDTO getFactOfTheDayById(Long id);

    /**
     * Method saves new {@link FactOfTheDayVO} and
     * {@link FactOfTheDayTranslationVO}.
     *
     * @param fact {@link FactOfTheDayPostDTO}
     * @return instance of {@link FactOfTheDayPostDTO}
     * @author Mykola Lehkyi
     */
    FactOfTheDayPostDTO saveFactOfTheDayAndTranslations(FactOfTheDayPostDTO fact);

    /**
     * Method saves new {@link FactOfTheDayVO} and
     * {@link FactOfTheDayTranslationVO}.
     *
     * @param fact {@link FactOfTheDayPostDTO}
     * @return instance of {@link FactOfTheDayPostDTO}
     * @author Mykola Lehkyi
     */
    FactOfTheDayPostDTO updateFactOfTheDayAndTranslations(FactOfTheDayPostDTO fact);

    /**
     * Method updates {@link FactOfTheDayVO}{@link FactOfTheDayVO} and
     * {@link FactOfTheDayTranslationVO}.
     *
     * @param fact {@link FactOfTheDayVO}
     * @return instance of {@link FactOfTheDayVO}
     * @author Mykola Lehkyi
     */
    FactOfTheDayVO update(FactOfTheDayPostDTO fact);

    /**
     * Method deletes {@link FactOfTheDayVO} and {@link FactOfTheDayTranslationVO}
     * by id.
     *
     * @param id Long
     * @return id of deleted {@link FactOfTheDayVO}
     * @author Mykola Lehkyi
     */
    Long deleteFactOfTheDayAndTranslations(Long id);

    /**
     * Method deletes all {@link FactOfTheDayVO} {@link FactOfTheDayVO} and
     * {@link FactOfTheDayTranslationVO} by list of IDs.
     *
     * @param listId list of id {@link FactOfTheDayVO}
     * @return listId list of id {@link FactOfTheDayVO}
     * @author Mykola Lehkyi
     */
    List<Long> deleteAllFactOfTheDayAndTranslations(List<Long> listId);

    /**
     * Method returns pageable of {@link FactOfTheDayDTO} that satisfy search query.
     *
     * @param searchQuery query to search
     * @return pageable of {@link FactOfTheDayDTO}
     */
    PageableDto<FactOfTheDayDTO> searchBy(Pageable pageable, String searchQuery);

    /**
     * Method return random {@link FactOfTheDayVO} .
     *
     * @return FactOfTheDay
     * @author Mykola Lehkyi
     */
    FactOfTheDayVO getRandomFactOfTheDay();

    /**
     * Method return random {@link FactOfTheDayTranslationDTO} by languageCode.
     *
     * @param languageCode String
     * @return {@link FactOfTheDayTranslationDTO}
     * @author Mykola Lehkyi
     */
    FactOfTheDayTranslationDTO getRandomFactOfTheDayByLanguage(String languageCode);
}
