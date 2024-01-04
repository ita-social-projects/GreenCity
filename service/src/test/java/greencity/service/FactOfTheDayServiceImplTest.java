package greencity.service;

import greencity.ModelUtils;
import greencity.dto.PageableDto;
import greencity.dto.factoftheday.FactOfTheDayDTO;
import greencity.dto.factoftheday.FactOfTheDayPostDTO;
import greencity.dto.factoftheday.FactOfTheDayTranslationDTO;
import greencity.dto.factoftheday.FactOfTheDayTranslationVO;
import greencity.dto.factoftheday.FactOfTheDayVO;
import greencity.dto.language.LanguageDTO;
import greencity.entity.FactOfTheDay;
import greencity.entity.FactOfTheDayTranslation;
import greencity.entity.Language;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotUpdatedException;
import greencity.repository.FactOfTheDayRepo;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
    void getFactOfTheDayByIdTest() {
        FactOfTheDayDTO factDto = ModelUtils.getFactOfTheDayDto();
        FactOfTheDay fact = ModelUtils.getFactOfTheDay();

        when(factOfTheDayRepo.findById(anyLong())).thenReturn(Optional.of(fact));
        when(modelMapper.map(fact, FactOfTheDayDTO.class)).thenReturn(factDto);
        FactOfTheDayDTO factDtoRes = factOfTheDayService.getFactOfTheDayById(1L);

        assertEquals(Long.valueOf(1), factDtoRes.getId());
    }

    @Test
    void getFactOfTheDayByIdTestFailed() {
        when(factOfTheDayRepo.findById(anyLong())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> factOfTheDayService.getFactOfTheDayById(1L));
    }

    @Test
    void getAllFactsOfTheDayTest() {
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
        when(modelMapper.map(factsOfTheDays.getFirst(), FactOfTheDayDTO.class)).thenReturn(dtoList.getFirst());

        PageableDto<FactOfTheDayDTO> actual = factOfTheDayService.getAllFactsOfTheDay(pageable);
        assertEquals(pageableDto, actual);
    }

    @Test
    void getAllFactsOfTheDayFailed() {
        Pageable pageable = PageRequest.of(5, 5);
        when(factOfTheDayRepo.findAll(any(Pageable.class))).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> factOfTheDayService.getAllFactsOfTheDay(pageable));
    }

    @Test
    void updateTest() {
        FactOfTheDay fact = ModelUtils.getFactOfTheDay();
        FactOfTheDayVO factOfTheDayVO = ModelUtils.getFactOfTheDayVO();

        when(factOfTheDayRepo.findById(anyLong())).thenReturn(Optional.of(fact));
        when(factOfTheDayRepo.save(fact)).thenReturn(fact);
        when(modelMapper.map(fact, FactOfTheDayVO.class)).thenReturn(factOfTheDayVO);

        FactOfTheDayPostDTO factDtoPost = ModelUtils.getFactOfTheDayPostDto();

        assertEquals(factOfTheDayVO, factOfTheDayService.update(factDtoPost));
    }

    @Test
    void updateTestFailed() {
        FactOfTheDayPostDTO factDtoPost = ModelUtils.getFactOfTheDayPostDto();
        when(factOfTheDayRepo.findById(anyLong())).thenThrow(NotUpdatedException.class);

        assertThrows(NotUpdatedException.class, () -> factOfTheDayService.update(factDtoPost));
    }

    @Test
    void saveFactOfTheDayAndTranslationsTest() {
        FactOfTheDayPostDTO factDtoPost = ModelUtils.getFactOfTheDayPostDto();

        FactOfTheDayPostDTO res = factOfTheDayService.saveFactOfTheDayAndTranslations(factDtoPost);
        verify(factOfTheDayRepo, times(1)).save(any(FactOfTheDay.class));
        verify(factOfTheDayTranslationService, times(1)).saveAll(anyList());

        assertEquals(factDtoPost, res);
    }

    @Test
    void saveFactOfTheDayAndTranslationsTestFailed() {
        FactOfTheDayPostDTO factDTO = ModelUtils.getFactOfTheDayPostDto();
        FactOfTheDay fact = ModelUtils.getFactOfTheDay();
        when(factOfTheDayRepo.save(fact)).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> factOfTheDayService.saveFactOfTheDayAndTranslations(factDTO));
        verify(factOfTheDayTranslationService, times(0)).saveAll(anyList());
    }

    @Test
    void updateFactOfTheDayAndTranslationsTest() {
        LanguageDTO languageDTO = ModelUtils.getLanguageDTO();
        FactOfTheDay dbFact = ModelUtils.getFactOfTheDay();

        when(factOfTheDayRepo.findById(anyLong())).thenReturn(Optional.of(dbFact));
        when(modelMapper.map(dbFact.getFactOfTheDayTranslations().get(0), FactOfTheDayTranslationVO.class)).thenReturn(
            ModelUtils.getFactOfTheDayTranslationVO());
        when(languageService.findByCode("en")).thenReturn(languageDTO);
        when(modelMapper.map(languageDTO, Language.class)).thenReturn(ModelUtils.getLanguage());
        when(factOfTheDayTranslationService.saveAll(anyList())).thenReturn(null);

        FactOfTheDayPostDTO fact = ModelUtils.getFactOfTheDayPostDto();
        assertEquals(fact, factOfTheDayService.updateFactOfTheDayAndTranslations(fact));
        verify(factOfTheDayRepo, times(1)).findById(anyLong());
        verify(factOfTheDayTranslationService, times(1)).deleteAll(anyList());
        verify(languageService, times(1)).findByCode(anyString());
        verify(modelMapper, times(1)).map(languageDTO, Language.class);
        verify(factOfTheDayRepo, times(1)).save(any(FactOfTheDay.class));
        verify(factOfTheDayTranslationService, times(1)).saveAll(anyList());
        verify(modelMapper, times(1)).map(dbFact.getFactOfTheDayTranslations().get(0), FactOfTheDayTranslationVO.class);
    }

    @Test
    void updateFactOfTheDayAndTranslationsTestFailed() {
        FactOfTheDayPostDTO factDtoPost = ModelUtils.getFactOfTheDayPostDto();
        when(factOfTheDayRepo.findById(anyLong())).thenThrow(NotUpdatedException.class);

        assertThrows(NotUpdatedException.class,
            () -> factOfTheDayService.updateFactOfTheDayAndTranslations(factDtoPost));
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
        when(modelMapper.map(factsOfTheDays.getFirst(), FactOfTheDayDTO.class)).thenReturn(dtoList.getFirst());

        PageableDto<FactOfTheDayDTO> actual = factOfTheDayService.searchBy(pageable, "query");
        assertEquals(pageableDto, actual);
    }

    @Test
    void searchByFailed() {

        int invalidNUmber = 10;
        int invalidSize = 10;
        Pageable pageable = PageRequest.of(invalidNUmber, invalidSize);
        when(factOfTheDayRepo.searchBy(pageable, "invalidQuery")).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> factOfTheDayService.searchBy(pageable, "invalidQuery"));
    }

    @Test
    void deleteAllFactOfTheDayAndTranslationsTest() {
        List<Long> idList = List.of(1L, 2L, 3L);
        FactOfTheDay fact = ModelUtils.getFactOfTheDay();
        FactOfTheDayTranslationVO factOfTheDayTranslationVO = ModelUtils.getFactOfTheDayTranslationVO();
        when(factOfTheDayRepo.findById(anyLong())).thenReturn(Optional.of(fact));
        when(modelMapper.map(fact.getFactOfTheDayTranslations().get(0), FactOfTheDayTranslationVO.class))
            .thenReturn(factOfTheDayTranslationVO);

        assertEquals(idList, factOfTheDayService.deleteAllFactOfTheDayAndTranslations(idList));
        verify(factOfTheDayRepo, times(3)).deleteById(anyLong());
        verify(factOfTheDayTranslationService, times(3)).deleteAll(anyList());
    }

    @Test
    void deleteFactOfTheDayAndTranslationsTest() {
        Long id = 1L;
        FactOfTheDay fact = ModelUtils.getFactOfTheDay();
        FactOfTheDayTranslationVO factOfTheDayTranslationVO = ModelUtils.getFactOfTheDayTranslationVO();
        when(factOfTheDayRepo.findById(anyLong())).thenReturn(Optional.of(fact));
        when(modelMapper.map(fact.getFactOfTheDayTranslations().get(0), FactOfTheDayTranslationVO.class))
            .thenReturn(factOfTheDayTranslationVO);

        assertEquals(id, factOfTheDayService.deleteFactOfTheDayAndTranslations(id));
        verify(factOfTheDayRepo, times(1)).deleteById(anyLong());
        verify(factOfTheDayTranslationService, times(1)).deleteAll(anyList());
    }

    @Test
    void deleteAllFactOfTheDayAndTranslationsTestFailed() {
        List<Long> longs = List.of(1L, 2L, 3L);
        when(factOfTheDayRepo.findById(anyLong())).thenThrow(NotUpdatedException.class);

        assertThrows(NotUpdatedException.class, () -> factOfTheDayService.deleteAllFactOfTheDayAndTranslations(longs));
    }

    @Test
    void deleteFactOfTheDayWithTranslationsTestFailed() {
        Long id = 1L;
        when(factOfTheDayRepo.findById(anyLong())).thenThrow(NotUpdatedException.class);

        assertThrows(NotUpdatedException.class, () -> factOfTheDayService.deleteFactOfTheDayAndTranslations(id));
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
        when(modelMapper.map(factOfTheDayTranslation, FactOfTheDayTranslationDTO.class))
            .thenReturn(factOfTheDayTranslationDTO);

        FactOfTheDayTranslationDTO actual = factOfTheDayService.getRandomFactOfTheDayByLanguage("en");

        assertEquals(factOfTheDayTranslationDTO, actual);

        verify(service, times(1)).getRandomFactOfTheDay();
    }

    @Test
    void getRandomFactOfTheDayByLanguageTestFailed() {
        String languageCode = "ua";
        Language english = ModelUtils.getLanguage();
        english.setCode("en");
        FactOfTheDayTranslation translation = ModelUtils.getFactOfTheDayTranslation();
        translation.setLanguage(english);
        FactOfTheDay fact = ModelUtils.getFactOfTheDay();
        fact.setFactOfTheDayTranslations(Collections.singletonList(translation));
        when(modelMapper.map(service.getRandomFactOfTheDay(), FactOfTheDay.class)).thenReturn(fact);

        assertThrows(NotFoundException.class, () -> factOfTheDayService.getRandomFactOfTheDayByLanguage(languageCode));
    }
}