package greencity.service;

import greencity.constant.LogMessage;
import greencity.dto.breaktime.BreakTimeVO;
import greencity.entity.BreakTime;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.BreakTimeRepo;
import greencity.constant.ErrorMessage;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
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
    public BreakTimeVO save(BreakTimeVO breakTime) {
        log.info(LogMessage.IN_SAVE, breakTime);

        if (breakTime.getEndTime().getHour() < breakTime.getStartTime().getHour()) {
            throw new BadRequestException(ErrorMessage.END_TIME_LATE_THAN_START_TIME);
        }

        BreakTime save = repo.save(modelMapper.map(breakTime, BreakTime.class));
        return modelMapper.map(save, BreakTimeVO.class);
    }

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Override
    public BreakTimeVO findById(Long id) {
        log.info(LogMessage.IN_FIND_BY_ID, id);

        BreakTime breakTime = repo
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException(ErrorMessage.BREAK_TIME_NOT_FOUND_BY_ID + id));
        return modelMapper.map(breakTime, BreakTimeVO.class);
    }

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Override
    public Long deleteById(Long id) {
        log.info(LogMessage.IN_DELETE_BY_ID, id);
        repo.delete(modelMapper.map(findById(id), BreakTime.class));
        return id;
    }

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Override
    public List<BreakTimeVO> findAll() {
        log.info(LogMessage.IN_FIND_ALL);

        return modelMapper.map(repo.findAll(), new TypeToken<List<BreakTimeVO>>() {
        }.getType());
    }
}
