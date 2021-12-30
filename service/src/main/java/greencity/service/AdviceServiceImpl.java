package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.advice.AdviceDto;
import greencity.dto.advice.AdvicePostDto;
import greencity.dto.advice.AdviceVO;
import greencity.dto.advice.AdviceViewDto;
import greencity.dto.habit.HabitVO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.entity.Advice;
import greencity.entity.Habit;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotUpdatedException;
import greencity.exception.exceptions.WrongIdException;
import greencity.filters.AdviceSpecification;
import greencity.filters.SearchCriteria;
import greencity.repository.AdviceRepo;
import greencity.repository.AdviceTranslationRepo;
import greencity.repository.HabitRepo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementation of {@link AdviceService}.
 *
 * @author Vitaliy Dzen
 */
@Service
@AllArgsConstructor
public class AdviceServiceImpl implements AdviceService {
    private final AdviceRepo adviceRepo;
    private final HabitRepo habitRepo;
    private final AdviceTranslationRepo adviceTranslationRepo;
    private final ModelMapper modelMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<AdviceVO> getAllAdvices(Pageable pageable) {
        return buildPageableDto(adviceRepo.findAll(pageable));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<AdviceVO> getAllAdvicesWithFilter(Pageable pageable, String filter) {
        return filter == null ? getAllAdvices(pageable) : filterByAllFields(pageable, filter);
    }

    @Override
    public PageableDto<AdviceVO> getFilteredAdvices(Pageable pageable, AdviceViewDto adviceViewDto) {
        Page<Advice> filteredAdvices = adviceRepo.findAll(getSpecification(adviceViewDto), pageable);

        return buildPageableDto(filteredAdvices);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LanguageTranslationDTO getRandomAdviceByHabitIdAndLanguage(Long id, String language) {
        return modelMapper.map(adviceTranslationRepo.getRandomAdviceTranslationByHabitIdAndLanguage(language, id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.ADVICE_NOT_FOUND_BY_ID + id)),
            LanguageTranslationDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LanguageTranslationDTO> getAllByHabitIdAndLanguage(Long habitId, String language) {
        if (Objects.equals(language, "uk")) {
            language = "ua";
        }
        return adviceTranslationRepo.getAllByHabitIdAndLanguageCode(habitId, language).stream()
            .map(advice -> modelMapper.map(advice, LanguageTranslationDTO.class))
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AdviceVO getAdviceById(Long id) {
        return modelMapper.map(
            adviceRepo.findById(id).orElseThrow(() -> new NotFoundException(ErrorMessage.ADVICE_NOT_FOUND_BY_ID + id)),
            AdviceVO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AdviceDto getAdviceByName(String language, String name) {
        return modelMapper.map(adviceTranslationRepo
            .findAdviceTranslationByLanguageCodeAndContent(language, name)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.ADVICE_NOT_FOUND_BY_NAME + name)), AdviceDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unlinkAdvice(String language, Long habitId, Integer[] advicesIndexes) {
        language = Objects.equals(language, "uk") ? "ua" : language;
        List<LanguageTranslationDTO> allAdvices = getAllByHabitIdAndLanguage(habitId, language);
        for (Integer index : advicesIndexes) {
            Long id = getAdviceByName(language, allAdvices.get(index - 1).getContent()).getId();
            delete(id);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AdviceVO save(AdvicePostDto advicePostDTO) {
        Advice advice = modelMapper.map(advicePostDTO, Advice.class);
        advice.getTranslations().forEach(adviceTranslation -> adviceTranslation.setAdvice(advice));
        Advice saved = adviceRepo.save(advice);

        return modelMapper.map(saved, AdviceVO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AdvicePostDto update(AdvicePostDto adviceDto, Long id) {
        Advice advice = adviceRepo.findById(id)
            .orElseThrow(() -> new NotUpdatedException(ErrorMessage.ADVICE_NOT_FOUND_BY_ID + id));
        Habit habit = habitRepo.findById(adviceDto.getHabit().getId())
            .orElseThrow(() -> new WrongIdException(ErrorMessage.HABIT_NOT_FOUND_BY_ID));
        advice.setHabit(habit);
        advice.getTranslations()
            .forEach(adviceTranslation -> adviceTranslation.setContent(adviceDto.getTranslations().stream()
                .filter(newTranslation -> newTranslation.getLanguage().getCode()
                    .equals(adviceTranslation.getLanguage().getCode()))
                .findFirst().get()
                .getContent()));
        Advice updated = adviceRepo.save(advice);

        return modelMapper.map(updated, AdvicePostDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long delete(Long id) {
        try {
            adviceRepo.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotDeletedException(ErrorMessage.ADVICE_NOT_DELETED);
        }
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAllByHabit(HabitVO habitVO) {
        Habit habit = modelMapper.map(habitVO, Habit.class);
        adviceRepo.findAllByHabitId(habit.getId())
            .forEach(advice -> {
                adviceTranslationRepo.deleteAllByAdvice(advice);
                adviceRepo.delete(advice);
            });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAllByIds(List<Long> ids) {
        ids.forEach(adviceRepo::deleteById);
    }

    private PageableDto<AdviceVO> buildPageableDto(Page<Advice> advices) {
        List<AdviceVO> adviceVOs = advices.getContent().stream()
            .map(advice -> modelMapper.map(advice, AdviceVO.class)).collect(Collectors.toList());

        return new PageableDto<>(adviceVOs, advices.getTotalElements(),
            advices.getPageable().getPageNumber(), advices.getTotalPages());
    }

    /**
     * Method filters by all fields all {@link AdviceVO}.
     *
     * @param pageable - {@link Pageable}
     * @param filter   - {@link String}
     * @return list of {@link AdviceVO}
     */
    private PageableDto<AdviceVO> filterByAllFields(Pageable pageable, String filter) {
        return buildPageableDto(adviceRepo.filterByAllFields(pageable, filter));
    }

    /**
     * This method used for build {@link SearchCriteria} depends on. {@link Advice}.
     *
     * @param adviceViewDto used for receive parameters for filters from UI.
     * @return {@link SearchCriteria}.
     */
    private List<SearchCriteria> buildSearchCriteria(AdviceViewDto adviceViewDto) {
        List<SearchCriteria> criteriaList = new ArrayList<>();

        setValueIfNotEmpty(criteriaList, "id", adviceViewDto.getId());
        setValueIfNotEmpty(criteriaList, "habitId", adviceViewDto.getHabitId());
        setValueIfNotEmpty(criteriaList, "translationContent", adviceViewDto.getTranslationContent());

        return criteriaList;
    }

    /**
     * Returns {@link AdviceSpecification} for entered filter parameters.
     *
     * @param adviceViewDto contains data from filters
     */
    private AdviceSpecification getSpecification(AdviceViewDto adviceViewDto) {
        return new AdviceSpecification(buildSearchCriteria(adviceViewDto));
    }

    /**
     * Method that adds new {@link SearchCriteria}.
     *
     * @param searchCriteria - list of existing {@link SearchCriteria}
     * @param key            - key of field
     * @param value          - value of field
     */
    private void setValueIfNotEmpty(List<SearchCriteria> searchCriteria, String key, String value) {
        if (!StringUtils.isEmpty(value)) {
            searchCriteria.add(SearchCriteria.builder()
                .key(key)
                .type(key)
                .value(value)
                .build());
        }
    }
}