package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habit.HabitAssignStatDto;
import greencity.dto.user.UserVO;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.User;
import greencity.exception.exceptions.NotUpdatedException;
import greencity.exception.exceptions.UserAlreadyHasHabitAssignedException;
import greencity.exception.exceptions.WrongIdException;
import greencity.repository.HabitAssignRepo;
import greencity.repository.HabitRepo;
import greencity.service.HabitAssignService;
import greencity.service.HabitService;
import greencity.service.HabitStatisticService;
import greencity.service.HabitStatusService;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link HabitAssignService}.
 */
@Service
@AllArgsConstructor
public class HabitAssignServiceImpl implements HabitAssignService {
    private final HabitRepo habitRepo;
    private final HabitAssignRepo habitAssignRepo;
    private final HabitStatisticService habitStatisticService;
    private final HabitStatusService habitStatusService;
    private final ModelMapper modelMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public HabitAssignDto getById(Long habitAssignId) {
        return modelMapper.map(habitAssignRepo.findById(habitAssignId)
            .orElseThrow(() -> new WrongIdException(ErrorMessage.HABIT_ASSIGN_NOT_FOUND_BY_ID + habitAssignId)),
            HabitAssignDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public HabitAssignDto assignHabitForUser(Long habitId, User user) {
        Habit habit = modelMapper.map(habitRepo.findById(habitId)
            .orElseThrow(() -> new WrongIdException(ErrorMessage.HABIT_NOT_FOUND_BY_ID))
        ,Habit.class);
        User user = modelMapper.map(userVO, User.class);
        //Habit habit = modelMapper.map(habitService.getById(habitId), Habit.class);

        if (habitAssignRepo.findByHabitIdAndUserIdAndSuspendedFalse(habitId, user.getId()).isPresent()) {
            throw new UserAlreadyHasHabitAssignedException(
                ErrorMessage.USER_ALREADY_HAS_ASSIGNED_HABIT + habitId);
        }
        if (habitAssignRepo.findByHabitIdAndUserIdAndCreateDate(
            habitId, user.getId(), ZonedDateTime.now()).isPresent()) {
            throw new UserAlreadyHasHabitAssignedException(
                ErrorMessage.USER_SUSPENDED_ASSIGNED_HABIT_FOR_CURRENT_DAY_ALREADY + habitId);
        } else {
            HabitAssign habitAssign = habitAssignRepo.save(
                HabitAssign.builder()
                    .habit(habit)
                    .acquired(false)
                    .createDate(ZonedDateTime.now())
                    .suspended(false)
                    .user(user)
                    .build());

            habitStatusService.saveStatusByHabitAssign(modelMapper.map(habitAssign, HabitAssignDto.class));
            return modelMapper.map(habitAssign, HabitAssignDto.class);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HabitAssignDto findActiveHabitAssignByUserIdAndHabitId(Long userId, Long habitId) {
        return modelMapper.map(habitAssignRepo.findByHabitIdAndUserIdAndSuspendedFalse(habitId, userId)
                .orElseThrow(() ->
                    new WrongIdException(ErrorMessage.HABIT_ASSIGN_NOT_FOUND_WITH_SUCH_USER_ID_AND_HABIT_ID)),
            HabitAssignDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<HabitAssignDto> getAllHabitAssignsByUserIdAndAcquiredStatus(Long userId, Boolean acquired) {
        return modelMapper.map(habitAssignRepo.findAllByUserIdAndAcquiredAndSuspendedFalse(userId, acquired),
            new TypeToken<List<HabitAssignDto>>() {
            }.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public HabitAssignDto updateStatus(Long habitAssignId, HabitAssignStatDto dto) {
        HabitAssign updatable = habitAssignRepo.findById(habitAssignId)
            .orElseThrow(() -> new NotUpdatedException(ErrorMessage.HABIT_ASSIGN_NOT_UPDATED_BY_ID));

        updatable.setAcquired(dto.getAcquired());
        updatable.setSuspended(dto.getSuspended());

        return modelMapper.map(habitAssignRepo.save(updatable), HabitAssignDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void deleteAllHabitAssignsByHabit(Habit habit) {
        habitAssignRepo.findAllByHabitId(habit.getId())
            .forEach(habitAssign -> {
                habitStatusService.deleteStatusByHabitAssign(habitAssign);
                habitStatisticService.deleteAllStatsByHabitAssign(habitAssign);
                habitAssignRepo.delete(habitAssign);
            });
    }
}
