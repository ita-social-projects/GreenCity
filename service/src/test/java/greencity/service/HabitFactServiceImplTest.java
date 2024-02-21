package greencity.service;

import greencity.ModelUtils;
import greencity.dto.PageableDto;
import greencity.dto.habit.HabitVO;
import greencity.dto.habitfact.HabitFactDto;
import greencity.dto.habitfact.HabitFactDtoResponse;
import greencity.dto.habitfact.HabitFactPostDto;
import greencity.dto.habitfact.HabitFactUpdateDto;
import greencity.dto.habitfact.HabitFactVO;
import greencity.dto.habitfact.HabitFactViewDto;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.entity.Habit;
import greencity.entity.HabitFact;
import greencity.entity.HabitFactTranslation;
import greencity.enums.FactOfDayStatus;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotUpdatedException;
import greencity.filters.HabitFactSpecification;
import greencity.repository.HabitFactRepo;
import greencity.repository.HabitFactTranslationRepo;
import greencity.repository.HabitRepo;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static greencity.ModelUtils.getFactTranslation;
import static greencity.ModelUtils.getLanguageTranslationDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    private static Stream<Arguments> getFilters() {
        return Stream.of(Arguments.of(""),
            Arguments.of((Object) null));
    }

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
        when(modelMapper.map(habitFactTranslation.getFirst(), LanguageTranslationDTO.class))
            .thenReturn(languageTranslationDTO);
        when(habitFactTranslationRepo.findAllByLanguageCode(pageable, language))
            .thenReturn(pageFacts);

        PageableDto<LanguageTranslationDTO> actual = habitFactService.getAllHabitFacts(pageable, language);
        assertEquals(pageableDto, actual);
    }

    @Test
    void getAllHabitFactsTest_shouldThrowException() {
        String language = "en";
        Pageable pageable = PageRequest.of(5, 5);
        List<HabitFactTranslation> habitFactTranslation =
            Collections.singletonList(ModelUtils.getHabitFactTranslation());
        Page<HabitFactTranslation> pageFacts = new PageImpl<>(habitFactTranslation);
        when(habitFactTranslationRepo.findAllByLanguageCode(pageable, language))
            .thenReturn(pageFacts);

        assertThrows(BadRequestException.class, () -> habitFactService.getAllHabitFacts(pageable, language));
    }

    @Test
    void getAllHabitFactsListTest() {
        String language = "en";
        Pageable pageable = PageRequest.of(5, 5);
        List<HabitFactTranslation> habitFactTranslation =
            Collections.singletonList(ModelUtils.getHabitFactTranslation());
        Page<HabitFactTranslation> pageFacts = new PageImpl<>(habitFactTranslation);
        when(habitFactTranslationRepo.findAllByLanguageCode(pageable, language))
            .thenReturn(pageFacts);
        assertThrows(BadRequestException.class, () -> habitFactService.getAllHabitFactsList(pageable, language));
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
    void getAllHabitFactsVOTest_shouldReturnCorrectValue() {
        int pageNumber = 0;
        int pageSize = 1;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        HabitFact habitFact = ModelUtils.getHabitFact();
        HabitFactVO habitFactVO = ModelUtils.getHabitFactVO();
        List<HabitFact> habitFacts = Collections.singletonList(habitFact);
        List<HabitFactVO> habitFactVOS = Collections.singletonList(habitFactVO);
        Page<HabitFact> pageAdvices = new PageImpl<>(habitFacts,
            pageable, habitFacts.size());
        PageableDto<HabitFactVO> expected = new PageableDto<>(habitFactVOS, habitFacts.size(), pageNumber, pageSize);
        when(habitFactRepo.findAll(pageable)).thenReturn(pageAdvices);
        when(modelMapper.map(habitFact, HabitFactVO.class)).thenReturn(habitFactVO);
        PageableDto<HabitFactVO> actual = habitFactService.getAllHabitFactsVO(pageable);

        assertEquals(expected, actual);
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
        habitFact.setHabit(ModelUtils.getHabit());
        HabitFactVO habitFactVO = ModelUtils.getHabitFactVO();
        habitFactVO.setHabit(ModelUtils.getHabitVO());
        HabitFactPostDto habitFactPostDto = ModelUtils.getHabitFactPostDto();
        when(modelMapper.map(habitFactPostDto, HabitFact.class)).thenReturn(habitFact);
        when(habitFactRepo.save(habitFact)).thenReturn(habitFact);
        when(modelMapper.map(habitFact, HabitFactVO.class)).thenReturn(habitFactVO);
        when(habitRepo.findById(habitFactVO.getHabit().getId())).thenReturn(Optional.of(habitFact.getHabit()));

        HabitFactVO actual = habitFactService.save(habitFactPostDto);

        assertEquals(habitFactVO, actual);
    }

    @Test
    void saveTest_shouldThrowException() {
        HabitFactPostDto habitFactPostDto = ModelUtils.getHabitFactPostDto();
        when(habitRepo.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> habitFactService.save(habitFactPostDto));
    }

    @Test
    void updateTest_shouldReturnCorrectValue() {
        Long id = 1L;
        Habit habit = ModelUtils.getHabit();
        HabitFact habitFact = ModelUtils.getHabitFact();
        HabitFactUpdateDto habitFactUpdateDto = ModelUtils.getHabitFactUpdateDto();
        when(habitFactRepo.findById(id)).thenReturn(Optional.of(habitFact));
        when(habitRepo.findById(id)).thenReturn(Optional.of(habit));
        habitFact.setHabit(habit);
        when(habitFactRepo.save(habitFact)).thenReturn(habitFact);
        HabitFactVO expected = modelMapper.map(habitFact, HabitFactVO.class);
        when(modelMapper.map(habitFact, HabitFactVO.class)).thenReturn(expected);
        HabitFactVO actual = habitFactService.update(habitFactUpdateDto, id);

        assertEquals(expected, actual);
    }

    @Test
    void updateTest_shouldThrowNotUpdatedException() {
        Long id = 1L;
        HabitFactUpdateDto habitFactUpdateDto = ModelUtils.getHabitFactUpdateDto();

        assertThrows(NotUpdatedException.class, () -> habitFactService.update(habitFactUpdateDto, id));
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

    @Test
    void deleteAllHabitFactsByListOfIdTest_shouldReturnCorrectValue() {
        List<Long> ids = List.of(1L, 2L, 3L);
        HabitFact habitFact = ModelUtils.getHabitFact();
        when(habitFactRepo.findById(anyLong())).thenReturn(Optional.of(habitFact));
        List<Long> actual = habitFactService.deleteAllHabitFactsByListOfId(ids);

        assertEquals(ids, actual);
        verify(habitFactRepo, times(ids.size())).delete(habitFact);
    }

    @Test
    void searchByTest_shouldReturnCorrectValueWithQuery() {
        int pageNumber = 0;
        int pageSize = 1;
        String query = "eng";
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        HabitFact habitFact = ModelUtils.getHabitFact();
        HabitFactVO habitFactVO = ModelUtils.getHabitFactVO();
        List<HabitFact> habitFacts = Collections.singletonList(habitFact);
        List<HabitFactVO> habitFactVOS = Collections.singletonList(habitFactVO);
        Page<HabitFact> habitFactPage = new PageImpl<>(habitFacts,
            pageable, habitFacts.size());
        PageableDto<HabitFactVO> expected = new PageableDto<>(habitFactVOS, habitFacts.size(), pageNumber, pageSize);
        when(habitFactRepo.searchHabitFactByFilter(pageable, query)).thenReturn(habitFactPage);
        when(modelMapper.map(habitFact, HabitFactVO.class)).thenReturn(habitFactVO);
        PageableDto<HabitFactVO> actual = habitFactService.getAllHabitFactVOsWithFilter(query, pageable);

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource(value = "getFilters")
    void searchByTest_shouldReturnCorrectValue(String param) {
        int pageNumber = 0;
        int pageSize = 1;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        HabitFact habitFact = ModelUtils.getHabitFact();
        HabitFactVO habitFactVO = ModelUtils.getHabitFactVO();
        List<HabitFact> habitFacts = Collections.singletonList(habitFact);
        List<HabitFactVO> habitFactVOS = Collections.singletonList(habitFactVO);
        Page<HabitFact> habitFactPage = new PageImpl<>(habitFacts,
            pageable, habitFacts.size());
        PageableDto<HabitFactVO> expected = new PageableDto<>(habitFactVOS, habitFacts.size(), pageNumber, pageSize);
        when(habitFactRepo.findAll(pageable)).thenReturn(habitFactPage);
        when(modelMapper.map(habitFact, HabitFactVO.class)).thenReturn(habitFactVO);
        PageableDto<HabitFactVO> actual = habitFactService.getAllHabitFactVOsWithFilter(param, pageable);

        assertEquals(expected, actual);
    }

    @Test
    void getFilteredDataForManagementByPage_shouldReturnCorrectValue() {
        int pageNumber = 0;
        int pageSize = 1;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        HabitFact habitFact = ModelUtils.getHabitFact();
        HabitFactVO habitFactVO = ModelUtils.getHabitFactVO();
        List<HabitFact> habitFacts = Collections.singletonList(habitFact);
        List<HabitFactVO> habitFactVOS = Collections.singletonList(habitFactVO);
        HabitFactViewDto habitFactViewDto = new HabitFactViewDto("1", "1", "eng");
        Page<HabitFact> habitFactPage = new PageImpl<>(habitFacts,
            pageable, habitFacts.size());
        when(habitFactRepo.findAll(any(HabitFactSpecification.class), eq(pageable))).thenReturn(habitFactPage);
        when(modelMapper.map(habitFact, HabitFactVO.class)).thenReturn(habitFactVO);
        PageableDto<HabitFactVO> expected = new PageableDto<>(habitFactVOS, habitFacts.size(), pageNumber, pageSize);
        PageableDto<HabitFactVO> actual = habitFactService
            .getFilteredDataForManagementByPage(pageable, habitFactViewDto);

        assertEquals(expected, actual);
    }

    @Test
    void getFilteredDataForManagementByPage_wthEmptyValue() {
        int pageNumber = 0;
        int pageSize = 1;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        HabitFact habitFact = ModelUtils.getHabitFact();
        HabitFactVO habitFactVO = ModelUtils.getHabitFactVO();
        List<HabitFact> habitFacts = Collections.singletonList(habitFact);
        List<HabitFactVO> habitFactVOS = Collections.singletonList(habitFactVO);
        HabitFactViewDto habitFactViewDto = new HabitFactViewDto("", "", "");
        Page<HabitFact> habitFactPage = new PageImpl<>(habitFacts,
            pageable, habitFacts.size());
        when(habitFactRepo.findAll(any(HabitFactSpecification.class), eq(pageable))).thenReturn(habitFactPage);
        when(modelMapper.map(habitFact, HabitFactVO.class)).thenReturn(habitFactVO);
        PageableDto<HabitFactVO> expected = new PageableDto<>(habitFactVOS, habitFacts.size(), pageNumber, pageSize);
        PageableDto<HabitFactVO> actual = habitFactService
            .getFilteredDataForManagementByPage(pageable, habitFactViewDto);

        assertEquals(expected, actual);
    }

    @Test
    void getHabitFactOfTheDay() {
        HabitFactTranslation res = getFactTranslation();
        when(habitFactTranslationRepo.findAllByFactOfDayStatusAndLanguageId(FactOfDayStatus.CURRENT, 1L))
            .thenReturn(res);
        when(modelMapper.map(res, LanguageTranslationDTO.class)).thenReturn(getLanguageTranslationDTO());
        assertEquals(getLanguageTranslationDTO(), habitFactService.getHabitFactOfTheDay(1L));
    }
}
