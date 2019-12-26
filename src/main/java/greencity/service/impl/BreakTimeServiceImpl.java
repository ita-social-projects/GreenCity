package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.constant.LogMessage;
import greencity.entity.BreakTime;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.BreakTimeRepo;
import greencity.service.BreakTimeService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

/**
 * Service implementation for BreakTime entity.
 *
 * @version 1.0
 */
@Service
@AllArgsConstructor
@Slf4j
public class BreakTimeServiceImpl implements BreakTimeService {
    private BreakTimeRepo repo;
    private ModelMapper modelMapper;

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Override
    public BreakTime save(BreakTime breakTime) {
        log.info(LogMessage.IN_SAVE, breakTime);

        if (breakTime.getEndTime().getHour() < breakTime.getStartTime().getHour()) {
            throw new BadRequestException(ErrorMessage.END_TIME_LATE_THAN_START_TIME);
        }

        return repo.save(breakTime);
    }

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Override
    public BreakTime findById(Long id) {
        log.info(LogMessage.IN_FIND_BY_ID, id);

        return repo
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException(ErrorMessage.BREAK_TIME_NOT_FOUND_BY_ID + id));
    }

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Override
    public Long deleteById(Long id) {
        log.info(LogMessage.IN_DELETE_BY_ID, id);

        repo.delete(findById(id));
        return id;
    }

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Override
    public List<BreakTime> findAll() {
        log.info(LogMessage.IN_FIND_ALL);

        return repo.findAll();
    }
}
