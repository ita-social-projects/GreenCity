package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.habit.HabitDto;
import greencity.dto.habit.HabitManagementDto;
import greencity.dto.habit.HabitVO;
import greencity.dto.habittranslation.HabitTranslationDto;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface HabitService {
    /**
     * Method find {@link HabitTranslationDto} by {@link HabitVO} and languageCode.
     * Method find {@link Habit} with all translations by id.
     *
     * @return {@link HabitTranslationDto}
     * @author Kovaliv Taras
     * @param id {@link Habit} id.
     * @return {@link HabitManagementDto}.
     */
    HabitManagementDto getById(Long id);

    /**
     * Method find {@link Habit} by id and language code.
     *
     * @param id           {@link Habit} id.
     * @param languageCode - language code.
     * @return {@link HabitDto}.
     */
    HabitDto getByIdAndLanguageCode(Long id, String languageCode);

    /**
     * Method find all {@link Habit} with all translations.
     *
     * @param pageable - instance of {@link Pageable}.
     * @return list of {@link HabitManagementDto}.
     * @author Dovganyuk Taras
     */
    PageableDto<HabitManagementDto> getAllHabitsDto(Pageable pageable);

    /**
     * Method returns all {@link Habit}'s by language code.
     *
     * @param pageable - instance of {@link Pageable}.
     * @param language - language code.
     * @return Pageable of {@link HabitTranslationDto}.
     * @author Dovganyuk Taras
     */
    PageableDto<HabitDto> getAllHabitsByLanguageCode(Pageable pageable, String language);

    /**
     * Method saves {@link Habit} with it's {@link HabitTranslation}'s.
     *
     * @param habitManagementDto {@link HabitManagementDto}.
     * @param image              {@link MultipartFile} image for habit.
     * @return {@link HabitDto}.
     */
    HabitManagementDto saveHabitAndTranslations(HabitManagementDto habitManagementDto, MultipartFile image);

    /**
     * Method updates {@link Habit} instance.
     *
     * @param habitManagementDto - instance of {@link HabitManagementDto}.
     * @param image              {@link MultipartFile} image for habit.
     */
    void update(HabitManagementDto habitManagementDto, MultipartFile image);

    /**
     * Method deletes {@link Habit} instance by it's id.
     *
     * @param id {@link Habit} id.
     */
    void delete(Long id);

    /**
     * Method deletes all {@link Habit} instances by list of id's.
     *
     * @param listId list of {@link Habit} id's.
     */
    void deleteAll(List<Long> listId);
}
