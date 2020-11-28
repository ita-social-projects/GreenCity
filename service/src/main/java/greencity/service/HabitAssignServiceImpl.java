package greencity.service;

import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.dto.habit.*;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarDto;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarVO;
import greencity.dto.user.UserVO;
import greencity.entity.*;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserAlreadyHasHabitAssignedException;
import greencity.exception.exceptions.UserAlreadyHasMaxNumberOfActiveHabitAssigns;
import greencity.repository.HabitAssignRepo;
import greencity.repository.HabitRepo;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.Collections;
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
    private final HabitStatisticService habitStatisticService;
    private final HabitStatusCalendarService habitStatusCalendarService;
    private final ModelMapper modelMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public HabitAssignDto getById(Long habitAssignId, String language) {
        HabitAssign habitAssign = habitAssignRepo.findById(habitAssignId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_ASSIGN_NOT_FOUND_BY_ID + habitAssignId));
        return buildHabitAssignDto(habitAssign, language);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public HabitAssignManagementDto assignDefaultHabitForUser(Long habitId, UserVO userVO) {
        User user = modelMapper.map(userVO, User.class);

        Habit habit = habitRepo.findById(habitId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_NOT_FOUND_BY_ID + habitId));
        validateHabitForAssign(habitId, user);

        HabitAssign habitAssign = buildHabitAssign(habit, user);
        enhanceAssignWithDefaultProperties(habitAssign);

        return modelMapper.map(habitAssign, HabitAssignManagementDto.class);
    }

    /**
     * Method updates {@link HabitAssign} with default properties.
     *
     * @param habitAssign {@link HabitAssign} instance.
     */
    private void enhanceAssignWithDefaultProperties(HabitAssign habitAssign) {
        habitAssign.setDuration(habitAssign.getHabit().getDefaultDuration());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public HabitAssignManagementDto assignCustomHabitForUser(Long habitId, UserVO userVO,
        HabitAssignPropertiesDto habitAssignPropertiesDto) {
        User user = modelMapper.map(userVO, User.class);

        Habit habit = habitRepo.findById(habitId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_NOT_FOUND_BY_ID + habitId));
        validateHabitForAssign(habitId, user);

        HabitAssign habitAssign = buildHabitAssign(habit, user);
        enhanceAssignWithCustomProperties(habitAssign, habitAssignPropertiesDto);

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
                .habitStreak(0)
                .workingDays(0)
                .lastEnrollmentDate(ZonedDateTime.now())
                .build());
    }

    /**
     * Method builds {@link HabitAssignDto} with one habit translation.
     *
     * @param habitAssign {@link HabitAssign} instance.
     * @param language    code of {@link Language}.
     * @return {@link HabitAssign} instance.
     */
    private HabitAssignDto buildHabitAssignDto(HabitAssign habitAssign, String language) {
        HabitTranslation habitTranslation = habitAssign.getHabit().getHabitTranslations().stream()
            .filter(ht -> ht.getLanguage().getCode().equals(language)).findFirst()
            .orElseThrow(() -> new NotFoundException(
                ErrorMessage.HABIT_TRANSLATION_NOT_FOUND + habitAssign.getHabit().getId()));

        HabitAssignDto habitAssignDto = modelMapper.map(habitAssign, HabitAssignDto.class);
        habitAssignDto.setHabit(modelMapper.map(habitTranslation, HabitDto.class));
        return habitAssignDto;
    }

    /**
     * Method validates new {@link HabitAssign} to be created for current user.
     *
     * @param habitId {@link Habit} id.
     * @param user    {@link User} instance.
     */
    private void validateHabitForAssign(Long habitId, User user) {
        if (habitAssignRepo.findByHabitIdAndUserIdAndSuspendedFalse(habitId, user.getId()).isPresent()) {
            throw new UserAlreadyHasHabitAssignedException(
                ErrorMessage.USER_ALREADY_HAS_ASSIGNED_HABIT + habitId);
        }
        if (habitAssignRepo.countHabitAssignsByUserIdAndSuspendedFalseAndAcquiredFalse(
            user.getId()) >= AppConstant.MAX_NUMBER_OF_HABIT_ASSIGNS_FOR_USER) {
            throw new UserAlreadyHasMaxNumberOfActiveHabitAssigns(
                ErrorMessage.USER_ALREADY_HAS_MAX_NUMBER_OF_HABIT_ASSIGNS
                    + AppConstant.MAX_NUMBER_OF_HABIT_ASSIGNS_FOR_USER);
        }
        if (habitAssignRepo.findByHabitIdAndUserIdAndCreateDate(
            habitId, user.getId(), ZonedDateTime.now()).isPresent()) {
            throw new UserAlreadyHasHabitAssignedException(
                ErrorMessage.USER_SUSPENDED_ASSIGNED_HABIT_FOR_CURRENT_DAY_ALREADY + habitId);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HabitAssignDto findActiveHabitAssignByUserIdAndHabitId(Long userId, Long habitId, String language) {
        HabitAssign habitAssign =
            habitAssignRepo.findByHabitIdAndUserIdAndSuspendedFalse(habitId, userId)
                .orElseThrow(
                    () -> new NotFoundException(ErrorMessage.HABIT_ASSIGN_NOT_FOUND_WITH_CURRENT_USER_ID_AND_HABIT_ID
                        + habitId));
        return buildHabitAssignDto(habitAssign, language);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<HabitAssignDto> getAllHabitAssignsByUserIdAndAcquiredStatus(Long userId, Boolean acquired,
        String language) {
        return habitAssignRepo.findAllByUserIdAndAcquiredAndSuspendedFalse(userId, acquired)
            .stream().map(habitAssign -> buildHabitAssignDto(habitAssign, language)).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<HabitAssignDto> getAllHabitAssignsByHabitIdAndAcquiredStatus(Long habitId, Boolean acquired,
        String language) {
        return habitAssignRepo.findAllByHabitIdAndAcquiredAndSuspendedFalse(habitId, acquired)
            .stream().map(habitAssign -> buildHabitAssignDto(habitAssign, language)).collect(Collectors.toList());
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
                ErrorMessage.HABIT_ASSIGN_NOT_FOUND_WITH_CURRENT_USER_ID_AND_HABIT_ID + habitId));

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
                habitStatisticService.deleteAllStatsByHabitAssign(habitAssignVO);
                habitAssignRepo.delete(habitAssign);
            });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HabitStatusCalendarDto enrollHabit(Long habitId, Long userId) {
        HabitAssign habitAssign = habitAssignRepo.findByHabitIdAndUserIdAndSuspendedFalse(habitId, userId)
            .orElseThrow(() -> new NotFoundException(
                ErrorMessage.HABIT_ASSIGN_NOT_FOUND_WITH_CURRENT_USER_ID_AND_HABIT_ID + habitId));

        LocalDate todayDate = LocalDate.now();
        updateHabitAssignAfterEnroll(habitAssign, todayDate);

        HabitStatusCalendar habitCalendar = HabitStatusCalendar.builder()
            .enrollDate(todayDate).habitAssign(habitAssign).build();

        HabitStatusCalendarVO habitCalendarVO =
            habitStatusCalendarService.save(modelMapper.map(habitCalendar, HabitStatusCalendarVO.class));
        return modelMapper.map(habitCalendarVO, HabitStatusCalendarDto.class);
    }

    /**
     * Method updates {@link HabitAssign} if it's completed.
     *
     * @param habitAssign {@link HabitAssign} instance.
     * @param todayDate   {@link LocalDate} date.
     */
    private void updateHabitAssignAfterEnroll(HabitAssign habitAssign, LocalDate todayDate) {
        habitAssign.setWorkingDays(habitAssign.getWorkingDays() + 1);
        habitAssign.setLastEnrollmentDate(ZonedDateTime.now());

        updateHabitStreakAfterEnroll(habitAssign, todayDate);

        if (isHabitAcquired(habitAssign)) {
            habitAssign.setAcquired(true);
        }
        habitAssignRepo.save(habitAssign);
    }

    /**
     * Method updates habit streak of {@link HabitAssign} and checks if it has been
     * already enrolled.
     *
     * @param habitAssign {@link HabitAssign} instance.
     * @param todayDate   {@link LocalDate} date.
     */
    private void updateHabitStreakAfterEnroll(HabitAssign habitAssign, LocalDate todayDate) {
        LocalDate lastEnrollmentDate = habitStatusCalendarService.findTopByEnrollDateAndHabitAssign(
            modelMapper.map(habitAssign, HabitAssignVO.class));

        int habitStreak = habitAssign.getHabitStreak();
        long intervalBetweenDates = 0;
        if (lastEnrollmentDate != null) {
            intervalBetweenDates = Period.between(lastEnrollmentDate, todayDate).getDays();
        }
        if ((intervalBetweenDates == 1) || lastEnrollmentDate == null) {
            habitAssign.setHabitStreak(++habitStreak);
        } else if (intervalBetweenDates > 1) {
            habitStreak = 1;
            habitAssign.setHabitStreak(habitStreak);
        } else {
            throw new BadRequestException(ErrorMessage.HABIT_HAS_BEEN_ALREADY_ENROLLED);
        }
    }

    /**
     * Method checks if {@link HabitAssign} is completed.
     *
     * @param habitAssign {@link HabitAssign} instance.
     * @return boolean.
     */
    private boolean isHabitAcquired(HabitAssign habitAssign) {
        int workingDays = habitAssign.getWorkingDays();
        int habitDuration = habitAssign.getDuration();
        if (workingDays == habitDuration) {
            if (Boolean.TRUE.equals(habitAssign.getAcquired())) {
                throw new BadRequestException(
                    ErrorMessage.HABIT_ALREADY_ACQUIRED + habitAssign.getHabit().getId());
            }
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unenrollHabit(Long habitId, Long userId, LocalDate date) {
        HabitAssign habitAssign = habitAssignRepo.findByHabitIdAndUserIdAndSuspendedFalse(habitId, userId)
            .orElseThrow(
                () -> new NotFoundException(ErrorMessage.HABIT_ASSIGN_NOT_FOUND_WITH_CURRENT_USER_ID_AND_HABIT_ID
                    + userId + ", " + habitId));

        deleteHabitStatusCalendarIfExists(date, habitAssign);
        updateHabitAssignAfterUnenroll(date, habitAssign);
    }

    /**
     * Method deletes enroll of {@link HabitAssign} if it exists.
     *
     * @param date        {@link LocalDate} date.
     * @param habitAssign {@link HabitAssign} instance.
     */
    private void deleteHabitStatusCalendarIfExists(LocalDate date, HabitAssign habitAssign) {
        HabitStatusCalendarVO habitStatusCalendarVO =
            habitStatusCalendarService
                .findHabitStatusCalendarByEnrollDateAndHabitAssign(
                    date, modelMapper.map(habitAssign, HabitAssignVO.class));
        if (habitStatusCalendarVO != null) {
            habitStatusCalendarService.delete(habitStatusCalendarVO);
        } else {
            throw new BadRequestException(ErrorMessage.HABIT_IS_NOT_ENROLLED);
        }
    }

    /**
     * Method updates {@link HabitAssign} after unenroll.
     *
     * @param date        {@link LocalDate} date.
     * @param habitAssign {@link HabitAssign} instance.
     */
    private void updateHabitAssignAfterUnenroll(LocalDate date, HabitAssign habitAssign) {
        int daysStreak = countHabitStreakAfterDate(date, habitAssign);
        habitAssign.setHabitStreak(daysStreak + 1);

        int workingDays = habitAssign.getWorkingDays();
        if (workingDays != 0) {
            habitAssign.setWorkingDays(--workingDays);
        }
        habitAssignRepo.save(habitAssign);
    }

    /**
     * Method returns number of habit streak after {@link LocalDate} parameter.
     *
     * @param dateTime    {@link LocalDate} date.
     * @param habitAssign {@link HabitAssign} instance.
     * @return int of habit days streak.
     */
    private int countHabitStreakAfterDate(LocalDate dateTime, HabitAssign habitAssign) {
        int daysStreak = 0;

        List<LocalDate> enrollDates = habitStatusCalendarService.findEnrolledDatesAfter(dateTime,
            modelMapper.map(habitAssign, HabitAssignVO.class));
        Collections.sort(enrollDates);

        for (int i = 0; i < enrollDates.size() - 1; i++) {
            if (Period.between(enrollDates.get(i), enrollDates.get(i + 1)).getDays() == 1) {
                daysStreak++;
            } else {
                daysStreak = 0;
            }
        }
        return daysStreak;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<HabitAssignDto> findActiveHabitAssignsOnDate(Long id, LocalDate date, String language) {
        List<HabitAssign> list = habitAssignRepo.findAllActiveHabitAssignsOnDate(date);
        return list.stream().map(
            habitAssign -> buildHabitAssignDto(habitAssign, language)).collect(Collectors.toList());
    }
}
