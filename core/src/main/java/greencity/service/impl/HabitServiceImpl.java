package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.habit.HabitDto;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.entity.Habit;
import greencity.entity.HabitTranslation;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.WrongIdException;
import greencity.repository.HabitRepo;
import greencity.repository.HabitTranslationRepo;
import greencity.service.HabitService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
    private final ModelMapper modelMapper;

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
    public HabitDto getById(Long id) {
        return modelMapper.map(habitRepo.findById(id)
            .orElseThrow(() -> new WrongIdException(ErrorMessage.HABIT_NOT_FOUND_BY_ID + id)),
            HabitDto.class);
    }

    @Override
    public PageableDto<HabitDto> getAllHabitsDto(Pageable pageable) {
        Page<Habit> habits = habitRepo.findAll(pageable);
        List<HabitDto> habitDtos = habitRepo.findAll()
            .stream()
            .map(habit -> modelMapper.map(habit, HabitDto.class))
            .collect(Collectors.toList());
        return new PageableDto<>(
            habitDtos,
            habits.getTotalElements(),
            habits.getPageable().getPageNumber(),
            habits.getTotalPages());
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