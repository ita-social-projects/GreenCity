package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habit.HabitDto;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.HabitTranslation;
import greencity.entity.User;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserAlreadyHasHabitAssignedException;
import greencity.exception.exceptions.WrongIdException;
import greencity.repository.HabitAssignRepo;
import greencity.repository.HabitRepo;
import greencity.repository.HabitTranslationRepo;
import greencity.service.HabitService;
import greencity.service.HabitStatusService;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link HabitService}.
 *
 * @author Kovaliv Taras
 */
@Service
@AllArgsConstructor
public class HabitServiceImpl implements HabitService {
    private final HabitTranslationRepo habitTranslationRepo;
    private final HabitRepo habitRepo;
    private final HabitStatusService habitStatusService;
    private final ModelMapper modelMapper;
    private final HabitAssignRepo habitAssignRepo;

    /**
     * Method {@link HabitTranslation} by {@link Habit} and languageCode.
     *
     * @return {@link HabitTranslation}
     * @author Kovaliv Taras
     */
    @Override
    public HabitTranslationDto getHabitTranslation(Habit habit, String languageCode) {
        HabitTranslation habitTranslation = habitTranslationRepo
            .findByHabitAndLanguageCode(habit, languageCode)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_TRANSLATION_NOT_FOUND));
        return modelMapper.map(habitTranslation, HabitTranslationDto.class);
    }

    /**
     * Method find {@link Habit} by id.
     *
     * @param id - id of Habit
     * @return {@link Habit}
     * @author Kovaliv Taras
     */
    @Override
    public Habit getById(Long id) {
        return habitRepo.findById(id)
            .orElseThrow(() -> new WrongIdException(ErrorMessage.HABIT_NOT_FOUND_BY_ID + id));
    }

    /**
     * Method assign {@link Habit} for user.
     *
     * @param habitId - id of habit user want to assign
     * @param user    - user that assign habit
     * @return {@link HabitAssignDto}
     */
    @Transactional
    @Override
    public HabitAssignDto assignHabitForUser(Long habitId, User user) {
        Habit habit = habitRepo.findById(habitId)
            .orElseThrow(() -> new WrongIdException(ErrorMessage.HABIT_NOT_FOUND_BY_ID + habitId));

        if (habitAssignRepo.findByHabitIdAndUserId(habitId, user.getId()).isPresent()) {
            throw new UserAlreadyHasHabitAssignedException(
                ErrorMessage.USER_ALREADY_HAS_ASSIGNED_HABIT + habitId);
        } else {
            HabitAssign habitAssign = habitAssignRepo.save(
                HabitAssign.builder()
                    .habit(habit)
                    .acquired(false)
                    .createDate(ZonedDateTime.now())
                    .user(user)
                    .build());

            habitStatusService.saveByHabitAssign(habitAssign);
            return modelMapper.map(habitAssign, HabitAssignDto.class);
        }
    }

    @Override
    public List<HabitDto> getAllHabitsDto() {
        return habitRepo.findAll()
            .stream()
            .map(habit -> modelMapper.map(habit, HabitDto.class))
            .collect(Collectors.toList());
    }

    @Override
    public PageableDto<HabitTranslationDto> getAllHabitsByLanguageCode(Pageable pageable, String language) {
        Page<HabitTranslation> pages =
            habitTranslationRepo.findAllByLanguageCode(pageable, language);
        List<HabitTranslationDto> habitTranslationDtos =
            pages.stream()
                .map(habit -> modelMapper.map(habit, HabitTranslationDto.class))
                .collect(Collectors.toList());
        return new PageableDto<>(habitTranslationDtos, pages.getTotalElements(),
            pages.getPageable().getPageNumber(),
            pages.getTotalPages());
    }
}