package greencity.service;

import greencity.dto.breaktime.BreakTimeVO;
import java.util.List;

/**
 * Provides the interface to manage {@code BreakTime} entity.
 */
public interface BreakTimeService {
    /**
     * Save BreakTime to DB.
     *
     * @param breakTime - entity of BreakTime.
     * @return saved BreakTime.
     */
    BreakTimeVO save(BreakTimeVO breakTime);

    /**
     * Find BreakTime entity by id.
     *
     * @param id - BreakTime id.
     * @return BreakTime entity.
     */
    BreakTimeVO findById(Long id);

    /**
     * Delete entity from DB by id.
     *
     * @param id - BreakTime id.
     * @return id of deleted BreakTime.
     */
    Long deleteById(Long id);

    /**
     * Find all BreakTimes from DB.
     *
     * @return List of BreakTimes.
     */
    List<BreakTimeVO> findAll();
}
