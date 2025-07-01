package greencity.service;

import greencity.ModelUtils;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.dailyfact.DailyFactDto;
import greencity.dto.dailyfact.DailyFactVO;
import greencity.dto.factoftheday.FactOfTheDayDTO;
import greencity.dto.factoftheday.FactOfTheDayPostDTO;
import greencity.dto.factoftheday.FactOfTheDayTranslationDTO;
import greencity.dto.factoftheday.FactOfTheDayTranslationVO;
import greencity.dto.language.LanguageDTO;
import greencity.dto.tag.TagDto;
import greencity.dto.user.UserVO;
import greencity.entity.FactOfTheDay;
import greencity.entity.Tag;
import greencity.entity.Language;
import greencity.entity.User;
import greencity.entity.DailyFact;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotUpdatedException;
import greencity.repository.DailyFactRepo;
import greencity.repository.FactOfTheDayRepo;
import greencity.repository.TagsRepo;
import greencity.repository.UserRepo;
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
import java.util.Set;
import java.util.Locale;

import static greencity.enums.TagType.FACT_OF_THE_DAY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FactOfTheDayServiceImplTest {
    @Mock
    private DailyFactRepo dailyFactRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private AIService aiService;

    @Mock
    private TranslationService translationService;

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
    @Mock
    private TagsRepo tagsRepo;

    @InjectMocks
    private FactOfTheDayServiceImpl factOfTheDayService;

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
    void saveFactOfTheDayAndTranslationsTest() {
        FactOfTheDayPostDTO factDtoPost = ModelUtils.getFactOfTheDayPostDto();
        Set<Tag> tagDtos = Set.of(ModelUtils.getTag());

        when(tagsRepo.findTagsById(List.of(25L))).thenReturn(tagDtos);

        FactOfTheDayPostDTO res = factOfTheDayService.saveFactOfTheDayAndTranslations(factDtoPost);
        verify(factOfTheDayRepo, times(1)).save(any(FactOfTheDay.class));
        verify(factOfTheDayTranslationService, times(1)).saveAll(anyList());

        assertEquals(factDtoPost, res);
    }

    @Test
    void saveFactOfTheDayAndTranslationsTestFailed() {
        FactOfTheDayPostDTO factDTO = ModelUtils.getFactOfTheDayPostDto();
        FactOfTheDay fact = ModelUtils.getFactOfTheDay();
        Set<Tag> tagDtos = Set.of(ModelUtils.getTag());

        when(tagsRepo.findTagsById(List.of(25L))).thenReturn(tagDtos);
        when(factOfTheDayRepo.save(fact)).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> factOfTheDayService.saveFactOfTheDayAndTranslations(factDTO));
        verify(factOfTheDayTranslationService, times(0)).saveAll(anyList());
    }

    @Test
    void updateFactOfTheDayAndTranslationsTest() {
        LanguageDTO languageDTO = ModelUtils.getLanguageDTO();
        FactOfTheDay dbFact = ModelUtils.getFactOfTheDay();
        Set<Tag> tagDtos = Set.of(ModelUtils.getTag());

        when(factOfTheDayRepo.findById(anyLong())).thenReturn(Optional.of(dbFact));
        when(modelMapper.map(dbFact.getFactOfTheDayTranslations().getFirst(), FactOfTheDayTranslationVO.class))
            .thenReturn(
                ModelUtils.getFactOfTheDayTranslationVO());
        when(languageService.findByCode("en")).thenReturn(languageDTO);
        when(factOfTheDayTranslationService.saveAll(anyList())).thenReturn(null);
        when(tagsRepo.findTagsById(List.of(25L))).thenReturn(tagDtos);

        FactOfTheDayPostDTO fact = ModelUtils.getFactOfTheDayPostDto();
        assertEquals(fact, factOfTheDayService.updateFactOfTheDayAndTranslations(fact));
        verify(factOfTheDayRepo, times(1)).findById(anyLong());
        verify(factOfTheDayTranslationService, times(1)).deleteAll(anyList());
        verify(languageService, times(1)).findByCode(anyString());
        verify(modelMapper, times(1)).map(languageDTO, Language.class);
        verify(factOfTheDayRepo, times(1)).save(any(FactOfTheDay.class));
        verify(factOfTheDayTranslationService, times(1)).saveAll(anyList());
        verify(modelMapper, times(1)).map(dbFact.getFactOfTheDayTranslations().getFirst(),
            FactOfTheDayTranslationVO.class);
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
        when(modelMapper.map(fact.getFactOfTheDayTranslations().getFirst(), FactOfTheDayTranslationVO.class))
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
        when(modelMapper.map(fact.getFactOfTheDayTranslations().getFirst(), FactOfTheDayTranslationVO.class))
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
    void getRandomFactOfTheDayByTags_success() {
        Set<Long> tagIds = Set.of(1L, 2L);
        FactOfTheDay factOfTheDay = ModelUtils.getFactOfTheDay();
        FactOfTheDayTranslationDTO expectedDto = ModelUtils.getFactOfTheDayTranslationDTO();

        when(factOfTheDayRepo.getRandomFactOfTheDay(tagIds)).thenReturn(Optional.of(factOfTheDay));
        when(modelMapper.map(factOfTheDay, FactOfTheDayTranslationDTO.class)).thenReturn(expectedDto);

        FactOfTheDayTranslationDTO result =
            factOfTheDayService.getRandomFactOfTheDayByTags(tagIds);

        assertEquals(expectedDto, result);
    }

    @Test
    void getRandomFactOfTheDayByLanguageAndTags_factNotFound() {
        Set<Long> tagIds = Set.of(1L, 2L);

        when(factOfTheDayRepo.getRandomFactOfTheDay(tagIds)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> factOfTheDayService.getRandomFactOfTheDayByTags(tagIds));
    }

    @Test
    void getRandomGeneralFactOfTheDay_success() {
        List<Tag> tags = List.of(ModelUtils.getTag());
        Set<Long> tagIds = Set.of(1L);
        FactOfTheDay factOfTheDay = ModelUtils.getFactOfTheDay();
        FactOfTheDayTranslationDTO expectedDto = ModelUtils.getFactOfTheDayTranslationDTO();

        when(factOfTheDayRepo.getRandomFactOfTheDay(tagIds)).thenReturn(Optional.of(factOfTheDay));
        when(modelMapper.map(factOfTheDay, FactOfTheDayTranslationDTO.class)).thenReturn(expectedDto);
        when(tagsRepo.findTagsByType(FACT_OF_THE_DAY)).thenReturn(tags);
        when(factOfTheDayService.getRandomFactOfTheDayByTags(tagIds))
            .thenReturn(expectedDto);

        FactOfTheDayTranslationDTO result = factOfTheDayService.getRandomGeneralFactOfTheDay();

        assertEquals(expectedDto, result);
    }

    @Test
    void getRandomGeneralFactOfTheDay_noTagsFound() {
        when(tagsRepo.findTagsByType(FACT_OF_THE_DAY)).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> factOfTheDayService.getRandomGeneralFactOfTheDay());
    }

    @Test
    void getRandomFactOfTheDayForUser_success() {
        String userEmail = "user@example.com";
        Set<Long> tagIds = Set.of(1L);
        FactOfTheDay factOfTheDay = ModelUtils.getFactOfTheDay();
        FactOfTheDayTranslationDTO expectedDto = ModelUtils.getFactOfTheDayTranslationDTO();

        when(factOfTheDayRepo.getRandomFactOfTheDay(tagIds)).thenReturn(Optional.of(factOfTheDay));
        when(modelMapper.map(factOfTheDay, FactOfTheDayTranslationDTO.class)).thenReturn(expectedDto);
        when(tagsRepo.findTagsIdByUserHabitsInProgress(userEmail)).thenReturn(tagIds);
        when(factOfTheDayService.getRandomFactOfTheDayByTags(tagIds))
            .thenReturn(expectedDto);

        FactOfTheDayTranslationDTO result =
            factOfTheDayService.getRandomFactOfTheDayForUser(userEmail);

        assertEquals(expectedDto, result);
    }

    @Test
    void getRandomFactOfTheDayForUser_noUserTags() {
        String userEmail = "user@example.com";

        when(tagsRepo.findTagsIdByUserHabitsInProgress(userEmail)).thenReturn(Collections.emptySet());

        FactOfTheDayTranslationDTO result = factOfTheDayService.getRandomFactOfTheDayForUser(userEmail);

        assertNull(result);
    }

    @Test
    void getAllFactOfTheDayTags_success() {
        TagDto tagDto = ModelUtils.getTagDto();
        Set<TagDto> expectedTags = Set.of(tagDto);

        when(factOfTheDayRepo.findAllFactOfTheDayAndHabitTags()).thenReturn(expectedTags);

        Set<TagDto> result = factOfTheDayService.getAllFactOfTheDayTags();

        assertEquals(expectedTags, result);
    }

    @Test
    void getAllFactOfTheDayTags_noTagsFound() {
        when(factOfTheDayRepo.findAllFactOfTheDayAndHabitTags()).thenReturn(Collections.emptySet());

        Set<TagDto> result = factOfTheDayService.getAllFactOfTheDayTags();

        assertTrue(result.isEmpty());
    }

    @Test
    void saveDailyFact_success() {
        UserVO userVO = ModelUtils.getUserVO();
        DailyFactDto dto = DailyFactDto.builder()
            .userVO(userVO)
            .factEn("en fact")
            .factUk("uk fact")
            .build();

        User user = ModelUtils.getUser();
        DailyFact savedEntity = ModelUtils.getDailyFact();
        DailyFactVO expected = ModelUtils.getDailyFactVO();

        when(dailyFactRepo.existsByUserId(userVO.getId())).thenReturn(false);
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(dailyFactRepo.save(any())).thenReturn(savedEntity);
        when(modelMapper.map(savedEntity, DailyFactVO.class)).thenReturn(expected);

        DailyFactVO result = factOfTheDayService.saveDailyFact(dto);
        assertEquals(expected, result);
    }

    @Test
    void saveDailyFact_whenFactAlreadyExistsForUser_shouldThrowIllegalArgumentException() {
        UserVO userVO = ModelUtils.getUserVO();
        DailyFactDto dto = DailyFactDto.builder().userVO(userVO).build();
        when(dailyFactRepo.existsByUserId(userVO.getId())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> factOfTheDayService.saveDailyFact(dto));

        String expectedMessage = ErrorMessage.DAILY_FACT_ALREADY_EXISTS_FOR_USER + userVO.getId();
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void updateDailyFact_success() {
        DailyFact existing = ModelUtils.getDailyFact();
        DailyFactDto dto = DailyFactDto.builder()
            .id(existing.getId())
            .factEn("updated en")
            .factUk("updated uk")
            .build();

        DailyFact updatedEntity = DailyFact.builder()
            .id(existing.getId())
            .user(existing.getUser())
            .factEn(dto.getFactEn())
            .factUk(dto.getFactUk())
            .build();

        DailyFactVO expected = ModelUtils.getDailyFactVO();

        when(dailyFactRepo.findById(dto.getId())).thenReturn(Optional.of(existing));
        when(dailyFactRepo.save(any())).thenReturn(updatedEntity);
        when(modelMapper.map(updatedEntity, DailyFactVO.class)).thenReturn(expected);

        DailyFactVO result = factOfTheDayService.updateDailyFact(dto);
        assertEquals(expected, result);
    }

    @Test
    void updateDailyFact_whenDailyFactNotFound_shouldThrowNotFoundException() {
        long nonExistentId = 999L;
        DailyFactDto dto = DailyFactDto.builder()
            .id(nonExistentId)
            .factEn("fact EN")
            .factUk("факт УК")
            .build();
        when(dailyFactRepo.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> factOfTheDayService.updateDailyFact(dto));

        String expectedMessage = ErrorMessage.DAILY_FACT_NOT_FOUND + nonExistentId;
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void getDailyFactForUser_existingSameDayFact_returnsCachedFact() {
        String email = "test@example.com";
        User user = ModelUtils.getUser();
        DailyFact existingFact = ModelUtils.getDailyFactToday();

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));
        when(dailyFactRepo.findByUserId(user.getId())).thenReturn(Optional.of(existingFact));

        String result = factOfTheDayService.getDailyFactForUser(email, Locale.ENGLISH);
        assertEquals(existingFact.getFactEn(), result);
    }

    @Test
    void getDailyFactForUser_existingOldFact_returnsNewFact() {
        String email = "test@example.com";
        User user = ModelUtils.getUser();
        DailyFact oldFact = ModelUtils.getOldDailyFact();
        String ecoFact = "English Fact";
        String translated = "Факт українською";

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));
        when(dailyFactRepo.findByUserId(user.getId())).thenReturn(Optional.of(oldFact));
        when(aiService.getEcoFact(user.getId(), "English")).thenReturn(ecoFact);
        when(translationService.translateText(ecoFact, "en", "uk")).thenReturn(translated);

        DailyFactVO savedFactVO = ModelUtils.getDailyFactVO();
        when(dailyFactRepo.findById(oldFact.getId())).thenReturn(Optional.of(oldFact));
        when(dailyFactRepo.save(any())).thenReturn(ModelUtils.getDailyFact());
        when(modelMapper.map(any(), eq(UserVO.class))).thenReturn(ModelUtils.getUserVO());
        when(modelMapper.map(any(DailyFact.class), eq(DailyFactVO.class))).thenReturn(savedFactVO);

        String result = factOfTheDayService.getDailyFactForUser(email, Locale.ENGLISH);
        assertEquals(ecoFact, result);
    }

    @Test
    void getDailyFactForUser_noExistingFact_createsNew() {
        String email = "test@example.com";
        User user = ModelUtils.getUser();
        String ecoFact = "English Fact";
        String translated = "Факт українською";

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));
        when(dailyFactRepo.findByUserId(user.getId())).thenReturn(Optional.empty());
        when(aiService.getEcoFact(user.getId(), "English")).thenReturn(ecoFact);
        when(translationService.translateText(ecoFact, "en", "uk")).thenReturn(translated);

        DailyFactVO savedFactVO = ModelUtils.getDailyFactVO();
        when(modelMapper.map(any(), eq(UserVO.class))).thenReturn(ModelUtils.getUserVO());
        when(modelMapper.map(any(UserVO.class), eq(User.class))).thenReturn(user);
        when(dailyFactRepo.save(any())).thenReturn(ModelUtils.getDailyFact());
        when(modelMapper.map(any(DailyFact.class), eq(DailyFactVO.class))).thenReturn(savedFactVO);

        String result = factOfTheDayService.getDailyFactForUser(email, Locale.ENGLISH);
        assertEquals(ecoFact, result);
    }

    @Test
    void getDailyFactForUser_userNotFound_throws() {
        String unknownEmail = "unknown@example.com";
        when(userRepo.findByEmail(unknownEmail)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> factOfTheDayService.getDailyFactForUser(unknownEmail, Locale.ENGLISH));

        assertEquals(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + unknownEmail, exception.getMessage());
    }
}