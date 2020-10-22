package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.constant.LogMessage;
import greencity.dto.openhours.OpeningHoursVO;
import greencity.dto.place.PlaceVO;
import greencity.entity.OpeningHours;
import greencity.entity.Place;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.OpenHoursRepo;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

/**
 * The class provides implementation of the {@code OpenHoursService}.
 */
@Slf4j
@AllArgsConstructor
@Service
public class OpenHoursServiceImpl implements OpenHoursService {
    /**
     * Autowired repository.
     */
    private final OpenHoursRepo hoursRepo;
    private final BreakTimeService breakTimeService;
    private final ModelMapper modelMapper;

    /**
     * {@inheritDoc}
     *
     * @author Roman Zahorui
     */
    @Override
    public List<OpeningHoursVO> getOpenHoursByPlace(PlaceVO place) {
        List<OpeningHours> openingHours = hoursRepo.findAllByPlace(modelMapper.map(place, Place.class));
        return modelMapper.map(openingHours, new TypeToken<List<OpeningHoursVO>>() {
        }.getType());
    }

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Override
    public OpeningHoursVO save(OpeningHoursVO hours) {
        log.info(LogMessage.IN_SAVE);

        if (hours.getOpenTime().getHour() > hours.getCloseTime().getHour()) {
            throw new BadRequestException(ErrorMessage.CLOSE_TIME_LATE_THAN_OPEN_TIME);
        }

        if (hours.getBreakTime() != null) {
            if (hours.getBreakTime().getStartTime().getHour() > hours.getOpenTime().getHour()
                && hours.getBreakTime().getEndTime().getHour() < hours.getCloseTime().getHour()) {
                breakTimeService.save(hours.getBreakTime());
            } else {
                throw new BadRequestException(ErrorMessage.WRONG_BREAK_TIME);
            }
        }
        OpeningHours save = hoursRepo.save(modelMapper.map(hours, OpeningHours.class));
        return modelMapper.map(save, OpeningHoursVO.class);
    }

    /**
     * {@inheritDoc}
     *
     * @author Nazar Vladyka
     */
    @Override
    public List<OpeningHoursVO> findAll() {
        log.info(LogMessage.IN_FIND_ALL);

        return modelMapper.map(hoursRepo.findAll(), new TypeToken<List<OpeningHoursVO>>() {
        }.getType());
    }

    /**
     * {@inheritDoc}
     *
     * @author Nazar Vladyka
     */
    @Override
    public OpeningHoursVO findById(Long id) {
        log.info(LogMessage.IN_FIND_BY_ID, id);

        OpeningHours openingHours = hoursRepo
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException(ErrorMessage.OPEN_HOURS_NOT_FOUND_BY_ID + id));
        return modelMapper.map(openingHours, OpeningHoursVO.class);
    }

    /**
     * {@inheritDoc}
     *
     * @author Nazar Vladyka
     */
    @Override
    public OpeningHoursVO update(Long id, OpeningHoursVO updatedHours) {
        log.info(LogMessage.IN_UPDATE);

        OpeningHoursVO updatable = findById(id);

        updatable.setOpenTime(updatedHours.getOpenTime());
        updatable.setCloseTime(updatedHours.getCloseTime());
        updatable.setWeekDay(updatedHours.getWeekDay());
        updatable.setPlace(updatedHours.getPlace());

        OpeningHours save = hoursRepo.save(modelMapper.map(updatable, OpeningHours.class));
        return modelMapper.map(save, OpeningHoursVO.class);
    }

    /**
     * {@inheritDoc}
     *
     * @author Nazar Vladyka
     */
    @Override
    public Long deleteById(Long id) {
        log.info(LogMessage.IN_DELETE_BY_ID, id);

        hoursRepo.delete(modelMapper.map(findById(id), OpeningHours.class));
        return id;
    }

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Override
    public Set<OpeningHoursVO> findAllByPlaceId(Long placeId) {
        return modelMapper.map(hoursRepo.findAllByPlaceId(placeId), new TypeToken<Set<OpeningHoursVO>>() {
        }.getType());
    }

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Override
    public void deleteAllByPlaceId(Long placeId) {
        hoursRepo.deleteAllByPlaceId(placeId);
    }
}
