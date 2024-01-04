package greencity.service;

import greencity.ModelUtils;
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
import greencity.filters.AdviceSpecification;
import greencity.repository.AdviceRepo;
import greencity.repository.AdviceTranslationRepo;
import greencity.repository.HabitRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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

    @Test
    void getAllAdvices() {
        int pageNumber = 0;
        int pageSize = 1;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Advice advice = ModelUtils.getAdvice();
        AdviceVO adviceVO = ModelUtils.getAdviceVO();
        List<Advice> advices = Collections.singletonList(advice);
        List<AdviceVO> adviceVOs = Collections.singletonList(adviceVO);
        Page<Advice> pageAdvices = new PageImpl<>(advices,
            pageable, advices.size());
        PageableDto<AdviceVO> expected = new PageableDto<>(adviceVOs, advices.size(), pageNumber, pageSize);
        when(adviceRepo.findAll(pageable)).thenReturn(pageAdvices);
        when(modelMapper.map(advice, AdviceVO.class)).thenReturn(adviceVO);
        PageableDto<AdviceVO> actual = adviceService.getAllAdvices(pageable);

        assertEquals(expected, actual);
    }

    @Test
    void getFilteredAdvices() {
        int pageNumber = 0;
        int pageSize = 1;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Advice advice = ModelUtils.getAdvice();
        AdviceVO adviceVO = ModelUtils.getAdviceVO();
        AdviceViewDto adviceViewDto = new AdviceViewDto("1", "", "Pro");
        List<Advice> advices = Collections.singletonList(advice);
        List<AdviceVO> adviceVOs = Collections.singletonList(adviceVO);
        Page<Advice> pageAdvices = new PageImpl<>(advices,
            pageable, advices.size());
        when(adviceRepo.findAll(any(AdviceSpecification.class), eq(pageable))).thenReturn(pageAdvices);
        when(modelMapper.map(advice, AdviceVO.class)).thenReturn(adviceVO);
        PageableDto<AdviceVO> expected = new PageableDto<>(adviceVOs, advices.size(), pageNumber, pageSize);
        PageableDto<AdviceVO> actual = adviceService.getFilteredAdvices(pageable, adviceViewDto);

        assertEquals(expected, actual);
    }

    @Test
    void getRandomAdviceByHabitIdAndLanguage() {
        String language = "en";
        Long id = 1L;
        AdviceTranslation adviceTranslation = ModelUtils.getAdviceTranslations().get(0);
        LanguageTranslationDTO expected = ModelUtils.getLanguageTranslationsDTOs().get(0);
        when(adviceTranslationRepo.getRandomAdviceTranslationByHabitIdAndLanguage(language, id))
            .thenReturn(Optional.of(adviceTranslation));
        when(modelMapper.map(adviceTranslation, LanguageTranslationDTO.class)).thenReturn(expected);
        LanguageTranslationDTO actual = adviceService.getRandomAdviceByHabitIdAndLanguage(id, language);

        assertEquals(expected, actual);
    }

    @Test
    void getAllByHabitIdAndLanguageTest() {
        String language = "en";
        Long id = 1L;
        List<LanguageTranslationDTO> expected = ModelUtils.getLanguageTranslationsDTOs();
        List<AdviceTranslation> adviceTranslationList = ModelUtils.getAdviceTranslations();
        when(adviceTranslationRepo.getAllByHabitIdAndLanguageCode(id, language)).thenReturn(adviceTranslationList);

        when(modelMapper.map(adviceTranslationList.getFirst(), LanguageTranslationDTO.class))
            .thenReturn(expected.get(0));
        when(modelMapper.map(adviceTranslationList.get(1), LanguageTranslationDTO.class)).thenReturn(expected.get(1));
        when(modelMapper.map(adviceTranslationList.get(2), LanguageTranslationDTO.class)).thenReturn(expected.get(2));

        List<LanguageTranslationDTO> actual = adviceService.getAllByHabitIdAndLanguage(id, language);
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
        Advice advice = ModelUtils.getAdvice();
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
        AdviceTranslation adviceTranslation = ModelUtils.getAdviceTranslations().get(0);
        Advice advice = ModelUtils.getAdvice();
        AdviceDto expected = modelMapper.map(advice, AdviceDto.class);
        when(adviceTranslationRepo.findAdviceTranslationByLanguageCodeAndContent(language, name))
            .thenReturn(Optional.of(adviceTranslation));
        when(modelMapper.map(adviceTranslation, AdviceDto.class)).thenReturn(expected);
        AdviceDto actual = adviceService.getAdviceByName(language, name);

        assertEquals(expected, actual);
    }

    @Test
    void filterByAllFields() {
        int pageNumber = 0;
        int pageSize = 1;
        String query = "Pro";
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Advice advice = ModelUtils.getAdvice();
        AdviceVO adviceVO = ModelUtils.getAdviceVO();
        List<Advice> advices = Collections.singletonList(advice);
        List<AdviceVO> adviceVOs = Collections.singletonList(adviceVO);
        Page<Advice> pageAdvices = new PageImpl<>(advices,
            pageable, advices.size());
        PageableDto<AdviceVO> expected = new PageableDto<>(adviceVOs, advices.size(), pageNumber, pageSize);
        when(adviceRepo.filterByAllFields(pageable, query)).thenReturn(pageAdvices);
        when(modelMapper.map(advice, AdviceVO.class)).thenReturn(adviceVO);
        PageableDto<AdviceVO> actual = adviceService.getAllAdvicesWithFilter(pageable, query);

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
        AdvicePostDto advicePostDto = ModelUtils.getAdvicePostDto();
        Advice advice = ModelUtils.getAdvice();
        when(modelMapper.map(advicePostDto, Advice.class)).thenReturn(advice);
        when(adviceRepo.save(advice)).thenReturn(advice);
        AdviceVO expected = modelMapper.map(advice, AdviceVO.class);
        when(modelMapper.map(advice, AdviceVO.class)).thenReturn(expected);
        AdviceVO actual = adviceService.save(advicePostDto);

        assertEquals(expected, actual);
    }

    @Test
    void update() {
        AdvicePostDto advicePostDto = ModelUtils.getAdvicePostDto();
        Advice advice = ModelUtils.getAdvice();
        Habit habit = ModelUtils.getHabit();
        Long adviceId = 1L;
        Long habitId = advicePostDto.getHabit().getId();

        when(adviceRepo.findById(adviceId)).thenReturn(Optional.of(advice));
        when(habitRepo.findById(habitId)).thenReturn(Optional.of(habit));
        advice.setHabit(habit);
        when(adviceRepo.save(advice)).thenReturn(advice);
        AdvicePostDto expected = modelMapper.map(advice, AdvicePostDto.class);
        when(modelMapper.map(advice, AdvicePostDto.class)).thenReturn(expected);
        AdvicePostDto actual = adviceService.update(advicePostDto, adviceId);

        assertEquals(expected, actual);
    }

    @Test
    void updateThrowNotFoundException() {
        Long adviceId = 1L;
        AdvicePostDto advicePostDto = ModelUtils.getAdvicePostDto();

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
        Habit habit = ModelUtils.getHabit();
        HabitVO habitVO = ModelUtils.getHabitVO();
        List<Advice> advices = ModelUtils.getAdvices();
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
        Habit habit = ModelUtils.getHabit();
        HabitVO habitVO = ModelUtils.getHabitVO();
        Long habitId = habit.getId();
        when(modelMapper.map(habitVO, Habit.class)).thenReturn(habit);
        when(adviceRepo.findAllByHabitId(habitId)).thenReturn(Collections.emptyList());
        adviceService.deleteAllByHabit(habitVO);

        verify(adviceTranslationRepo, never()).deleteAllByAdvice(any(Advice.class));
        verify(adviceRepo, never()).delete(any(Advice.class));
    }

    @Test
    void deleteAllByIds() {
        List<Long> ids = List.of(1L, 2L, 3L);
        adviceService.deleteAllByIds(ids);

        verify(adviceRepo, times(ids.size())).deleteById(anyLong());
    }
}