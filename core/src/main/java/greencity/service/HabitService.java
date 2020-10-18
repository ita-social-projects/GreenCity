package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.habit.HabitDto;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.entity.Habit;
import greencity.entity.HabitTranslation;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface HabitService {
    /**
     * Method find {@link HabitTranslation} by {@link Habit} and languageCode.
     *
     * @param habit {@link Habit} instance.
     * @param languageCode - language code.
     * @return {@link HabitTranslationDto}.
     * @author Kovaliv Taras
     */
    HabitTranslationDto getHabitTranslation(Habit habit, String languageCode);

    /**
     * Method find {@link Habit} by id.
     *
     * @param id {@link Habit} id.
     * @return {@link HabitDto}
     * @author Kovaliv Taras
     */
    HabitDto getById(Long id);

    /**
     * Method find all {@link HabitDto}.
     *
     * @param pageable - instance of {@link Pageable}.
     * @return list of {@link HabitDto}
     * @author Dovganyuk Taras
     */
    PageableDto<HabitDto> getAllHabitsDto(Pageable pageable);

    /**
     * Method returns all habits by language.
     *
     * @param pageable - instance of {@link Pageable}.
     * @param language - language code.
     * @return Pageable of {@link HabitTranslationDto}
     * @author Dovganyuk Taras
     */
    PageableDto<HabitTranslationDto> getAllHabitsByLanguageCode(Pageable pageable, String language);

    /**
     * Method saves {@link Habit} with it's {@link HabitTranslation}'s.
     *
     * @param habitDto {@link HabitDto}.
     * @param image {@link MultipartFile} image for habit.
     * @return {@link HabitDto}.
     */
    HabitDto saveHabitAndTranslations(HabitDto habitDto, MultipartFile image);

    /**
     * Method updates {@link Habit} instance.
     *
     * @param habitDto - instance of {@link HabitDto}.
     * @param image {@link MultipartFile} image for habit.
     */
    void update(HabitDto habitDto, MultipartFile image);

    /**
     * Method deletes {@link Habit} instance.
     *
     * @param id {@link Habit} id.
     */
    void delete(Long id);

    /**
     * Method deletes all {@link Habit} instances by list of id's.
     *
     *
     * @param listId list of {@link Habit} id's.
     */
    void deleteAll(List<Long> listId);
}
