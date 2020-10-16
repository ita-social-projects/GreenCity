package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.habit.HabitDto;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.entity.Habit;
import greencity.entity.HabitTranslation;
import greencity.entity.Language;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.WrongIdException;
import greencity.repository.HabitRepo;
import greencity.repository.HabitTranslationRepo;
import greencity.service.FileService;
import greencity.service.HabitService;
import greencity.service.LanguageService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Implementation of {@link HabitService}.
 *
 * @author Kovaliv Taras
 */
@Service
@AllArgsConstructor
public class HabitServiceImpl implements HabitService {
    private final HabitTranslationRepo habitTranslationRepo;
    private final LanguageService languageService;
    private final FileService fileService;
    private final HabitRepo habitRepo;
    private final ModelMapper modelMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public HabitTranslationDto getHabitTranslation(Habit habit, String languageCode) {
        HabitTranslation habitTranslation = habitTranslationRepo
            .findByHabitAndLanguageCode(habit, languageCode)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_TRANSLATION_NOT_FOUND));
        return modelMapper.map(habitTranslation, HabitTranslationDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HabitDto getById(Long id) {
        return modelMapper.map(habitRepo.findById(id)
                .orElseThrow(() -> new WrongIdException(ErrorMessage.HABIT_NOT_FOUND_BY_ID + id)),
            HabitDto.class);
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public HabitDto saveHabitAndTranslations(HabitDto habitDto, MultipartFile image) {
        Habit habit = habitRepo.save(Habit.builder()
            .image(habitDto.getImage())
            .habitTranslations(
                habitDto.getHabitTranslations().stream()
                    .map(translationDto -> HabitTranslation.builder()
                        .description(translationDto.getDescription())
                        .habitItem(translationDto.getHabitItem())
                        .name(translationDto.getName())
                        .language(modelMapper.map(
                            languageService.findByCode(translationDto.getLanguageCode()),
                            Language.class)).build())
                    .collect(Collectors.toList())
            ).build());
        habit.getHabitTranslations().forEach(ht -> ht.setHabit(habit));
        /*if (habitDto.getImage() != null) {
            image = fileService.convertToMultipartImage(habitDto.getImage());
        }
        if (image != null) {
            habit.setImage(fileService.upload(image).toString());
            System.out.println(habit.getImage());
        }*/
        habitTranslationRepo.saveAll(habit.getHabitTranslations());
        return modelMapper.map(habit, HabitDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void update(HabitDto habitDto, MultipartFile image) {
        Habit habit = habitRepo.findById(habitDto.getId())
            .orElseThrow(() -> new WrongIdException(ErrorMessage.HABIT_NOT_FOUND_BY_ID));

        habit.getHabitTranslations()
            .forEach(ht -> {
                HabitTranslationDto htd = habitDto.getHabitTranslations().stream()
                    .filter(e -> e.getLanguageCode().equals(ht.getLanguage().getCode())).findFirst()
                    .orElseThrow(RuntimeException::new);

                ht.setDescription(htd.getDescription());
                ht.setHabitItem(htd.getHabitItem());
                ht.setName(htd.getName());
            });
        habit.setImage(habitDto.getImage());
        if (image != null) {
            habit.setImage(fileService.upload(image).toString());
        }
        habitRepo.save(habit);
    }
}