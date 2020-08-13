package greencity.service;

import greencity.entity.FactOfTheDay;
import java.util.List;

public interface FactOfTheDayService {
    /**
     * Method finds all {@link FactOfTheDay}.
     *
     * @return List of all {@link FactOfTheDay}
     * @author Mykola Lehkyi
     */
    List<FactOfTheDay> getAllFactsOfTheDay();

    /**
     * Method find {@link FactOfTheDay} by id.
     *
     * @param id of {@link FactOfTheDay}
     * @return {@link FactOfTheDay}
     * @author Mykola Lehkyi
     */
    FactOfTheDay getFactOfTheDayById(Long id);

    /**
     * Method find {@link FactOfTheDay} by fact.
     *
     * @param title of {@link FactOfTheDay}
     * @return {@link FactOfTheDay}
     * @author Mykola Lehkyi
     */
    List<FactOfTheDay> getAllFactOfTheDayByTitle(String title);

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
