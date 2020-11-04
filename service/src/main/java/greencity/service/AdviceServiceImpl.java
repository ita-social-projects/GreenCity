package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.advice.AdviceDto;
import greencity.dto.advice.AdvicePostDto;
import greencity.dto.advice.AdviceVO;
import greencity.dto.habit.HabitVO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.entity.Advice;
import greencity.entity.Habit;
import greencity.entity.localization.AdviceTranslation;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotUpdatedException;
import greencity.exception.exceptions.WrongIdException;
import greencity.repository.AdviceRepo;
import greencity.repository.AdviceTranslationRepo;
import greencity.repository.HabitRepo;

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
    private final HabitRepo habitRepo;
    private final AdviceTranslationRepo adviceTranslationRepo;
    private final ModelMapper modelMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LanguageTranslationDTO> getAllAdvices() {
        return modelMapper.map(adviceTranslationRepo.findAll(), new TypeToken<List<LanguageTranslationDTO>>() {
        }.getType());
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
    public AdviceVO update(AdvicePostDto adviceDto, Long id) {
        Advice advice = adviceRepo.findById(id)
            .orElseThrow(() -> new NotUpdatedException(ErrorMessage.ADVICE_NOT_FOUND_BY_ID + id));
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

        return modelMapper.map(updated, AdviceVO.class);
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
}