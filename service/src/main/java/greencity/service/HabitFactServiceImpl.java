package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.habit.HabitVO;
import greencity.dto.habitfact.HabitFactDto;
import greencity.dto.habitfact.HabitFactPostDto;
import greencity.dto.habitfact.HabitFactVO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.entity.Habit;
import greencity.entity.HabitFact;
import greencity.entity.HabitFactTranslation;
import greencity.entity.Language;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotUpdatedException;
import greencity.exception.exceptions.WrongIdException;
import greencity.repository.HabitFactRepo;
import greencity.repository.HabitFactTranslationRepo;
import greencity.repository.HabitRepo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link HabitFactService}.
 *
 * @author Vitaliy Dzen
 */
@Service
@AllArgsConstructor
public class HabitFactServiceImpl implements HabitFactService {
    private final HabitFactRepo habitFactRepo;
    private final HabitFactTranslationRepo habitFactTranslationRepo;
    private final ModelMapper modelMapper;
    private final HabitRepo habitRepo;

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<LanguageTranslationDTO> getAllHabitFacts(Pageable page, String language) {
        Page<HabitFactTranslation> habitFactTranslation = habitFactTranslationRepo
            .findByHabitFactTranslationsLanguageCodeOrderByIdAsc(language, page);
        return getPagesWithLanguageTranslationDTO(habitFactTranslation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LanguageTranslationDTO getRandomHabitFactByHabitIdAndLanguage(Long id, String language) {
        return modelMapper.map(habitFactTranslationRepo.getRandomHabitFactTranslationByHabitIdAndLanguage(language, id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_FACT_NOT_FOUND_BY_ID + id)),
            LanguageTranslationDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HabitFactDto getHabitFactById(Long id) {
        return modelMapper.map(habitFactRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_FACT_NOT_FOUND_BY_ID + id)),
            HabitFactDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HabitFactDto getHabitFactByName(String language, String name) {
        return modelMapper.map(habitFactTranslationRepo.findFactTranslationByLanguageCodeAndContent(language, name)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_FACT_NOT_FOUND_BY_ID + name)),
            HabitFactDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HabitFactVO save(HabitFactPostDto fact) {
        HabitFact map = modelMapper.map(fact, HabitFact.class);
        return modelMapper.map(habitFactRepo.save(map), HabitFactVO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HabitFactVO update(HabitFactPostDto factDto, Long id) {
        HabitFact habitFactFromDB = habitFactRepo.findById(id)
            .map(habitFact -> {
                Habit habit = habitRepo.findById(factDto.getHabit().getId())
                    .orElseThrow(() -> new WrongIdException(ErrorMessage.HABIT_NOT_FOUND_BY_ID));
                habitFact.setHabit(habit);
                updateHabitTranslations(habitFact.getTranslations(), factDto.getTranslations());
                return habitFactRepo.save(habitFact);
            }).orElseThrow(() -> new NotUpdatedException(ErrorMessage.HABIT_FACT_NOT_UPDATED_BY_ID));
        return modelMapper.map(habitFactFromDB, HabitFactVO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long delete(Long id) {
        if (habitFactRepo.findById(id).isEmpty()) {
            throw new NotDeletedException(ErrorMessage.HABIT_FACT_NOT_DELETED_BY_ID);
        }
        habitFactRepo.deleteById(id);
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteAllByHabit(HabitVO habit) {
        habitFactRepo.findAllByHabitId(habit.getId())
            .forEach(habitFact -> {
                habitFactTranslationRepo.deleteAllByHabitFact(habitFact);
                habitFactRepo.delete(habitFact);
            });
    }

    private PageableDto<LanguageTranslationDTO> getPagesWithLanguageTranslationDTO(Page<HabitFactTranslation> pages) {
        List<LanguageTranslationDTO> languageTranslationDTOS = pages
            .stream()
            .map(habitFactTranslation -> modelMapper.map(habitFactTranslation, LanguageTranslationDTO.class))
            .collect(Collectors.toList());
        return new PageableDto<>(
            languageTranslationDTOS,
            pages.getTotalElements(),
            pages.getPageable().getPageNumber(),
            pages.getTotalPages()
        );
    }

    private void updateHabitTranslations(List<HabitFactTranslation> habitFactTranslations,
                                         List<LanguageTranslationDTO> languageTranslationDTOS) {
        Iterator<HabitFactTranslation> adviceTranslationIterator = habitFactTranslations.iterator();
        Iterator<LanguageTranslationDTO> languageTranslationDTOIterator = languageTranslationDTOS.iterator();
        while (adviceTranslationIterator.hasNext() && languageTranslationDTOIterator.hasNext()) {
            HabitFactTranslation habitFactTranslation = adviceTranslationIterator.next();
            LanguageTranslationDTO languageDTO = languageTranslationDTOIterator.next();
            habitFactTranslation.setContent(languageDTO.getContent());
            habitFactTranslation.setLanguage(modelMapper.map(languageDTO.getLanguage(), Language.class));
        }
    }
}
