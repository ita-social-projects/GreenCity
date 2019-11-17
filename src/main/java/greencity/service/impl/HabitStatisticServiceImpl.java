package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.dto.habitstatistic.HabitStatisticDto;
import greencity.dto.habitstatistic.HabitStatisticForUpdateDto;
import greencity.entity.HabitStatistic;
import greencity.exception.BadRequestException;
import greencity.exception.NotFoundException;
import greencity.mapping.HabitStatisticMapper;
import greencity.repository.HabitStatisticRepo;
import greencity.service.HabitStatisticService;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class HabitStatisticServiceImpl implements HabitStatisticService {
    private HabitStatisticRepo habitStatisticRepo;
    private HabitStatisticMapper habitStatisticMapper;
    private ModelMapper modelMapper;

    @Override
    /**
     * {@inheritDoc}
     *
     * @author Yuriy Olkhovskyi
     */
    public HabitStatisticDto save(HabitStatisticDto dto) {
        if (LocalDate.now().compareTo(dto.getDate()) == 0
            || LocalDate.now().compareTo(dto.getDate()) == 1) {
            HabitStatistic habitStatistic = habitStatisticMapper.convertToEntity(dto);

            return habitStatisticMapper.convertToDto(habitStatisticRepo.save(habitStatistic));
        } else {
            throw new BadRequestException(ErrorMessage.WRONG_DATE);
        }
    }

    @Override
    /**
     * {@inheritDoc}
     *
     * @author Yuriy Olkhovskyi
     */
    public HabitStatisticForUpdateDto update(HabitStatisticForUpdateDto dto) {
        HabitStatistic updatable = findById(dto.getHabitStatisticId());

        updatable.setAmountOfItems(dto.getAmountOfItems());
        updatable.setHabitRate(dto.getHabitRate());

        return modelMapper.map(habitStatisticRepo.save(updatable),
            HabitStatisticForUpdateDto.class);
    }

    /**
     * {@inheritDoc}
     *
     * @author Yuriy Olkhovskyi
     */
    @Override
    public HabitStatistic findById(Long id) {
        return habitStatisticRepo
            .findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage
                .HABIT_STATISTIC_NOT_FOUND_BY_ID + id));
    }
}