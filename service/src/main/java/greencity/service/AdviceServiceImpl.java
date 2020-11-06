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
import greencity.entity.localization.AdviceTranslation;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotUpdatedException;
import greencity.exception.exceptions.WrongIdException;
import greencity.filters.AdviceSpecification;
import greencity.filters.SearchCriteria;
import greencity.repository.AdviceRepo;
import greencity.repository.AdviceTranslationRepo;
import greencity.repository.HabitRepo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
                .orElseThrow(() ->
                        new NotFoundException(ErrorMessage.ADVICE_NOT_FOUND_BY_ID + id)), LanguageTranslationDTO.class);
    }

    @Override
    public PageableDto<AdviceVO> searchBy(Pageable pageable, String query) {
        return buildPageableDto(adviceRepo.searchBy(pageable, query));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AdviceVO getAdviceById(Long id) {
        return modelMapper.map(adviceRepo.findById(id).orElseThrow(() ->
                new NotFoundException(ErrorMessage.ADVICE_NOT_FOUND_BY_ID + id)), AdviceVO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AdviceDto getAdviceByName(String language, String name) {
        return modelMapper.map(adviceTranslationRepo
                .findAdviceTranslationByLanguageCodeAndContent(language, name).orElseThrow(() ->
                        new NotFoundException(ErrorMessage.ADVICE_NOT_FOUND_BY_NAME + name)), AdviceDto.class);
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
        Advice advice = adviceRepo.findById(id).orElseThrow(() ->
                new NotUpdatedException(ErrorMessage.ADVICE_NOT_FOUND_BY_ID + id));
        Habit habit = habitRepo.findById(adviceDto.getHabit().getId())
                .orElseThrow(() -> new WrongIdException(ErrorMessage.HABIT_NOT_FOUND_BY_ID));
        advice.setHabit(habit);
        List<AdviceTranslation> adviceTranslations = modelMapper.map(adviceDto.getTranslations(),
                new TypeToken<List<AdviceTranslation>>() {
                }.getType());
        adviceTranslationRepo.deleteAllByAdvice(advice);
        advice.setTranslations(adviceTranslations);
        adviceTranslations.forEach(adviceTranslation -> adviceTranslation.setAdvice(advice));
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
     * * This method used for build {@link SearchCriteria} depends on {@link Advice}.
     *
     * @param adviceViewDto used for receive parameters for filters from UI.
     * @return {@link SearchCriteria}.
     */
    private List<SearchCriteria> buildSearchCriteria(AdviceViewDto adviceViewDto) {
        List<SearchCriteria> criteriaList = new ArrayList<>();

        Field[] fields = adviceViewDto.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String value;
            try {
                value = (String) field.get(adviceViewDto);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Cannot retrieve value from field!");
            }
            if (!value.isEmpty()) {
                criteriaList.add(SearchCriteria.builder()
                        .key(field.getName()).type(field.getName())
                        .value(value)
                        .build());
            }
        }

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
}