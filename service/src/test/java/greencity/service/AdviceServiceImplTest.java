package greencity.service;

import greencity.ModelUtils;
import greencity.dto.advice.AdviceDto;
import greencity.dto.advice.AdvicePostDto;
import greencity.dto.advice.AdviceVO;
import greencity.dto.habit.HabitVO;
import greencity.dto.language.LanguageDTO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.dto.user.HabitIdRequestDto;
import greencity.entity.Advice;
import greencity.entity.Habit;
import greencity.entity.Language;
import greencity.entity.localization.AdviceTranslation;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotUpdatedException;
import greencity.repository.AdviceRepo;
import greencity.repository.AdviceTranslationRepo;
import greencity.repository.HabitRepo;
import java.lang.reflect.Type;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.dao.EmptyResultDataAccessException;

@ExtendWith(MockitoExtension.class)
class AdviceServiceImplTest {
    @InjectMocks
    private AdviceServiceImpl adviceService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AdviceRepo adviceRepo;

    @Mock
    private HabitRepo habitRepo;

    @Mock
    private AdviceTranslationRepo adviceTranslationRepo;

    private Language defaultLanguage = ModelUtils.getLanguage();

    private List<AdviceTranslation> adviceTranslations = new ArrayList<>(Arrays.asList(
        AdviceTranslation.builder().id(1L).language(defaultLanguage).content("hello").build(),
        AdviceTranslation.builder().id(2L).language(defaultLanguage).content("text").build(),
        AdviceTranslation.builder().id(3L).language(defaultLanguage).content("smile").build()));

    private List<LanguageTranslationDTO> languageTranslationDTOs = Arrays.asList(
        new LanguageTranslationDTO(new LanguageDTO(1L, "en"), "hello"),
        new LanguageTranslationDTO(new LanguageDTO(1L, "en"), "text"),
        new LanguageTranslationDTO(new LanguageDTO(1L, "en"), "smile"));

    private List<Advice> advices = new ArrayList<>(Arrays.asList(
        Advice.builder().id(1L).habit(Habit.builder().id(1L).build()).build(),
        Advice.builder().id(2L).habit(Habit.builder().id(1L).build()).build(),
        Advice.builder().id(3L).habit(Habit.builder().id(1L).build()).build()));

    private Habit habit = Habit.builder().id(1L).image("image.png").build();

    private HabitVO habitVO = HabitVO.builder().id(1L).image("image_png").build();

    private Advice advice = Advice.builder().id(1L)
        .translations(adviceTranslations)
        .habit(habit)
        .build();

    private AdviceDto getAdviceDto() {
        return modelMapper.map(advice, AdviceDto.class);
    }

    private AdvicePostDto getAdvicePostDto() {
        return new AdvicePostDto(languageTranslationDTOs, new HabitIdRequestDto(1L));
    }

    @Test
    void getAllAdvices() {
        Type type = new TypeToken<List<LanguageTranslationDTO>>() {
        }.getType();
        when(adviceTranslationRepo.findAll()).thenReturn(adviceTranslations);
        when(modelMapper.map(adviceTranslations, type)).thenReturn(languageTranslationDTOs);
        List<LanguageTranslationDTO> actual = adviceService.getAllAdvices();

        assertEquals(languageTranslationDTOs, actual);
    }

    @Test
    void getRandomAdviceByHabitIdAndLanguage() {
        String language = "en";
        Long id = 1L;
        AdviceTranslation adviceTranslation = adviceTranslations.get(0);
        LanguageTranslationDTO expected = languageTranslationDTOs.get(0);
        when(adviceTranslationRepo.getRandomAdviceTranslationByHabitIdAndLanguage(language, id))
            .thenReturn(Optional.of(adviceTranslation));
        when(modelMapper.map(adviceTranslation, LanguageTranslationDTO.class)).thenReturn(expected);
        LanguageTranslationDTO actual = adviceService.getRandomAdviceByHabitIdAndLanguage(id, language);

        assertEquals(expected, actual);
    }

    @Test
    void getRandomAdviceByHabitIdAndLanguageThrowNotFoundException() {
        String language = "en";
        Long id = 1L;

        assertThrows(NotFoundException.class, () -> adviceService.getRandomAdviceByHabitIdAndLanguage(id, language));
    }

    @Test
    void getAdviceById() {
        Long id = 1L;
        AdviceVO expected = modelMapper.map(advice, AdviceVO.class);
        when(adviceRepo.findById(id)).thenReturn(Optional.of(advice));
        when(modelMapper.map(advice, AdviceVO.class)).thenReturn(expected);
        AdviceVO actual = adviceService.getAdviceById(id);

        assertEquals(expected, actual);
    }

    @Test
    void getAdviceByIdThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> adviceService.getAdviceById(1L));
    }

    @Test
    void getAdviceByName() {
        String language = "en";
        String name = "name";
        AdviceTranslation adviceTranslation = adviceTranslations.get(0);
        AdviceDto expected = getAdviceDto();
        when(adviceTranslationRepo.findAdviceTranslationByLanguageCodeAndContent(language, name))
            .thenReturn(Optional.of(adviceTranslation));
        when(modelMapper.map(adviceTranslation, AdviceDto.class)).thenReturn(expected);
        AdviceDto actual = adviceService.getAdviceByName(language, name);

        assertEquals(expected, actual);
    }

    @Test
    void getAdviceByNameThrowNotFoundException() {
        String language = "en";
        String name = "name";

        assertThrows(NotFoundException.class, () -> adviceService.getAdviceByName(language, name));
    }

    @Test
    void save() {
        AdvicePostDto advicePostDto = getAdvicePostDto();
        when(modelMapper.map(advicePostDto, Advice.class)).thenReturn(advice);
        when(adviceRepo.save(advice)).thenReturn(advice);
        AdviceVO expected = modelMapper.map(advice, AdviceVO.class);
        when(modelMapper.map(advice, AdviceVO.class)).thenReturn(expected);
        AdviceVO actual = adviceService.save(advicePostDto);

        assertEquals(expected, actual);
    }

    @Test
    void update() {
        AdvicePostDto advicePostDto = getAdvicePostDto();
        Long adviceId = 1L;
        Long habitId = advicePostDto.getHabit().getId();

        when(adviceRepo.findById(adviceId)).thenReturn(Optional.of(advice));
        when(habitRepo.findById(habitId)).thenReturn(Optional.of(habit));
        advice.setHabit(habit);
        Type type = new TypeToken<List<AdviceTranslation>>() {
        }.getType();
        when(modelMapper.map(advicePostDto.getTranslations(), type)).thenReturn(adviceTranslations);
        advice.setTranslations(adviceTranslations);
        when(adviceRepo.save(advice)).thenReturn(advice);
        AdviceVO expected = modelMapper.map(advice, AdviceVO.class);
        when(modelMapper.map(advice, AdviceVO.class)).thenReturn(expected);
        AdviceVO actual = adviceService.update(advicePostDto, adviceId);

        assertEquals(expected, actual);
        verify(adviceTranslationRepo, times(1)).deleteAllByAdvice(advice);
    }

    @Test
    void updateThrowNotFoundException() {
        Long adviceId = 1L;
        AdvicePostDto advicePostDto = getAdvicePostDto();

        assertThrows(NotUpdatedException.class, () -> adviceService.update(advicePostDto, adviceId));
    }

    @Test
    void delete() {
        Long expected = 1L;
        Long actual = adviceService.delete(expected);

        verify(adviceRepo).deleteById(expected);
        assertEquals(expected, actual);
    }

    @Test
    void deleteThrowNotDeletedException() {
        Long id = 1L;
        doThrow(EmptyResultDataAccessException.class).when(adviceRepo).deleteById(id);

        assertThrows(NotDeletedException.class, () -> adviceService.delete(id));
    }

    @Test
    void deleteAllByHabit() {
        Long habitId = habit.getId();
        when(modelMapper.map(habitVO, Habit.class)).thenReturn(habit);
        when(adviceRepo.findAllByHabitId(habitId)).thenReturn(advices);
        adviceService.deleteAllByHabit(habitVO);
        int numberOfAdvices = advices.size();

        verify(adviceTranslationRepo, times(numberOfAdvices)).deleteAllByAdvice(any(Advice.class));
        verify(adviceRepo, times(numberOfAdvices)).delete(any(Advice.class));
    }

    @Test
    void deleteAllByHabitReturnEmptyList() {
        Long habitId = habit.getId();
        when(modelMapper.map(habitVO, Habit.class)).thenReturn(habit);
        when(adviceRepo.findAllByHabitId(habitId)).thenReturn(Collections.emptyList());
        adviceService.deleteAllByHabit(habitVO);

        verify(adviceTranslationRepo, never()).deleteAllByAdvice(any(Advice.class));
        verify(adviceRepo, never()).delete(any(Advice.class));
    }
}
