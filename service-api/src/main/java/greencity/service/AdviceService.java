package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.advice.AdviceDto;
import greencity.dto.advice.AdvicePostDto;
import greencity.dto.advice.AdviceVO;
import greencity.dto.advice.AdviceViewDto;
import greencity.dto.habit.HabitVO;
import greencity.dto.language.LanguageTranslationDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * AdviceService interface.
 *
 * @author Vitaliy Dzen
 */
public interface AdviceService {
    /**
     * Method finds all {@link AdviceDto}.
     *
     * @return List of all {@link AdviceDto}
     * @author Vitaliy Dzen
     */
    PageableDto<AdviceVO> getAllAdvices(Pageable pageable);

    /**
     * Method finds all {@link AdviceVO}'s that satisfy filters .
     *
     * @return List of all {@link AdviceVO}
     * @author Markiyan Derevetskyi
     */
    PageableDto<AdviceVO> getFilteredAdvices(Pageable pageable, AdviceViewDto adviceViewDto);

    /**
     * Method finds random {@link AdviceDto}.
     *
     * @return random {@link AdviceDto}
     * @author Vitaliy Dzen
     */
    LanguageTranslationDTO getRandomAdviceByHabitIdAndLanguage(Long id, String language);

    /**
     * Method finds all {@link AdviceVO} by query.
     *
     * @param query {@link String} - string query
     * @param pageable {@link Pageable}
     * @return list of found {@link AdviceVO}
     * @author Markiyan Derevetskyi
     * */
    PageableDto<AdviceVO> searchBy(Pageable pageable, String query);

    /**
     * Method find {@link AdviceVO} by id.
     *
     * @param id of {@link AdviceDto}
     * @return {@link AdviceVO}
     * @author Vitaliy Dzen
     */
    AdviceVO getAdviceById(Long id);

    /**
     * Method find {@link AdviceDto} by content.
     *
     * @param name of {@link AdviceDto}
     * @return {@link AdviceDto}
     * @author Vitaliy Dzen
     */
    AdviceDto getAdviceByName(String language, String name);

    /**
     * Method saves new {@link AdvicePostDto}.
     *
     * @param advice {@link AdvicePostDto}
     * @return instance of {@link AdviceVO}
     * @author Vitaliy Dzen
     */
    AdviceVO save(AdvicePostDto advice);

    /**
     * Method updates {@link AdviceVO}.
     *
     * @param advice {@link AdvicePostDto}
     * @return instance of {@link AdvicePostDto}
     * @author Vitaliy Dzen
     */
    AdvicePostDto update(AdvicePostDto advice, Long id);

    /**
     * Method delete {@link AdviceDto} by id.
     *
     * @param id Long
     * @return id of deleted {@link AdviceDto}
     * @author Vitaliy Dzen
     */
    Long delete(Long id);

    /**
     * Method deletes all {@code Advice}'s by {@code Habit} instance.
     *
     * @param habit {@link HabitVO} instance.
     */
    void deleteAllByHabit(HabitVO habit);

    /**
     * Method that deletes all advices by given ids.
     *
     * @param ids - list of {@link Long}
     * @author Markiyan Derevetskyi
     * */
    void deleteAllByIds(List<Long> ids);
}
