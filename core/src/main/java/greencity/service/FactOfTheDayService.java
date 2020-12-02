package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.factoftheday.FactOfTheDayDTO;
import greencity.dto.factoftheday.FactOfTheDayPostDTO;
import greencity.entity.FactOfTheDay;
import greencity.entity.FactOfTheDayTranslation;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface FactOfTheDayService {
    /**
     * Method finds all {@link FactOfTheDay} with pageable configuration.
     *
     * @param pageable {@link Pageable}
     * @return {@link PageableDto} with list of all {@link FactOfTheDayDTO}
     * @author Mykola Lehkyi
     */
    PageableDto<FactOfTheDayDTO> getAllFactsOfTheDay(Pageable pageable);

    /**
     * Method find {@link FactOfTheDay} by id.
     *
     * @param id of {@link FactOfTheDay}
     * @return {@link FactOfTheDay}
     * @author Mykola Lehkyi
     */
    FactOfTheDayDTO getFactOfTheDayById(Long id);

    /**
     * Method find {@link FactOfTheDay} by name.
     *
     * @param name of {@link FactOfTheDay}
     * @return {@link FactOfTheDay}
     * @author Mykola Lehkyi
     */
    List<FactOfTheDay> getAllFactOfTheDayByName(String name);

    /**
     * Method saves new {@link FactOfTheDay} and {@link FactOfTheDayTranslation}.
     *
     * @param fact {@link FactOfTheDayPostDTO}
     * @return instance of {@link FactOfTheDayPostDTO}
     * @author Mykola Lehkyi
     */
    FactOfTheDayPostDTO saveFactOfTheDayAndTranslations(FactOfTheDayPostDTO fact);

    /**
     * Method saves new {@link FactOfTheDay} and {@link FactOfTheDayTranslation}.
     *
     * @param fact {@link FactOfTheDayPostDTO}
     * @return instance of {@link FactOfTheDayPostDTO}
     * @author Mykola Lehkyi
     */
    FactOfTheDayPostDTO updateFactOfTheDayAndTranslations(FactOfTheDayPostDTO fact);

    /**
     * Method updates {@link FactOfTheDay}{@link FactOfTheDay} and {@link FactOfTheDayTranslation}.
     *
     * @param fact {@link FactOfTheDay}
     * @return instance of {@link FactOfTheDay}
     * @author Mykola Lehkyi
     */
    FactOfTheDay update(FactOfTheDayPostDTO fact);

    /**
     * Method deletes {@link FactOfTheDay} and {@link FactOfTheDayTranslation} by id.
     *
     * @param id Long
     * @return id of deleted {@link FactOfTheDay}
     * @author Mykola Lehkyi
     */
    Long deleteFactOfTheDayAndTranslations(Long id);

    /**
     * Method deletes all {@link FactOfTheDay} {@link FactOfTheDay} and {@link FactOfTheDayTranslation} by list of IDs.
     *
     * @param listId list of id {@link FactOfTheDay}
     * @return listId list of id {@link FactOfTheDay}
     * @author Mykola Lehkyi
     */
    List<Long> deleteAllFactOfTheDayAndTranslations(List<Long> listId);
}
