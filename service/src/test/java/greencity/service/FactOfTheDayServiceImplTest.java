package greencity.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import greencity.ModelUtils;
import greencity.dto.PageableDto;
import greencity.dto.factoftheday.*;
import greencity.entity.FactOfTheDay;
import greencity.entity.FactOfTheDayTranslation;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotUpdatedException;
import greencity.repository.FactOfTheDayRepo;
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
class FactOfTheDayServiceImplTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private FactOfTheDayTranslationServiceImpl factOfTheDayTranslationService;

    @Mock
    private FactOfTheDayRepo factOfTheDayRepo;

    @Mock
    private LanguageService languageService;

    @Mock
    private FactOfTheDayService service;

    @InjectMocks
    private FactOfTheDayServiceImpl factOfTheDayService;

    @Test
    void findByIdTest() {
        FactOfTheDayDTO factDto = ModelUtils.getFactOfTheDayDto();
        FactOfTheDay fact = ModelUtils.getFactOfTheDay();

        when(factOfTheDayRepo.findById(anyLong())).thenReturn(Optional.of(fact));
        when(modelMapper.map(fact, FactOfTheDayDTO.class)).thenReturn(factDto);
        FactOfTheDayDTO factDtoRes = factOfTheDayService.getFactOfTheDayById(1L);

        assertEquals(Long.valueOf(1), factDtoRes.getId());
    }

    @Test
    void findByIdTestFailed() {
        when(factOfTheDayRepo.findById(anyLong())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> factOfTheDayService.getFactOfTheDayById(1L));
    }

    @Test
    void findAllTest() {
        int pageNumber = 0;
        int pageSize = 1;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        List<FactOfTheDay> factsOfTheDays = Collections.singletonList(ModelUtils.getFactOfTheDay());

        Page<FactOfTheDay> pageFacts = new PageImpl<>(factsOfTheDays,
            pageable, factsOfTheDays.size());

        List<FactOfTheDayDTO> dtoList = Collections.singletonList(
            ModelUtils.getFactOfTheDayDto());

        PageableDto<FactOfTheDayDTO> pageableDto = new PageableDto<>(dtoList, dtoList.size(), 0, 1);

        when(factOfTheDayRepo.findAll(pageable)).thenReturn(pageFacts);
        when(modelMapper.map(factsOfTheDays.get(0), FactOfTheDayDTO.class)).thenReturn(dtoList.get(0));

        PageableDto<FactOfTheDayDTO> actual = factOfTheDayService.getAllFactsOfTheDay(pageable);
        assertEquals(pageableDto, actual);
    }

    @Test
    void updateTest() {
        FactOfTheDay fact = ModelUtils.getFactOfTheDay();
        FactOfTheDayPostDTO factDtoPost = ModelUtils.getFactOfTheDayPostDto();
        FactOfTheDayVO factOfTheDayVO = ModelUtils.getFactOfTheDayVO();

        when(factOfTheDayRepo.findById(anyLong())).thenReturn(Optional.of(fact));
        when(factOfTheDayRepo.save(fact)).thenReturn(fact);
        when(modelMapper.map(fact, FactOfTheDayVO.class)).thenReturn(factOfTheDayVO);

        assertEquals(factOfTheDayVO, factOfTheDayService.update(factDtoPost));
    }

    @Test
    void updateTestFailed() {
        FactOfTheDayPostDTO factDtoPost = ModelUtils.getFactOfTheDayPostDto();
        when(factOfTheDayRepo.findById(anyLong())).thenThrow(NotUpdatedException.class);

        assertThrows(NotUpdatedException.class, () -> factOfTheDayService.update(factDtoPost));
    }

    @Test
    void saveFactOfTheDayWithTranslationsTest() {
        FactOfTheDayPostDTO factDtoPost = ModelUtils.getFactOfTheDayPostDto();

        FactOfTheDayPostDTO res = factOfTheDayService.saveFactOfTheDayAndTranslations(factDtoPost);
        verify(factOfTheDayRepo, times(1)).save(any(FactOfTheDay.class));
        verify(factOfTheDayTranslationService, times(1)).saveAll(anyList());

        assertEquals(factDtoPost, res);
    }

    @Test
    void updateFactOfTheDayWithTranslationsTest() {
        FactOfTheDay fact = ModelUtils.getFactOfTheDay();
        FactOfTheDayVO factOfTheDayVO = ModelUtils.getFactOfTheDayVO();
        FactOfTheDayTranslation factOfTheDayTranslation = ModelUtils.getFactOfTheDayTranslation();
        FactOfTheDayTranslationVO factOfTheDayTranslationVO = ModelUtils.getFactOfTheDayTranslationVO();
        FactOfTheDayPostDTO factDtoPost = ModelUtils.getFactOfTheDayPostDto();

        when(factOfTheDayRepo.findById(anyLong())).thenReturn(Optional.of(fact));
        when(modelMapper.map(factOfTheDayTranslation, FactOfTheDayTranslationVO.class)).thenReturn(factOfTheDayTranslationVO);
        FactOfTheDayPostDTO res = factOfTheDayService.updateFactOfTheDayAndTranslations(factDtoPost);

        verify(factOfTheDayTranslationService, times(1)).deleteAll(factOfTheDayVO.getFactOfTheDayTranslations());
        verify(factOfTheDayRepo, times(1)).save(any(FactOfTheDay.class));
        verify(factOfTheDayTranslationService, times(1)).saveAll(anyList());

        assertEquals(factDtoPost, res);
    }

    @Test
    void updateFactOfTheDayWithTranslationsTestFailed() {
        FactOfTheDayPostDTO factDtoPost = ModelUtils.getFactOfTheDayPostDto();
        when(factOfTheDayRepo.findById(anyLong())).thenThrow(NotUpdatedException.class);

        assertThrows(NotUpdatedException.class, () -> factOfTheDayService.updateFactOfTheDayAndTranslations(factDtoPost));
    }

    @Test
    void deleteFactOfTheDayAndTranslationsTest() {
        FactOfTheDay fact = ModelUtils.getFactOfTheDay();
        FactOfTheDayVO factOfTheDayVO = ModelUtils.getFactOfTheDayVO();
        FactOfTheDayTranslation factOfTheDayTranslation = ModelUtils.getFactOfTheDayTranslation();
        FactOfTheDayTranslationVO factOfTheDayTranslationVO = ModelUtils.getFactOfTheDayTranslationVO();
        when(factOfTheDayRepo.findById(anyLong())).thenReturn(Optional.of(fact));
        when(modelMapper.map(factOfTheDayTranslation, FactOfTheDayTranslationVO.class)).thenReturn(factOfTheDayTranslationVO);
        assertEquals(factOfTheDayVO.getId(), factOfTheDayService.deleteFactOfTheDayAndTranslations(1L));

        verify(factOfTheDayRepo, times(1)).deleteById(anyLong());
        verify(factOfTheDayTranslationService, times(1)).deleteAll(factOfTheDayVO.getFactOfTheDayTranslations());
    }

    @Test
    void deleteFactOfTheDayWithTranslationsTestFailed() {
        when(factOfTheDayRepo.findById(anyLong())).thenThrow(NotUpdatedException.class);
        assertThrows(NotUpdatedException.class, () -> factOfTheDayService.deleteFactOfTheDayAndTranslations(1L));
    }

    @Test
    void searchByTest() {
        int pageNumber = 0;
        int pageSize = 1;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<FactOfTheDay> factsOfTheDays = Collections.singletonList(ModelUtils.getFactOfTheDay());

        Page<FactOfTheDay> pageFacts = new PageImpl<>(factsOfTheDays,
            pageable, factsOfTheDays.size());

        List<FactOfTheDayDTO> dtoList = Collections.singletonList(
            ModelUtils.getFactOfTheDayDto());

        PageableDto<FactOfTheDayDTO> pageableDto = new PageableDto<>(dtoList, dtoList.size(), 0, 1);

        when(factOfTheDayRepo.searchBy(pageable, "query")).thenReturn(pageFacts);
        when(modelMapper.map(factsOfTheDays.get(0), FactOfTheDayDTO.class)).thenReturn(dtoList.get(0));

        PageableDto<FactOfTheDayDTO> actual = factOfTheDayService.searchBy(pageable, "query");
        assertEquals(pageableDto, actual);
    }

    @Test
    void deleteAllFactOfTheDayAndTranslationsTest() {
        List<Long> listId = List.of(1L, 2L, 3L);
        FactOfTheDay fact = ModelUtils.getFactOfTheDay();
        FactOfTheDayVO factOfTheDayVO = ModelUtils.getFactOfTheDayVO();
        FactOfTheDayTranslation factOfTheDayTranslation = ModelUtils.getFactOfTheDayTranslation();
        FactOfTheDayTranslationVO factOfTheDayTranslationVO = ModelUtils.getFactOfTheDayTranslationVO();
        when(factOfTheDayRepo.findById(anyLong())).thenReturn(Optional.of(fact));
        when(modelMapper.map(factOfTheDayTranslation, FactOfTheDayTranslationVO.class)).thenReturn(factOfTheDayTranslationVO);
        assertEquals(listId, factOfTheDayService.deleteAllFactOfTheDayAndTranslations(listId));

        verify(factOfTheDayRepo, times(3)).deleteById(anyLong());
        verify(factOfTheDayTranslationService, times(3)).deleteAll(factOfTheDayVO.getFactOfTheDayTranslations());
    }

    @Test
    void deleteAllFactOfTheDayAndTranslationsTestFailed() {
        when(factOfTheDayRepo.findById(anyLong())).thenThrow(NotUpdatedException.class);
        assertThrows(NotUpdatedException.class, () -> factOfTheDayService.deleteAllFactOfTheDayAndTranslations(List.of(anyLong())));
    }

    @Test
    void getRandomFactOfTheDayTest() {
        FactOfTheDayVO factOfTheDayVO = ModelUtils.getFactOfTheDayVO();
        FactOfTheDay fact = ModelUtils.getFactOfTheDay();
        when(factOfTheDayRepo.getRandomFactOfTheDay()).thenReturn(Optional.of(fact));
        when(modelMapper.map(fact, FactOfTheDayVO.class)).thenReturn(factOfTheDayVO);
        FactOfTheDayVO actual = factOfTheDayService.getRandomFactOfTheDay();

        assertEquals(factOfTheDayVO, actual);

        verify(factOfTheDayRepo, times(1)).getRandomFactOfTheDay();
    }

    @Test
    void getRandomFactOfTheDayTestFailed() {
        when(factOfTheDayRepo.getRandomFactOfTheDay()).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> factOfTheDayService.getRandomFactOfTheDay());
    }

    @Test
    void getRandomFactOfTheDayByLanguageTest() {
        FactOfTheDay fact = ModelUtils.getFactOfTheDay();
        FactOfTheDayVO factOfTheDayVO = ModelUtils.getFactOfTheDayVO();
        FactOfTheDayTranslation factOfTheDayTranslation = ModelUtils.getFactOfTheDayTranslation();
        FactOfTheDayTranslationDTO factOfTheDayTranslationDTO = ModelUtils.getFactOfTheDayTranslationDTO();

        when(service.getRandomFactOfTheDay()).thenReturn(factOfTheDayVO);
        when(modelMapper.map(factOfTheDayVO, FactOfTheDay.class)).thenReturn(fact);
        when(modelMapper.map(factOfTheDayTranslation, FactOfTheDayTranslationDTO.class)).thenReturn(factOfTheDayTranslationDTO);

        FactOfTheDayTranslationDTO actual = factOfTheDayService.getRandomFactOfTheDayByLanguage("en");

        assertEquals(factOfTheDayTranslationDTO, actual);

        verify(service, times(1)).getRandomFactOfTheDay();
    }
}
