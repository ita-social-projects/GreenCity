package greencity.service.impl;

import greencity.ModelUtils;
import greencity.dto.PageableDto;
import greencity.dto.factoftheday.FactOfTheDayDTO;
import greencity.dto.factoftheday.FactOfTheDayPostDTO;
import greencity.entity.FactOfTheDay;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotUpdatedException;
import greencity.repository.FactOfTheDayRepo;
import greencity.service.LanguageService;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class FactOfTheDayServiceImplTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private FactOfTheDayTranslationServiceImpl factOfTheDayTranslationService;

    @Mock
    private FactOfTheDayRepo factOfTheDayRepo;

    @Mock
    private LanguageService languageService;

    @InjectMocks
    private FactOfTheDayServiceImpl factOfTheDayService;

    @Test
    public void findByIdTest(){
        FactOfTheDayDTO factDto = ModelUtils.getFactOfTheDayDto();
        FactOfTheDay fact = ModelUtils.getFactOfTheDay();

        when(factOfTheDayRepo.findById(anyLong())).thenReturn(Optional.of(fact));
        when(modelMapper.map(fact, FactOfTheDayDTO.class)).thenReturn(factDto);
        FactOfTheDayDTO factDtoRes = factOfTheDayService.getFactOfTheDayById(1L);

        assertEquals(Long.valueOf(1), factDtoRes.getId());
    }

    @Test
    public void findByIdTestFailed(){
        when(factOfTheDayRepo.findById(anyLong())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> factOfTheDayService.getFactOfTheDayById(1L));
    }

    @Test
    public void findAllByNameTest(){
        List<FactOfTheDay> factsOfTheDays = Collections.singletonList(ModelUtils.getFactOfTheDay());
        when(factOfTheDayRepo.findAllByName("name")).thenReturn(factsOfTheDays);
        List<FactOfTheDay> factsOfTheDaysFound = factOfTheDayService.getAllFactOfTheDayByName("name");

        assertEquals(factsOfTheDays, factsOfTheDaysFound);
    }

    @Test
    public void findAllTest(){
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
    public void updateTest(){
        FactOfTheDay fact = ModelUtils.getFactOfTheDay();
        FactOfTheDayPostDTO factDtoPost = ModelUtils.getFactOfTheDayPostDto();

        when(factOfTheDayRepo.findById(anyLong())).thenReturn(Optional.of(fact));
        when(factOfTheDayService.update(factDtoPost)).thenReturn(fact);
        factOfTheDayService.update(factDtoPost);

        assertEquals(fact, factOfTheDayService.update(factDtoPost));
    }

    @Test
    public void updateTestFailed(){
        FactOfTheDayPostDTO factDtoPost = ModelUtils.getFactOfTheDayPostDto();
        when(factOfTheDayRepo.findById(anyLong())).thenThrow(NotUpdatedException.class);

        assertThrows(NotUpdatedException.class, () -> factOfTheDayService.update(factDtoPost));
    }

    @Test
    public void saveFactOfTheDayWithTranslationsTest(){
        FactOfTheDayPostDTO factDtoPost = ModelUtils.getFactOfTheDayPostDto();

        FactOfTheDayPostDTO res = factOfTheDayService.saveFactOfTheDayAndTranslations(factDtoPost);
        verify(factOfTheDayRepo, times(1)).save(any(FactOfTheDay.class));
        verify(factOfTheDayTranslationService, times(1)).saveAll(anyList());

        assertEquals(factDtoPost, res);
    }

    @Test
    public void updateFactOfTheDayWithTranslationsTest(){
        FactOfTheDay fact = ModelUtils.getFactOfTheDay();
        FactOfTheDayPostDTO factDtoPost = ModelUtils.getFactOfTheDayPostDto();

        when(factOfTheDayRepo.findById(anyLong())).thenReturn(Optional.of(fact));
        FactOfTheDayPostDTO res = factOfTheDayService.updateFactOfTheDayAndTranslations(factDtoPost);

        verify(factOfTheDayTranslationService, times(1)).deleteAll(fact.getFactOfTheDayTranslations());
        verify(factOfTheDayRepo, times(1)).save(any(FactOfTheDay.class));
        verify(factOfTheDayTranslationService, times(1)).saveAll(anyList());

        assertEquals(factDtoPost, res);
    }

    @Test
    public void updateFactOfTheDayWithTranslationsTestFailed(){
        FactOfTheDayPostDTO factDtoPost = ModelUtils.getFactOfTheDayPostDto();
        when(factOfTheDayRepo.findById(anyLong())).thenThrow(NotUpdatedException.class);

        assertThrows(NotUpdatedException.class, () -> factOfTheDayService.updateFactOfTheDayAndTranslations(factDtoPost));
    }

    @Test
    public void deleteFactOfTheDayAndTranslationsTest(){
        FactOfTheDay fact = ModelUtils.getFactOfTheDay();
        when(factOfTheDayRepo.findById(anyLong())).thenReturn(Optional.of(fact));
        assertEquals(fact.getId(), factOfTheDayService.deleteFactOfTheDayAndTranslations(1L));

        verify(factOfTheDayRepo, times(1)).deleteById(anyLong());
        verify(factOfTheDayTranslationService, times(1)).deleteAll(fact.getFactOfTheDayTranslations());
    }

    @Test
    public void deleteFactOfTheDayWithTranslationsTestFailed(){
        when(factOfTheDayRepo.findById(anyLong())).thenThrow(NotUpdatedException.class);
        assertThrows(NotUpdatedException.class, () -> factOfTheDayService.deleteFactOfTheDayAndTranslations(anyLong()));
    }
}