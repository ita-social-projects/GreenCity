package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.entity.FactOfTheDay;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotUpdatedException;
import greencity.repository.FactOfTheDayRepo;
import greencity.service.FactOfTheDayService;
import java.util.List;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link FactOfTheDayService}.
 *
 * @author Mykola Lehkyi
 */
@Service
@EnableCaching
public class FactOfTheDayServiceImpl implements FactOfTheDayService {
    private FactOfTheDayRepo factOfTheDayRepo;

    /**
     * Constructor with parameters.
     *
     * @author Mykola Lehkyi
     */
    public FactOfTheDayServiceImpl(FactOfTheDayRepo factOfTheDayRepo) {
        this.factOfTheDayRepo = factOfTheDayRepo;
    }

    /**
     * Method finds all {@link FactOfTheDay}.
     *
     * @return List of all {@link FactOfTheDay}
     * @author Mykola Lehkyi
     */
    @Override
    public List<FactOfTheDay> getAllFactsOfTheDay() {
        return factOfTheDayRepo.findAll();
    }

    /**
     * Method find {@link FactOfTheDay} by id.
     *
     * @param id of {@link FactOfTheDay}
     * @return {@link FactOfTheDay}
     * @author Mykola Lehkyi
     */
    @Override
    public FactOfTheDay getFactOfTheDayById(Long id) {
        return factOfTheDayRepo.findById(id).orElseThrow(() ->
            new NotFoundException(ErrorMessage.FACT_OF_THE_DAY_NOT_FOUND));
    }

    /**
     * Method find {@link FactOfTheDay} by title.
     *
     * @param name of {@link FactOfTheDay}
     * @return {@link FactOfTheDay}
     * @author Mykola Lehkyi
     */
    @Override
    public List<FactOfTheDay> getAllFactOfTheDayByName(String name) {
        return factOfTheDayRepo.findAllByName(name);
    }

    /**
     * Method saves new {@link FactOfTheDay}.
     *
     * @param fact {@link FactOfTheDay}
     * @return instance of {@link FactOfTheDay}
     * @author Mykola Lehkyi
     */
    @Override
    public FactOfTheDay save(FactOfTheDay fact) {
        return factOfTheDayRepo.save(fact);
    }

    /**
     * Method updates {@link FactOfTheDay}.
     *
     * @param fact {@link FactOfTheDay} Object
     * @return instance of {@link FactOfTheDay}
     * @author Mykola Lehkyi
     */
    @Override
    public FactOfTheDay update(FactOfTheDay fact) {
        FactOfTheDay factOfTheDay = factOfTheDayRepo.findById(fact.getId()).orElseThrow(() -> new NotUpdatedException(
            ErrorMessage.FACT_OF_THE_DAY_NOT_UPDATED));
        return factOfTheDayRepo.save(factOfTheDay);
    }

    /**
     * Method deletes {@link FactOfTheDay} by id.
     *
     * @param id Long of {@link FactOfTheDay}
     * @return id of deleted element
     * @author Mykola Lehkyi
     */
    @Override
    public Long delete(Long id) {
        if (!(factOfTheDayRepo.findById(id).isPresent())) {
            throw new NotDeletedException(ErrorMessage.FACT_OF_THE_DAY_NOT_DELETED);
        }
        factOfTheDayRepo.deleteById(id);
        return id;
    }
}
