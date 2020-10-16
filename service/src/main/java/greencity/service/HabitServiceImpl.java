package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.habit.HabitDto;
import greencity.dto.habit.HabitVO;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.entity.Habit;
import greencity.entity.HabitTranslation;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.WrongIdException;
import greencity.repository.HabitRepo;
import greencity.repository.HabitTranslationRepo;
import greencity.service.constant.ErrorMessage;
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
    public HabitTranslationDto getHabitTranslation(HabitVO habit, String languageCode) {
        Habit mappedHabit = modelMapper.map(habit, Habit.class);
        HabitTranslation habitTranslation = habitTranslationRepo
            .findByHabitAndLanguageCode(mappedHabit, languageCode)
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
    public HabitVO getById(Long id) {
        Habit habit = habitRepo.findById(id)
            .orElseThrow(() -> new WrongIdException(ErrorMessage.HABIT_NOT_FOUND_BY_ID + id));
        return modelMapper.map(habit, HabitVO.class);
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