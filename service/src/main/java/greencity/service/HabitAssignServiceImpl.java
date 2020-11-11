package greencity.service;

import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.dto.habit.*;
import greencity.dto.user.UserVO;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.User;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserAlreadyHasHabitAssignedException;
import greencity.exception.exceptions.UserAlreadyHasMaxNumberOfActiveHabitAssigns;
import greencity.repository.HabitAssignRepo;
import greencity.repository.HabitRepo;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link HabitAssignService}.
 */
@Service
@AllArgsConstructor
public class HabitAssignServiceImpl implements HabitAssignService {
    private final HabitAssignRepo habitAssignRepo;
    private final HabitRepo habitRepo;
    private final HabitService habitService;
    private final HabitStatisticService habitStatisticService;
    private final HabitStatusService habitStatusService;
    private final ModelMapper modelMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public HabitAssignDto getById(Long habitAssignId, String language) {
        HabitAssign habitAssign = habitAssignRepo.findById(habitAssignId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_ASSIGN_NOT_FOUND_BY_ID + habitAssignId));

        HabitDto habitDto = habitService.getByIdAndLanguageCode(habitAssign.getHabit().getId(), language);
        HabitAssignDto habitAssignDto = modelMapper.map(habitAssign, HabitAssignDto.class);
        habitAssignDto.setHabit(habitDto);
        return habitAssignDto;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public HabitAssignManagementDto assignDefaultHabitForUser(Long habitId, UserVO userVO) {
        User user = modelMapper.map(userVO, User.class);
        Habit habit = validateHabitForAssign(habitId, user);

        HabitAssign habitAssign = buildHabitAssign(habit, user);
        enhanceAssignWithDefaultProperties(habitAssign);

        habitStatusService.saveStatusByHabitAssign(modelMapper.map(habitAssign, HabitAssignVO.class));
        return modelMapper.map(habitAssign, HabitAssignManagementDto.class);
    }

    /**
     * Method updates {@link HabitAssign} with default properties.
     *
     * @param habitAssign {@link HabitAssign} instance.
     */
    private void enhanceAssignWithDefaultProperties(HabitAssign habitAssign) {
        habitAssign.setDuration(AppConstant.DEFAULT_DAYS_DURATION_OF_HABIT_ASSIGN_FOR_USER);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public HabitAssignManagementDto assignCustomHabitForUser(Long habitId, UserVO userVO,
        HabitAssignPropertiesDto habitAssignPropertiesDto) {
        User user = modelMapper.map(userVO, User.class);
        Habit habit = validateHabitForAssign(habitId, user);

        HabitAssign habitAssign = buildHabitAssign(habit, user);
        enhanceAssignWithCustomProperties(habitAssign, habitAssignPropertiesDto);

        habitStatusService.saveStatusByHabitAssign(modelMapper.map(habitAssign, HabitAssignVO.class));
        return modelMapper.map(habitAssign, HabitAssignManagementDto.class);
    }

    /**
     * Method updates {@link HabitAssign} with custom properties from
     * {@link HabitAssignPropertiesDto} instance.
     *
     * @param habitAssign {@link HabitAssign} instance.
     * @param props       {@link HabitAssignPropertiesDto} instance.
     */
    private void enhanceAssignWithCustomProperties(HabitAssign habitAssign,
        HabitAssignPropertiesDto props) {
        habitAssign.setDuration(props.getDuration());
    }

    /**
     * Method builds {@link HabitAssign} with main props.
     *
     * @param habit {@link Habit} instance.
     * @param user  {@link User} instance.
     * @return {@link HabitAssign} instance.
     */
    private HabitAssign buildHabitAssign(Habit habit, User user) {
        return habitAssignRepo.save(
            HabitAssign.builder()
                .habit(habit)
                .acquired(false)
                .createDate(ZonedDateTime.now())
                .suspended(false)
                .user(user)
                .duration(0)
                .build());
    }

    /**
     * Method validates new {@link HabitAssign} to be created for current user.
     *
     * @param habitId {@link Habit} id.
     * @param user    {@link User} instance.
     * @return {@link Habit} instance.
     */
    private Habit validateHabitForAssign(Long habitId, User user) {
        Habit habit = habitRepo.findById(habitId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_NOT_FOUND_BY_ID + habitId));

        if (habitAssignRepo.findByHabitIdAndUserIdAndSuspendedFalse(habit.getId(), user.getId()).isPresent()) {
            throw new UserAlreadyHasHabitAssignedException(
                ErrorMessage.USER_ALREADY_HAS_ASSIGNED_HABIT + habit.getId());
        }
        if (habitAssignRepo.countHabitAssignsByUserIdAndSuspendedFalseAndAcquiredFalse(
            user.getId()) >= AppConstant.MAX_NUMBER_OF_HABIT_ASSIGNS_FOR_USER) {
            throw new UserAlreadyHasMaxNumberOfActiveHabitAssigns(
                ErrorMessage.USER_ALREADY_HAS_MAX_NUMBER_OF_HABIT_ASSIGNS
                    + AppConstant.MAX_NUMBER_OF_HABIT_ASSIGNS_FOR_USER);
        }
        if (habitAssignRepo.findByHabitIdAndUserIdAndCreateDate(
            habit.getId(), user.getId(), ZonedDateTime.now()).isPresent()) {
            throw new UserAlreadyHasHabitAssignedException(
                ErrorMessage.USER_SUSPENDED_ASSIGNED_HABIT_FOR_CURRENT_DAY_ALREADY + habit.getId());
        }
        return habit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HabitAssignDto findActiveHabitAssignByUserIdAndHabitId(Long userId, Long habitId, String language) {
        HabitDto habitDto = habitService.getByIdAndLanguageCode(habitId, language);
        HabitAssignDto habitAssignDto =
            modelMapper.map(habitAssignRepo.findByHabitIdAndUserIdAndSuspendedFalse(habitId, userId)
                .orElseThrow(() -> new NotFoundException(
                    ErrorMessage.HABIT_ASSIGN_NOT_FOUND_WITH_SUCH_USER_ID_AND_HABIT_ID + userId
                        + ", " + habitId)),
                HabitAssignDto.class);
        habitAssignDto.setHabit(habitDto);
        return habitAssignDto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<HabitAssignDto> getAllHabitAssignsByUserIdAndAcquiredStatus(Long userId, Boolean acquired,
        String language) {
        return habitAssignRepo.findAllByUserIdAndAcquiredAndSuspendedFalse(userId, acquired)
            .stream().map(habitAssign -> {
                HabitAssignDto habitAssignDto = modelMapper.map(habitAssign, HabitAssignDto.class);
                HabitDto habitDto = habitService.getByIdAndLanguageCode(habitAssign.getHabit().getId(), language);
                habitAssignDto.setHabit(habitDto);
                return habitAssignDto;
            }).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<HabitAssignDto> getAllHabitAssignsByHabitIdAndAcquiredStatus(Long habitId, Boolean acquired,
        String language) {
        HabitDto habitDto = habitService.getByIdAndLanguageCode(habitId, language);
        return habitAssignRepo.findAllByHabitIdAndAcquiredAndSuspendedFalse(habitId, acquired)
            .stream().map(habitAssign -> {
                HabitAssignDto habitAssignDto = modelMapper.map(habitAssign, HabitAssignDto.class);
                habitAssignDto.setHabit(habitDto);
                return habitAssignDto;
            }).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public HabitAssignManagementDto updateStatusByHabitIdAndUserId(Long habitId, Long userId,
        HabitAssignStatDto dto) {
        HabitAssign updatable = habitAssignRepo.findByHabitIdAndUserIdAndSuspendedFalse(habitId, userId)
            .orElseThrow(() -> new NotFoundException(
                ErrorMessage.HABIT_ASSIGN_NOT_FOUND_WITH_SUCH_USER_ID_AND_HABIT_ID + habitId));

        enhanceStatusesWithDto(dto, updatable);

        return modelMapper.map(habitAssignRepo.save(updatable), HabitAssignManagementDto.class);
    }

    /**
     * Method updates {@link HabitAssign} with {@link HabitAssignStatDto} fields.
     *
     * @param dto       {@link HabitAssignStatDto} instance.
     * @param updatable {@link HabitAssign} instance.
     */
    private void enhanceStatusesWithDto(HabitAssignStatDto dto, HabitAssign updatable) {
        updatable.setAcquired(dto.getAcquired());
        updatable.setSuspended(dto.getSuspended());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void deleteAllHabitAssignsByHabit(HabitVO habit) {
        habitAssignRepo.findAllByHabitId(habit.getId())
            .forEach(habitAssign -> {
                HabitAssignVO habitAssignVO = modelMapper.map(habitAssign, HabitAssignVO.class);
                habitStatusService.deleteStatusByHabitAssign(habitAssignVO);
                habitStatisticService.deleteAllStatsByHabitAssign(habitAssignVO);
                habitAssignRepo.delete(habitAssign);
            });
    }
}
