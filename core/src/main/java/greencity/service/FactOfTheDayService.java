package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.factoftheday.FactOfTheDayDTO;
import greencity.entity.FactOfTheDay;
import java.util.List;
import org.springframework.data.domain.Page;
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
    FactOfTheDay getFactOfTheDayById(Long id);

    /**
     * Method find {@link FactOfTheDay} by name.
     *
     * @param name of {@link FactOfTheDay}
     * @return {@link FactOfTheDay}
     * @author Mykola Lehkyi
     */
    List<FactOfTheDay> getAllFactOfTheDayByName(String name);

    /**
     * Method saves new {@link FactOfTheDay}.
     *
     * @param fact {@link FactOfTheDay}
     * @return instance of {@link FactOfTheDay}
     * @author Mykola Lehkyi
     */
    FactOfTheDay save(FactOfTheDay fact);

    /**
     * Method updates {@link FactOfTheDay}.
     *
     * @param fact {@link FactOfTheDay}
     * @return instance of {@link FactOfTheDay}
     * @author Mykola Lehkyi
     */
    FactOfTheDay update(FactOfTheDay fact);

    /**
     * Method delete {@link FactOfTheDay} by id.
     *
     * @param id Long
     * @return id of deleted {@link FactOfTheDay}
     * @author Mykola Lehkyi
     */
    Long delete(Long id);
}
