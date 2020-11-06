package greencity.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import greencity.ModelUtils;
import greencity.dto.PageableDto;
import greencity.dto.habit.HabitVO;
import greencity.dto.habitfact.HabitFactDto;
import greencity.dto.habitfact.HabitFactDtoResponse;
import greencity.dto.habitfact.HabitFactPostDto;
import greencity.dto.habitfact.HabitFactVO;
import greencity.dto.language.LanguageDTO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.entity.Habit;
import greencity.entity.HabitFact;
import greencity.entity.HabitFactTranslation;
import greencity.entity.Language;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotUpdatedException;
import greencity.repository.HabitFactRepo;
import greencity.repository.HabitFactTranslationRepo;
import greencity.repository.HabitRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class HabitFactServiceImplTest {
    @Mock
    HabitFactTranslationRepo habitFactTranslationRepo;
    @Mock
    HabitFactRepo habitFactRepo;
    @Mock
    ModelMapper modelMapper;
    @Mock
    HabitRepo habitRepo;

    @InjectMocks
    HabitFactServiceImpl habitFactService;

    @Test
    void getAllHabitFactsTest_shouldReturnCorrectValue() {
        String language = "en";
        Pageable pageable = PageRequest.of(0, 5);
        LanguageTranslationDTO languageTranslationDTO = ModelUtils.getLanguageTranslationDTO();
        List<HabitFactTranslation> habitFactTranslation =
            Collections.singletonList(ModelUtils.getHabitFactTranslation());
        List<LanguageTranslationDTO> languageTranslationDTOS = Collections.singletonList(languageTranslationDTO);
        PageableDto<LanguageTranslationDTO> pageableDto =
            new PageableDto<>(languageTranslationDTOS, languageTranslationDTOS.size(), 0, 1);
        Page<HabitFactTranslation> pageFacts = new PageImpl<>(habitFactTranslation,
            pageable, habitFactTranslation.size());
        when(modelMapper.map(habitFactTranslation.get(0), LanguageTranslationDTO.class))
            .thenReturn(languageTranslationDTO);
        when(habitFactTranslationRepo.findAllByLanguageCode(pageable, language))
            .thenReturn(pageFacts);

        PageableDto<LanguageTranslationDTO> actual = habitFactService.getAllHabitFacts(pageable, language);
        assertEquals(pageableDto, actual);
    }

    @Test
    void getRandomHabitFactByHabitIdAndLanguageTest_shouldReturnCorrectValue() {
        Long id = 1L;
        String language = "ua";
        HabitFactTranslation habitFactTranslation = ModelUtils.getHabitFactTranslation();
        LanguageTranslationDTO languageTranslationDTO = ModelUtils.getLanguageTranslationDTO();
        when(habitFactTranslationRepo.getRandomHabitFactTranslationByHabitIdAndLanguage(language, id))
            .thenReturn(Optional.of(habitFactTranslation));
        when(modelMapper.map(habitFactTranslation, LanguageTranslationDTO.class)).thenReturn(languageTranslationDTO);

        LanguageTranslationDTO actual = habitFactService.getRandomHabitFactByHabitIdAndLanguage(id, language);

        assertEquals(languageTranslationDTO, actual);
    }

    @Test
    void getRandomHabitFactByHabitIdAndLanguageTest_shouldThrowExceptionWhenWrongId() {
        Long id = 1L;
        String language = "ua";
        when(habitFactTranslationRepo.getRandomHabitFactTranslationByHabitIdAndLanguage(language, id))
            .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class,
            () -> habitFactService.getRandomHabitFactByHabitIdAndLanguage(id, language));
    }

    @Test
    void getHabitFactByIdTest_shouldReturnCorrectValue() {
        Long id = 1L;
        HabitFact habitFact = ModelUtils.getHabitFact();
        HabitFactVO habitFactVO = ModelUtils.getHabitFactVO();
        HabitFactDtoResponse habitFactDtoResponse = new HabitFactDtoResponse();
        when(habitFactRepo.findById(id)).thenReturn(Optional.of(habitFact));
        when(modelMapper.map(habitFact, HabitFactVO.class)).thenReturn(habitFactVO);
        when(modelMapper.map(habitFactVO, HabitFactDtoResponse.class)).thenReturn(habitFactDtoResponse);

        HabitFactDtoResponse actual = habitFactService.getHabitFactById(id);

        assertEquals(habitFactDtoResponse, actual);
    }

    @Test
    void getHabitFactByIdTest_shouldThrowExceptionWhenWrongId() {
        Long id = 1L;
        when(habitFactRepo.findById(id)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> habitFactService.getHabitFactById(id));
    }

    @Test
    void getHabitFactByNameTest_shouldReturnCorrectValue() {
        String language = "ua";
        String name = "name";
        HabitFactTranslation habitFactTranslation = ModelUtils.getHabitFactTranslation();
        HabitFactDto habitFactDto = ModelUtils.getHabitFactDto();
        when(habitFactTranslationRepo.findFactTranslationByLanguageCodeAndContent(language, name))
            .thenReturn(Optional.of(habitFactTranslation));
        when(modelMapper.map(habitFactTranslation, HabitFactDto.class)).thenReturn(habitFactDto);

        HabitFactDto actual = habitFactService.getHabitFactByName(language, name);

        assertEquals(habitFactDto, actual);
    }

    @Test
    void getHabitFactByNameTest_shouldThrowExceptionWhenWrongId() {
        String language = "ua";
        String name = "name";
        when(habitFactTranslationRepo.findFactTranslationByLanguageCodeAndContent(language, name))
            .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> habitFactService.getHabitFactByName(language, name));
    }

    @Test
    void saveTest_shouldReturnCorrectValue() {
        HabitFact habitFact = ModelUtils.getHabitFact();
        HabitFactVO habitFactVO = ModelUtils.getHabitFactVO();
        HabitFactPostDto habitFactPostDto = ModelUtils.getHabitFactPostDto();
        when(modelMapper.map(habitFactPostDto, HabitFact.class)).thenReturn(habitFact);
        when(habitFactRepo.save(habitFact)).thenReturn(habitFact);
        when(modelMapper.map(habitFact, HabitFactVO.class)).thenReturn(habitFactVO);

        HabitFactVO actual = habitFactService.save(habitFactPostDto);

        assertEquals(habitFactVO, actual);
    }

    @Test
    void updateTest_shouldReturnCorrectValue() {
        Long id = 1L;
        Habit habit = Habit.builder()
            .id(1L)
            .habitAssigns(Collections.singletonList(ModelUtils.getHabitAssign()))
            .habitTranslations(null)
            .image("")
            .build();
        HabitFact habitFact = HabitFact.builder()
            .id(1L)
            .habit(habit)
            .translations(Collections.singletonList(ModelUtils.getHabitFactTranslation()))
            .build();
        Language defaultLanguage = ModelUtils.getLanguage();
        HabitFactPostDto habitFactPostDto = ModelUtils.getHabitFactPostDto();
        Long id2 = habitFactPostDto.getHabit().getId();
        when(habitFactRepo.findById(id)).thenReturn(Optional.of(habitFact));
        when(habitRepo.findById(id2)).thenReturn(Optional.of(habit));
        habitFact.setHabit(habit);
        when(modelMapper.map(any(LanguageDTO.class), eq(Language.class))).thenReturn(defaultLanguage);
        when(habitFactRepo.save(habitFact)).thenReturn(habitFact);
        HabitFactVO expected = modelMapper.map(habitFact, HabitFactVO.class);
        when(modelMapper.map(habitFact, HabitFactVO.class)).thenReturn(expected);
        HabitFactVO actual = habitFactService.update(habitFactPostDto, id);

        assertEquals(expected, actual);
    }

    @Test
    void updateTest_shouldThrowNotUpdatedException() {
        Long id = 1L;
        HabitFactPostDto habitFactPostDto = ModelUtils.getHabitFactPostDto();

        assertThrows(NotUpdatedException.class, () -> habitFactService.update(habitFactPostDto, id));
    }

    @Test
    void deleteTest_shouldReturnCorrectValue() {
        Long id = 1L;
        HabitFact habitFact = ModelUtils.getHabitFact();
        when(habitFactRepo.findById(id)).thenReturn(Optional.of(habitFact));
        Long actual = habitFactService.delete(id);

        verify(habitFactRepo).deleteById(id);
        assertEquals(id, actual);
    }

    @Test
    void deleteTest_shouldThrowNotDeletedException() {
        Long id = 1L;
        when(habitFactRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotDeletedException.class, () -> habitFactService.delete(id));
    }

    @Test
    void deleteAllByHabitTest_shouldDeleteAll() {
        HabitFact habitFact = ModelUtils.getHabitFact();
        HabitVO habitVO = ModelUtils.getHabitVO();
        when(habitFactRepo.findAllByHabitId(habitVO.getId())).thenReturn(Collections.singletonList(habitFact));
        habitFactService.deleteAllByHabit(habitVO);

        verify(habitFactTranslationRepo, times(1)).deleteAllByHabitFact(habitFact);
    }
}
