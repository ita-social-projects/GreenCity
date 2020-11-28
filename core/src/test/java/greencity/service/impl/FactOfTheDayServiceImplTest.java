package greencity.service.impl;

import greencity.dto.factoftheday.FactOfTheDayDTO;
import greencity.dto.factoftheday.FactOfTheDayPostDTO;
import greencity.entity.FactOfTheDay;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotUpdatedException;
import greencity.repository.FactOfTheDayRepo;
import greencity.service.FactOfTheDayTranslationService;
import greencity.service.LanguageService;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class FactOfTheDayServiceImplTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private LanguageService languageService;

    @Mock
    private FactOfTheDayTranslationService factOfTheDayTranslationService;

    @Mock
    private PlaceCommentServiceImpl placeCommentService;

    @Mock
    private FactOfTheDayRepo factOfTheDayRepo;

    @InjectMocks
    private FactOfTheDayServiceImpl factOfTheDayService;

    private static FactOfTheDay factOfTheDay1 = new FactOfTheDay();
    private static List<FactOfTheDay> factsOfTheDay = new ArrayList<>();
    private static FactOfTheDayDTO factOfTheDayDTO = new FactOfTheDayDTO();
    private static FactOfTheDayPostDTO factOfTheDayPostDTO = new FactOfTheDayPostDTO();

    @BeforeAll
    public static void setup() {
        factOfTheDay1.setId(1L);
        factOfTheDay1.setName("Fact");
        factOfTheDay1.setCreateDate(ZonedDateTime.now());
        factsOfTheDay.add(factOfTheDay1);
        factOfTheDayDTO.setId(1L);
        factOfTheDayDTO.setName("Fact");
        factOfTheDayDTO.setCreateDate(ZonedDateTime.now());
        factOfTheDayPostDTO.setId(1L);
        factOfTheDayPostDTO.setName("FactPostDTO");
    }

    @Test
    public void testGetFactOfTheDayByName() {
        when(factOfTheDayRepo.findAllByName("Fact")).thenReturn(factsOfTheDay);
        Assertions.assertEquals(factOfTheDayService.getAllFactOfTheDayByName("Fact"),factsOfTheDay);
    }

    @Test
    public void testGetFactOfTheDayById() {
        when(factOfTheDayRepo.findById(1L)).thenReturn(java.util.Optional.ofNullable(factOfTheDay1));
        when(modelMapper.map(factOfTheDay1, FactOfTheDayDTO.class)).thenReturn(factOfTheDayDTO);
        Assertions.assertEquals(factOfTheDayService.getFactOfTheDayById(1L),factOfTheDayDTO);
    }

    @Test
    public void testGetFactOfTheDayWithUnknownId() {
        NotFoundException exception = new NotFoundException("FACT NOT FOUND");
        when(factOfTheDayRepo.findById(324L)).thenThrow(exception);
        Assertions.assertThrows(NotFoundException.class, () -> factOfTheDayService.getFactOfTheDayById(324L));
    }

    @Test
    public void testUpdate() {
        when(factOfTheDayRepo.findById(factOfTheDayDTO.getId())).thenReturn(
            java.util.Optional.ofNullable(factOfTheDay1));
        when(factOfTheDayRepo.save(factOfTheDay1)).thenReturn(factOfTheDay1);
        Assertions.assertEquals(factOfTheDayService.update(factOfTheDayPostDTO),factOfTheDay1);
    }

    @Test
    public void testDeleteFactOfTheDayAndTranslations() {
        when(factOfTheDayRepo.findById(1L)).thenReturn(java.util.Optional.ofNullable(factOfTheDay1));
        factOfTheDayRepo.deleteById(1L);
        verify(factOfTheDayRepo, times(1)).deleteById(1L);
        Assertions.assertEquals(factOfTheDay1.getId(), factOfTheDayService.deleteFactOfTheDayAndTranslations(1L));
    }

    @Test
    public void testDeleteFactOfTheDayAndTranslationsWithUnknownId() {
        NotUpdatedException notUpdatedException = new NotUpdatedException("FACT_NOT_DELETED");
        when(factOfTheDayRepo.findById(345L)).thenThrow(notUpdatedException);
        Assertions.assertThrows(NotUpdatedException.class, () -> factOfTheDayService.deleteFactOfTheDayAndTranslations(345L));
    }

    @Test
    public void testDeleteAllFactOfTheDayAndTranslations() {
        FactOfTheDay factOfTheDay = new FactOfTheDay();
        List<Long> listId = Arrays.asList(1L,2L,3L,4L);
        listId.forEach(l -> {
            when(factOfTheDayRepo.findById(l)).thenReturn(Optional.of(factOfTheDay));
            factOfTheDayRepo.deleteById(l);
            factOfTheDayTranslationService.deleteAll(factOfTheDay.getFactOfTheDayTranslations());
            verify(factOfTheDayRepo, times(1)).deleteById(l);
        });
        Assertions.assertEquals(listId, factOfTheDayService.deleteAllFactOfTheDayAndTranslations(listId));
    }
}
