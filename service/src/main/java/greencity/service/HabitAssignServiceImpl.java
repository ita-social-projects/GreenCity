package greencity.service;

import greencity.achievement.AchievementCalculation;
import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habit.HabitAssignManagementDto;
import greencity.dto.habit.HabitAssignPropertiesDto;
import greencity.dto.habit.HabitAssignStatDto;
import greencity.dto.habit.HabitAssignVO;
import greencity.dto.habit.HabitDto;
import greencity.dto.habit.HabitEnrollDto;
import greencity.dto.habit.HabitVO;
import greencity.dto.habit.HabitsDateEnrollmentDto;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarVO;
import greencity.dto.user.UserVO;
import greencity.entity.*;
import greencity.enums.AchievementCategoryType;
import greencity.enums.AchievementType;
import greencity.enums.HabitAssignStatus;
import greencity.enums.ShoppingListItemStatus;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserAlreadyHasEnrolledHabitAssign;
import greencity.exception.exceptions.UserAlreadyHasHabitAssignedException;
import greencity.exception.exceptions.UserAlreadyHasMaxNumberOfActiveHabitAssigns;
import greencity.exception.exceptions.UserHasReachedOutOfEnrollRange;
import greencity.repository.HabitAssignRepo;
import greencity.repository.HabitRepo;
import greencity.repository.ShoppingListItemRepo;
import greencity.repository.UserShoppingListItemRepo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
    private final ShoppingListItemRepo shoppingListItemRepo;
    private final UserShoppingListItemRepo userShoppingListItemRepo;
    private final HabitStatisticService habitStatisticService;
    private final HabitStatusCalendarService habitStatusCalendarService;
    private final AchievementCalculation achievementCalculation;
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
        HabitAssign habitAssign =
            habitAssignRepo.findByHabitIdAndUserIdAndStatusIsCancelled(habitId, user.getId());

        if (habitAssign != null) {
            habitAssign.setStatus(HabitAssignStatus.INPROGRESS);
            habitAssign.setCreateDate(ZonedDateTime.now());
        } else {
            habitAssign = buildHabitAssign(habit, user);
        }

        enhanceAssignWithDefaultProperties(habitAssign);

        List<Long> shoppingList = shoppingListItemRepo.getAllShoppingListItemIdByHabitIdISContained(habitId);
        List<UserShoppingListItem> userShoppingList = new ArrayList<>();
        for (Long shoppingItem: shoppingList) {
            userShoppingList.add(UserShoppingListItem.builder()
                .habitAssign(habitAssign)
                .shoppingListItem(shoppingListItemRepo.getOne(shoppingItem))
                .dateCompleted(LocalDateTime.now())
                .status(ShoppingListItemStatus.ACTIVE)
                .build());
        }
        userShoppingListItemRepo.saveAll(userShoppingList);

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
        HabitAssign habitAssign =
            habitAssignRepo.findByHabitIdAndUserIdAndStatusIsCancelled(habitId, user.getId());

        if (habitAssign != null) {
            habitAssign.setStatus(HabitAssignStatus.INPROGRESS);
            habitAssign.setCreateDate(ZonedDateTime.now());
        } else {
            habitAssign = buildHabitAssign(habit, user);
        }
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
                .status(HabitAssignStatus.INPROGRESS)
                .createDate(ZonedDateTime.now())
                .user(user)
                .duration(habit.getDefaultDuration())
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
        HabitTranslation habitTranslation = getHabitTranslation(habitAssign, language);

        HabitAssignDto habitAssignDto = modelMapper.map(habitAssign, HabitAssignDto.class);
        habitAssignDto.setHabit(modelMapper.map(habitTranslation, HabitDto.class));
        return habitAssignDto;
    }

    /**
     * Method to get {@link HabitTranslation} for current habit assign and language.
     *
     * @param habitAssign {@link HabitAssign} habit assign.
     * @param language    {@link String} language code.
     */
    private HabitTranslation getHabitTranslation(HabitAssign habitAssign, String language) {
        return habitAssign.getHabit().getHabitTranslations().stream()
            .filter(ht -> ht.getLanguage().getCode().equals(language)).findFirst()
            .orElseThrow(() -> new NotFoundException(
                ErrorMessage.HABIT_TRANSLATION_NOT_FOUND + habitAssign.getHabit().getId()));
    }

    /**
     * Method validates new {@link HabitAssign} to be created for current user.
     *
     * @param habitId {@link Habit} id.
     * @param user    {@link User} instance.
     */
    private void validateHabitForAssign(Long habitId, User user) {
        if (habitAssignRepo.findByHabitIdAndUserId(habitId, user.getId()).isPresent()) {
            throw new UserAlreadyHasHabitAssignedException(
                ErrorMessage.USER_ALREADY_HAS_ASSIGNED_HABIT + habitId);
        }
        if (habitAssignRepo.countHabitAssignsByUserIdAndAcquiredFalseAndCancelledFalse(
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
    public HabitAssignDto findHabitAssignByUserIdAndHabitId(Long userId, Long habitId, String language) {
        HabitAssign habitAssign =
            habitAssignRepo.findByHabitIdAndUserId(habitId, userId)
                .orElseThrow(
                    () -> new NotFoundException(ErrorMessage.HABIT_ASSIGN_NOT_FOUND_WITH_CURRENT_USER_ID_AND_HABIT_ID
                        + habitId));
        return buildHabitAssignDto(habitAssign, language);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<HabitAssignDto> getAllHabitAssignsByUserIdAndAcquiredStatus(Long userId, String language) {
        return habitAssignRepo.findAllByUserId(userId)
            .stream().map(habitAssign -> buildHabitAssignDto(habitAssign, language)).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<HabitAssignDto> getAllHabitAssignsByHabitIdAndAcquiredStatus(Long habitId,
        String language) {
        return habitAssignRepo.findAllByHabitId(habitId)
            .stream().map(habitAssign -> buildHabitAssignDto(habitAssign, language)).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public HabitAssignManagementDto updateStatusByHabitIdAndUserId(Long habitId, Long userId,
        HabitAssignStatDto dto) {
        HabitAssign updatable = habitAssignRepo.findByHabitIdAndUserId(habitId, userId)
            .orElseThrow(() -> new NotFoundException(
                ErrorMessage.HABIT_ASSIGN_NOT_FOUND_WITH_CURRENT_USER_ID_AND_HABIT_ID + habitId));

        updatable.setStatus(dto.getStatus());

        return modelMapper.map(habitAssignRepo.save(updatable), HabitAssignManagementDto.class);
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
    public HabitAssignDto enrollHabit(Long habitId, Long userId, LocalDate dateTime, String language) {
        HabitAssign habitAssign = habitAssignRepo.findByHabitIdAndUserId(habitId, userId)
            .orElseThrow(() -> new NotFoundException(
                ErrorMessage.HABIT_ASSIGN_NOT_FOUND_WITH_CURRENT_USER_ID_AND_HABIT_ID + habitId));

        validateForEnroll(dateTime, habitAssign);

        HabitStatusCalendar habitCalendar = HabitStatusCalendar.builder()
            .enrollDate(dateTime).habitAssign(habitAssign).build();

        updateHabitAssignAfterEnroll(habitAssign, habitCalendar, userId);
        return buildHabitAssignDto(habitAssign, "en");
    }

    /**
     * Method validates existed enrolls of {@link HabitAssign} for creating new one.
     *
     * @param habitAssign {@link HabitAssign} instance.
     * @param dateTime    {@link LocalDate} date.
     */
    private void validateForEnroll(LocalDate dateTime, HabitAssign habitAssign) {
        HabitAssignVO habitAssignVO = modelMapper.map(habitAssign, HabitAssignVO.class);
        HabitStatusCalendarVO habitCalendarVO =
            habitStatusCalendarService.findHabitStatusCalendarByEnrollDateAndHabitAssign(
                dateTime, habitAssignVO);
        if (habitCalendarVO != null) {
            throw new UserAlreadyHasEnrolledHabitAssign(ErrorMessage.HABIT_HAS_BEEN_ALREADY_ENROLLED);
        }

        LocalDate today = LocalDate.now();
        LocalDate lastDayToEnroll = today.minusDays(AppConstant.MAX_PASSED_DAYS_OF_ABILITY_TO_ENROLL);
        if (!(dateTime.isBefore(today.plusDays(1)) && dateTime.isAfter(lastDayToEnroll))) {
            throw new UserHasReachedOutOfEnrollRange(
                ErrorMessage.HABIT_STATUS_CALENDAR_OUT_OF_ENROLL_RANGE);
        }
    }

    /**
     * Method updates {@link HabitAssign} after enroll.
     *
     * @param habitAssign {@link HabitAssign} instance.
     */
    private void updateHabitAssignAfterEnroll(HabitAssign habitAssign,
        HabitStatusCalendar habitCalendar, Long userId) {
        habitAssign.setWorkingDays(habitAssign.getWorkingDays() + 1);
        habitAssign.setLastEnrollmentDate(ZonedDateTime.now());

        List<HabitStatusCalendar> habitStatusCalendars =
            new ArrayList<>(habitAssign.getHabitStatusCalendars());
        habitStatusCalendars.add(habitCalendar);
        habitAssign.setHabitStatusCalendars(habitStatusCalendars);

        int habitStreak = countNewHabitStreak(habitAssign.getHabitStatusCalendars());
        habitAssign.setHabitStreak(habitStreak);
        CompletableFuture.runAsync(() -> achievementCalculation
            .calculateAchievement(userId, AchievementType.COMPARISON,
                AchievementCategoryType.HABIT_STREAK, habitStreak));

        if (isHabitAcquired(habitAssign)) {
            habitAssign.setStatus(HabitAssignStatus.ACQUIRED);
            CompletableFuture.runAsync(() -> achievementCalculation
                .calculateAchievement(userId, AchievementType.INCREMENT, AchievementCategoryType.HABIT_STREAK, 0));
        }
        habitAssignRepo.save(habitAssign);
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
            if (HabitAssignStatus.ACQUIRED.equals(habitAssign.getStatus())) {
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
    public HabitAssignDto unenrollHabit(Long habitId, Long userId, LocalDate date) {
        HabitAssign habitAssign = habitAssignRepo.findByHabitIdAndUserId(habitId, userId)
            .orElseThrow(
                () -> new NotFoundException(ErrorMessage.HABIT_ASSIGN_NOT_FOUND_WITH_CURRENT_USER_ID_AND_HABIT_ID
                    + userId + ", " + habitId));

        deleteHabitStatusCalendarIfExists(date, habitAssign);
        updateHabitAssignAfterUnenroll(habitAssign);

        return modelMapper.map(habitAssign, HabitAssignDto.class);
    }

    /**
     * Method checks and calls method for delete if enroll of {@link HabitAssign}
     * exists.
     *
     * @param date        {@link LocalDate} date.
     * @param habitAssign {@link HabitAssign} instance.
     */
    private void deleteHabitStatusCalendarIfExists(LocalDate date, HabitAssign habitAssign) {
        HabitStatusCalendarVO habitCalendarVO =
            habitStatusCalendarService
                .findHabitStatusCalendarByEnrollDateAndHabitAssign(
                    date, modelMapper.map(habitAssign, HabitAssignVO.class));
        deleteHabitStatusCalendar(habitAssign, habitCalendarVO);
    }

    /**
     * Method deletes enroll of {@link HabitAssign}.
     *
     * @param habitCalendarVO {@link HabitStatusCalendarVO} date.
     * @param habitAssign     {@link HabitAssign} instance.
     */
    private void deleteHabitStatusCalendar(HabitAssign habitAssign, HabitStatusCalendarVO habitCalendarVO) {
        if (habitCalendarVO != null) {
            List<HabitStatusCalendar> habitCalendars =
                new ArrayList<>(habitAssign.getHabitStatusCalendars());
            habitCalendars.removeIf(hc -> hc.getEnrollDate().isEqual(habitCalendarVO.getEnrollDate()));
            habitAssign.setHabitStatusCalendars(habitCalendars);
            habitStatusCalendarService.delete(habitCalendarVO);
        } else {
            throw new BadRequestException(ErrorMessage.HABIT_IS_NOT_ENROLLED);
        }
    }

    /**
     * Method updates {@link HabitAssign} after unenroll.
     *
     * @param habitAssign {@link HabitAssign} instance.
     */
    private void updateHabitAssignAfterUnenroll(HabitAssign habitAssign) {
        habitAssign.setWorkingDays(habitAssign.getWorkingDays() - 1);
        habitAssign.setHabitStreak(countNewHabitStreak(habitAssign.getHabitStatusCalendars()));

        habitAssignRepo.save(habitAssign);
    }

    /**
     * Method counts new habit streak for {@link HabitAssign}.
     *
     * @param habitCalendars {@link List} of {@link HabitStatusCalendar}'s.
     * @return int of habit days streak.
     */
    private int countNewHabitStreak(List<HabitStatusCalendar> habitCalendars) {
        habitCalendars.sort(Comparator.comparing(HabitStatusCalendar::getEnrollDate).reversed());

        LocalDate today = LocalDate.now();
        int daysStreak = 0;
        int daysPast = 0;
        for (HabitStatusCalendar hc : habitCalendars) {
            if (today.minusDays(daysPast++).equals(hc.getEnrollDate())) {
                daysStreak++;
            } else {
                return daysStreak;
            }
        }
        return daysStreak;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<HabitAssignDto> findInprogressHabitAssignsOnDate(Long userId, LocalDate date, String language) {
        List<HabitAssign> list = habitAssignRepo.findAllInprogressHabitAssignsOnDate(userId, date);
        return list.stream().map(
            habitAssign -> buildHabitAssignDto(habitAssign, language)).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override

    public List<HabitsDateEnrollmentDto> findHabitAssignsBetweenDates(Long userId, LocalDate from, LocalDate to,
        String language) {
        List<HabitAssign> habitAssignsBetweenDates = habitAssignRepo
            .findAllHabitAssignsBetweenDates(userId, from, to);
        List<LocalDate> dates = Stream.iterate(from, date -> date.plusDays(1))
            .limit(ChronoUnit.DAYS.between(from, to.plusDays(1)))
            .collect(Collectors.toList());
        List<HabitsDateEnrollmentDto> dtos = new ArrayList<>();
        for (LocalDate date : dates) {
            dtos.add(HabitsDateEnrollmentDto.builder().enrollDate(date)
                .habitAssigns(new ArrayList<>())
                .build());
        }
        habitAssignsBetweenDates.forEach(habitAssign -> buildHabitsDateEnrollmentDto(habitAssign, language, dtos));

        return dtos;
    }

    /**
     * Method to fill in all user enrollment activity in the list of
     * {@code HabitsDateEnrollmentDto}'s by {@code HabitAssign}'s list of habit
     * status calendar.
     *
     * @param habitAssign {@code HabitAssign} habit assign.
     * @param language    {@link String} of language code value.
     * @param list        of {@link HabitsDateEnrollmentDto} instances.
     */
    private void buildHabitsDateEnrollmentDto(HabitAssign habitAssign, String language,
        List<HabitsDateEnrollmentDto> list) {
        HabitTranslation habitTranslation = getHabitTranslation(habitAssign, language);

        for (HabitsDateEnrollmentDto dto : list) {
            if (checkIfHabitIsActiveOnDay(dto, habitAssign)) {
                markHabitOnHabitsEnrollmentDto(dto, checkIfHabitIsEnrolledOnDay(dto, habitAssign),
                    habitTranslation, habitAssign);
            }
        }
    }

    /**
     * Method to mark if habit was enrolled on concrete date.
     *
     * @param dto              {@link HabitsDateEnrollmentDto}.
     * @param isEnrolled       {@link boolean} shows if habit was enrolled.
     * @param habitTranslation {@link HabitTranslation} contains content.
     * @param habitAssign      {@link HabitAssign} contains habit id.
     */
    private void markHabitOnHabitsEnrollmentDto(HabitsDateEnrollmentDto dto, boolean isEnrolled,
        HabitTranslation habitTranslation, HabitAssign habitAssign) {
        dto.getHabitAssigns().add(HabitEnrollDto.builder()
            .habitDescription(habitTranslation.getDescription()).habitName(habitTranslation.getName())
            .isEnrolled(isEnrolled).habitId(habitAssign.getHabit().getId()).build());
    }

    /**
     * Method to check if {@code HabitAssign} was enrolled on concrete date.
     *
     * @param dto         {@link HabitsDateEnrollmentDto} which contains date.
     * @param habitAssign {@link HabitAssign} contains enroll dates.
     * @return boolean.
     */
    private boolean checkIfHabitIsEnrolledOnDay(HabitsDateEnrollmentDto dto, HabitAssign habitAssign) {
        for (int i = 0; i < habitAssign.getHabitStatusCalendars().size(); i++) {
            if (habitAssign.getHabitStatusCalendars().get(i).getEnrollDate()
                .equals(dto.getEnrollDate())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method to check if {@code HabitAssign} is active on concrete date.
     *
     * @param dto         {@link HabitsDateEnrollmentDto} which contains date.
     * @param habitAssign {@link HabitAssign} contains habit date borders.
     * @return boolean.
     */
    private boolean checkIfHabitIsActiveOnDay(HabitsDateEnrollmentDto dto, HabitAssign habitAssign) {
        return dto.getEnrollDate()
            .isBefore(habitAssign.getCreateDate().toLocalDate().plusDays(habitAssign.getDuration() + 1L))
            && dto.getEnrollDate()
                .isAfter(habitAssign.getCreateDate().toLocalDate().minusDays(1L));
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void addDefaultHabit(UserVO user, String language) {
        if (habitAssignRepo.findAllByUserId(user.getId()).isEmpty()) {
            UserVO userVO = modelMapper.map(user, UserVO.class);
            assignDefaultHabitForUser(1L, userVO);
        }
    }

    /**
     * Method to set {@link HabitAssign} status from inprogress to cancelled.
     *
     * @param habitId - id of {@link HabitVO}.
     * @param userId  - id of {@link UserVO}.
     * @return {@link HabitAssignDto}.
     */
    @Transactional
    @Override
    public HabitAssignDto cancelHabitAssign(Long habitId, Long userId) {
        HabitAssign habitAssignToCancel = habitAssignRepo.findByHabitIdAndUserIdAndStatusIsInprogress(habitId, userId)
            .orElseThrow(() -> new NotFoundException(
                ErrorMessage.HABIT_ASSIGN_NOT_FOUND_WITH_CURRENT_USER_ID_AND_HABIT_ID_AND_INPROGRESS_STATUS + habitId));
        habitAssignToCancel.setStatus(HabitAssignStatus.CANCELLED);
        habitAssignRepo.save(habitAssignToCancel);
        return buildHabitAssignDto(habitAssignToCancel, "en");
    }

    /**
     * {@inheritDoc}
     */
    public void deleteHabitAssign(Long habitId, Long userId) {
        HabitAssign habitAssign = habitAssignRepo.findByUserIdAndHabitId(habitId, userId)
            .orElseThrow(() -> new NotFoundException(
                ErrorMessage.HABIT_ASSIGN_NOT_FOUND_WITH_CURRENT_USER_ID_AND_HABIT_ID));
        habitAssignRepo.delete(habitAssign);
    }
}
