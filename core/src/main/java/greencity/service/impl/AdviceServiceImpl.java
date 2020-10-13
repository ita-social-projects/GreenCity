package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.dto.advice.AdviceDto;
import greencity.dto.advice.AdviceVO;
import greencity.dto.advice.AdvicePostDto;
import greencity.dto.language.LanguageTranslationVO;
import greencity.entity.Advice;
import greencity.entity.Habit;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotUpdatedException;
import greencity.repository.AdviceRepo;
import greencity.repository.AdviceTranslationRepo;
import greencity.service.AdviceService;
import greencity.service.HabitService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.dao.EmptyResultDataAccessException;
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
    private final HabitService habitService;
    private final AdviceTranslationRepo adviceTranslationRepo;
    private final ModelMapper modelMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LanguageTranslationVO> getAllAdvices() {
        return modelMapper.map(adviceTranslationRepo.findAll(), new TypeToken<List<LanguageTranslationVO>>() {
        }.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LanguageTranslationVO getRandomAdviceByHabitIdAndLanguage(Long id, String language) {
        return modelMapper.map(adviceTranslationRepo.getRandomAdviceTranslationByHabitIdAndLanguage(language, id)
            .orElseThrow(() ->
                new NotFoundException(ErrorMessage.ADVICE_NOT_FOUND_BY_ID + id)), LanguageTranslationVO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AdviceDto getAdviceById(Long id) {
        return modelMapper.map(adviceRepo.findById(id).orElseThrow(() ->
            new NotFoundException(ErrorMessage.ADVICE_NOT_FOUND_BY_ID + id)), AdviceDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AdviceDto getAdviceByName(String language, String name) {
        return modelMapper.map(adviceTranslationRepo
            .findAdviceTranslationByLanguageCodeAndAdvice(language, name).orElseThrow(() ->
                new NotFoundException(ErrorMessage.ADVICE_NOT_FOUND_BY_NAME + name)), AdviceDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AdviceVO save(AdvicePostDto advicePostDTO) {
        Advice saved = adviceRepo.save(modelMapper.map(advicePostDTO, Advice.class));
        return modelMapper.map(saved, AdviceVO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AdviceVO update(AdvicePostDto adviceDto, Long id) {
        Advice advice = adviceRepo.findById(id)
            .map(employee -> {
                Habit habit = habitService.getById(adviceDto.getHabit().getId());
                employee.setHabit(habit);
                return adviceRepo.save(employee);
            })
            .orElseThrow(() -> new NotUpdatedException(ErrorMessage.ADVICE_NOT_UPDATED));
        return modelMapper.map(advice, AdviceVO.class);
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
}