package greencity.service;

import greencity.dto.advice.AdviceAdminDTO;
import greencity.dto.advice.AdvicePostDTO;
import greencity.entity.Advice;
import java.util.List;

/**
 * AdviceService interface.
 *
 * @author Vitaliy Dzen
 */
public interface AdviceService {
    /**
     * Method finds all {@link Advice}.
     *
     * @return List of all {@link Advice}
     * @author Vitaliy Dzen
     */
    List getAllAdvices();

    /**
     * Method finds random {@link Advice}.
     *
     * @return random {@link Advice}
     * @author Vitaliy Dzen
     */
    AdviceAdminDTO getRandomAdviceByHabitId(Long id);

    /**
     * Method find {@link Advice} by id.
     *
     * @param id of {@link Advice}
     * @return {@link AdviceAdminDTO}
     * @author Vitaliy Dzen
     */
    AdviceAdminDTO getAdviceById(Long id);

    /**
     * Method find {@link Advice} by advice.
     *
     * @param name of {@link Advice}
     * @return {@link AdviceAdminDTO}
     * @author Vitaliy Dzen
     */
    AdviceAdminDTO getAdviceByName(String name);

    /**
     * Method saves new {@link Advice}.
     *
     * @param advice {@link AdviceAdminDTO}
     * @return instance of {@link Advice}
     * @author Vitaliy Dzen
     */
    Advice save(AdvicePostDTO advice);

    /**
     * Method updates {@link Advice}.
     *
     * @param advice {@link AdviceAdminDTO}
     * @return instance of {@link Advice}
     * @author Vitaliy Dzen
     */
    Advice update(AdvicePostDTO advice, Long id);

    /**
     * Method delete {@link Advice} by id.
     *
     * @param id Long
     * @return id of deleted {@link Advice}
     * @author Vitaliy Dzen
     */
    Long delete(Long id);
}
