package greencity.service;

import greencity.dto.habit.*;
import greencity.dto.user.UserVO;

import java.time.LocalDate;
import java.util.List;

public interface HabitAssignService {
    /**
     * Method to find {@code HabitAssign} by id.
     *
     * @param language {@link String} of language code value.
     * @return {@link HabitAssignDto}.
     */
    HabitAssignDto getById(Long habitAssignId, String language);

    /**
     * Method for assigning {@code Habit} with default properties.
     *
     * @param habitId {@code Habit} id.
     * @param user    @link UserVO} id.
     * @return {@link HabitAssignManagementDto}.
     */
    HabitAssignManagementDto assignDefaultHabitForUser(Long habitId, UserVO user);

    /**
     * Method for assigning {@code Habit} with custom properties.
     *
     * @param habitId                  {@code Habit} id.
     * @param user                     @link UserVO} id.
     * @param habitAssignPropertiesDto {@link HabitAssignPropertiesDto} instance.
     * @return {@link HabitAssignManagementDto}.
     */
    HabitAssignManagementDto assignCustomHabitForUser(Long habitId, UserVO user,
        HabitAssignPropertiesDto habitAssignPropertiesDto);

    /**
     * Method to find {@code HabitAssign} by {@code Habit} id and {@code User} id.
     *
     *
     * @param userId   {@code User} id.
     * @param habitId  {@code Habit} id.
     * @param language {@link String} of language code value.
     * @return HabitAssignDto.
     */
    HabitAssignDto findHabitAssignByUserIdAndHabitId(Long userId, Long habitId, String language);

    /**
     * Method to find all (not cancelled) {@code HabitAssign}'s by
     * {@code User} id and acquired status.
     *
     * @param userId   {@code User} id.
     * @param language {@link String} of language code value.
     * @return list of {@link HabitAssignDto}.
     */
    List<HabitAssignDto> getAllHabitAssignsByUserIdAndAcquiredStatus(Long userId, String language);

    /**
     * Method to find all(not cancelled) {@code HabitAssign}'s by
     * {@code Habit} id and acquired status.
     *
     * @param habitId  {@code Habit} id.
     * @param language {@link String} of language code value.
     * @return list of {@link HabitAssignDto}.
     */
    List<HabitAssignDto> getAllHabitAssignsByHabitIdAndAcquiredStatus(Long habitId, String language);

    /**
     * Method to delete all {@code HabitAssign}'s by {@code Habit} instance.
     *
     * @param habit {@link HabitVO} instance.
     */
    void deleteAllHabitAssignsByHabit(HabitVO habit);

    /**
     * Method for updating inprogress, acquired {@code HabitAssign} in database by {@code Habit} and
     * {@code User} id's.
     *
     * @param habitId {@code Habit} id.
     * @param userId  {@code User} id.
     * @param dto     {@link HabitAssignStatDto} dto with new cancelled and acquired
     *                status.
     * @return {@link HabitAssignManagementDto} instance.
     */
    HabitAssignManagementDto updateStatusByHabitIdAndUserId(Long habitId, Long userId, HabitAssignStatDto dto);

    /**
     * Method to enroll {@code Habit}.
     *
     * @param habitId  {@code Habit} id to enroll.
     * @param userId   {@code User} id.
     * @param dateTime {@link LocalDate} dateTime we want enroll.
     * @return {@link HabitAssignDto}.
     */
    HabitAssignDto enrollHabit(Long habitId, Long userId, LocalDate dateTime);

    /**
     * Method to unenroll Habit in defined date.
     *
     * @param habitId  {@code Habit} id to unenroll.
     * @param userId   {@code User} id.
     * @param dateTime {@link LocalDate} dateTime we want unenroll.
     * @return {@link HabitAssignDto}.
     */
    HabitAssignDto unenrollHabit(Long habitId, Long userId, LocalDate dateTime);

    /**
     * Method to find all inprogress habit assigns on certain {@link LocalDate}.
     *
     * @param userId   {@code User} id.
     * @param date     {@link LocalDate} instance.
     * @param language {@link String} of language code value.
     * @return list of {@link HabitAssignDto} instances.
     */
    List<HabitAssignDto> findInprogressHabitAssignsOnDate(Long userId, LocalDate date, String language);

    /**
     * Method to find all inprogress, acquired habit assigns between 2 {@link LocalDate}s.
     *
     * @param userId   {@code User} id.
     * @param from     {@link LocalDate} instance.
     * @param to       {@link LocalDate} instance.
     * @param language {@link String} of language code value.
     * @return list of {@link HabitAssignDto} instances.
     */
    List<HabitsDateEnrollmentDto> findHabitAssignsBetweenDates(Long userId,
                                                               LocalDate from, LocalDate to, String language);

    /**
     * Method add default habit.
     *
     * @param user {@link UserVO} instance.
     */
    void addDefaultHabit(UserVO user, String language);

    /**
     * Method to set {@link HabitAssignVO} status from inprogress to cancelled.
     *
     * @param habitId - id of {@link HabitVO}.
     * @param userId  - id of {@link UserVO} .
     * @return {@link HabitAssignDto}.
     */
    HabitAssignDto cancelHabitAssign(Long habitId, Long userId);
}
