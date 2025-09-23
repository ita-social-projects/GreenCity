package greencity.service;

import static greencity.constant.OpenAIRequest.NEWS_WITHOUT_QUERY;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.ModelUtils;
import greencity.converters.FloatArrayConverter;
import greencity.client.UserRemoteClient;
import greencity.constant.OpenAIConstants;
import greencity.dto.habit.DurationHabitDto;
import greencity.dto.habit.ShortHabitDto;
import greencity.dto.language.LanguageDTO;
import greencity.dto.openai.OpenAIResponseDTO;
import greencity.dto.user.UserVO;
import greencity.entity.EcoNews;
import greencity.entity.EcoNewsRelevance;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.enums.OpenAIResponseFormat;
import greencity.enums.TagType;
import greencity.exception.exceptions.EcoNewsCreationException;
import greencity.exception.exceptions.JsonResponseParseException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.OpenAIRequestException;
import greencity.exception.exceptions.WrongEmailException;
import greencity.repository.EcoNewsRelevanceRepo;
import greencity.repository.EcoNewsRepo;
import greencity.repository.HabitAssignRepo;
import greencity.repository.HabitRepo;
import greencity.repository.TagsRepo;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClientRequestException;

@ExtendWith(MockitoExtension.class)
class AIServiceImplTest {

    @InjectMocks
    private AIServiceImpl aiService;
    @Mock
    private OpenAIService openAIService;
    @Mock
    private LanguageService languageService;
    @Mock
    private EcoNewsRepo ecoNewsRepo;
    @Mock
    private HabitAssignRepo habitAssignRepo;
    @Mock
    private TagsRepo tagsRepo;
    @Mock
    private UserService userService;
    @Mock
    private HabitRepo habitRepo;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private FloatArrayConverter floatArrayConverter;
    @Mock
    private EcoNewsRelevanceRepo ecoNewsRelevanceRepo;

    private final Long id = 1L;
    private final String language = "en";
    private LanguageDTO languageDTO;
    private User user;
    private UserVO userVO;
    private Tag tag;
    private Habit habit;
    private ShortHabitDto shortHabitDto;
    private HabitAssign habitAssign;
    private DurationHabitDto durationHabitDto;
    private OpenAIResponseDTO openAIResponseDTO;
    private EcoNews ecoNews;

    @BeforeEach
    void init() {
        languageDTO = ModelUtils.getLanguageDTO();
        user = ModelUtils.getUser();
        userVO = ModelUtils.getUserVO();
        tag = ModelUtils.getTag();
        habit = ModelUtils.getHabit();
        shortHabitDto = ModelUtils.getShortHabitDto();
        habitAssign = ModelUtils.getHabitAssign();
        durationHabitDto = ModelUtils.getDurationHabitDto();
        openAIResponseDTO = ModelUtils.getOpenAIResponseDTO();
        ecoNews = EcoNews.builder()
            .id(id)
            .title("Test EcoNews Title")
            .text("Test EcoNews content.")
            .build();

        ReflectionTestUtils.setField(aiService, "objectMapper", new ObjectMapper());

    }

    @Test
    void getForecast_whenHabitAssignsAreEmpty_shouldReturnAdvice() {
        String responseJson = "{\"content\": \"advice\"}";
        openAIResponseDTO.setContent(responseJson);

        when(habitAssignRepo.findAllByUserId(id)).thenReturn(Collections.emptyList());
        when(habitRepo.findRandomHabit()).thenReturn(habit);
        when(modelMapper.map(habit, ShortHabitDto.class)).thenReturn(shortHabitDto);
        when(languageService.findByCode(language)).thenReturn(languageDTO);
        when(openAIService.makeRequest(eq(languageDTO), anyString(), eq(OpenAIResponseFormat.TEXT)))
            .thenReturn(openAIResponseDTO);

        String result = aiService.getForecast(id, language);

        assertTrue(result.contains("advice"));
        verify(habitAssignRepo).findAllByUserId(id);
        verify(habitRepo).findRandomHabit();
        verify(modelMapper).map(habit, ShortHabitDto.class);
        verify(openAIService).makeRequest(eq(languageDTO), contains(shortHabitDto.toString()),
            eq(OpenAIResponseFormat.TEXT));
    }

    @Test
    void getForecast_whenHabitAssignsIsNull_shouldThrowNotFoundException() {
        when(habitRepo.findRandomHabit()).thenReturn(null);

        assertThrows(NotFoundException.class, () -> aiService.getForecast(id, language));

        verify(habitRepo).findRandomHabit();
        verify(openAIService, never()).makeRequest(eq(languageDTO), contains(shortHabitDto.toString()),
            eq(OpenAIResponseFormat.TEXT));
    }

    @Test
    void getForecast_whenHabitAssignsExist_shouldReturnForecast() {
        String responseJson = "{\"content\": \"forecast\"}";
        openAIResponseDTO.setContent(responseJson);

        when(habitAssignRepo.findAllByUserId(id)).thenReturn(List.of(habitAssign));
        when(modelMapper.map(habitAssign, DurationHabitDto.class)).thenReturn(durationHabitDto);
        when(languageService.findByCode(language)).thenReturn(languageDTO);
        when(openAIService.makeRequest(eq(languageDTO), anyString(), eq(OpenAIResponseFormat.TEXT)))
            .thenReturn(openAIResponseDTO);

        String result = aiService.getForecast(id, language);

        assertTrue(result.contains("forecast"));
        verify(habitAssignRepo).findAllByUserId(id);
        verify(modelMapper).map(habitAssign, DurationHabitDto.class);
        verify(languageService).findByCode(language);
        verify(openAIService).makeRequest(eq(languageDTO), contains(durationHabitDto.toString()),
            eq(OpenAIResponseFormat.TEXT));
    }

    @Test
    void generateAndSaveEcoNews_shouldCreateAndSaveEcoNews() {
        String jsonResponse = """
                {
                    "title": "AI-generated Title",
                    "content": "This is the eco news content."
                }
            """;
        openAIResponseDTO.setContent(jsonResponse);

        when(languageService.findByCode(language)).thenReturn(languageDTO);
        when(openAIService.makeRequest(languageDTO, NEWS_WITHOUT_QUERY, OpenAIResponseFormat.JSON_SCHEMA))
            .thenReturn(openAIResponseDTO);
        when(userService.findNotDeactivatedByEmail(OpenAIConstants.AI_USER_EMAIL)).thenReturn(ModelUtils.getUserVO());
        when(tagsRepo.findTagsByType(TagType.ECO_NEWS)).thenReturn(List.of(tag));
        when(modelMapper.map(userVO, User.class)).thenReturn(user);

        aiService.generateAndSaveEcoNews(language);

        ArgumentCaptor<EcoNews> captor = ArgumentCaptor.forClass(EcoNews.class);
        verify(ecoNewsRepo).save(captor.capture());

        EcoNews savedNews = captor.getValue();
        assertEquals("AI-generated Title", savedNews.getTitle());
        assertEquals("This is the eco news content.", savedNews.getText());
        assertEquals(user, savedNews.getAuthor());
        assertTrue(savedNews.getTags().contains(tag));
    }

    @Test
    void generateAndSaveEcoNews_whenNoTagsFound_shouldThrowException() {
        String jsonResponse = """
                {
                    "title": "Sample",
                    "content": "News content"
                }
            """;
        openAIResponseDTO.setContent(jsonResponse);

        when(languageService.findByCode(language)).thenReturn(languageDTO);
        when(openAIService.makeRequest(languageDTO, NEWS_WITHOUT_QUERY, OpenAIResponseFormat.JSON_SCHEMA))
            .thenReturn(openAIResponseDTO);
        when(userService.findNotDeactivatedByEmail(OpenAIConstants.AI_USER_EMAIL)).thenReturn(ModelUtils.getUserVO());
        when(tagsRepo.findTagsByType(TagType.ECO_NEWS)).thenReturn(Collections.emptyList());

        assertThrows(EcoNewsCreationException.class, () -> aiService.generateAndSaveEcoNews(language));
    }

    @Test
    void getNews_whenValidJsonReturned_shouldReturnParsedContent() {
        String query = "climate";
        String openAiRawResponse = """
            {
                "title": "Eco News Title",
                "content": "This is the eco news content."
            }
            """;
        openAIResponseDTO.setContent(openAiRawResponse);

        when(languageService.findByCode(language)).thenReturn(languageDTO);
        when(openAIService.makeRequest(eq(languageDTO), contains(query), eq(OpenAIResponseFormat.JSON_SCHEMA)))
            .thenReturn(openAIResponseDTO);

        String result = aiService.getNews(language, query);

        assertEquals("This is the eco news content.", result);
        verify(languageService).findByCode(language);
        verify(openAIService).makeRequest(eq(languageDTO), contains(query), eq(OpenAIResponseFormat.JSON_SCHEMA));
    }

    @Test
    void getNews_whenQueryIsNull_shouldReturnParsedContent() {
        String openAiResponse = """
                {
                    "title": "AI Title",
                    "content": "AI-generated eco news content."
                }
            """;
        openAIResponseDTO.setContent(openAiResponse);

        when(languageService.findByCode(language)).thenReturn(languageDTO);
        when(openAIService.makeRequest(eq(languageDTO), anyString(), eq(OpenAIResponseFormat.JSON_SCHEMA)))
            .thenReturn(openAIResponseDTO);

        String result = aiService.getNews(language, null);

        assertEquals("AI-generated eco news content.", result);
        verify(languageService).findByCode(language);
        verify(openAIService).makeRequest(eq(languageDTO), anyString(), eq(OpenAIResponseFormat.JSON_SCHEMA));
    }

    @Test
    void getNews_whenJsonResponseParseException_shouldRetryAndEventuallyThrow() {
        String query = "climate";

        String invalidJson = "not a valid json";
        openAIResponseDTO.setContent(invalidJson);

        when(languageService.findByCode(language)).thenReturn(languageDTO);
        when(openAIService.makeRequest(eq(languageDTO), anyString(), eq(OpenAIResponseFormat.JSON_SCHEMA)))
            .thenReturn(openAIResponseDTO);

        JsonResponseParseException thrown = assertThrows(
            JsonResponseParseException.class,
            () -> aiService.getNews(language, query));

        assertEquals(OpenAIConstants.ERROR_MAX_ATTEMPTS_REACHED, thrown.getMessage());

        verify(openAIService, times(OpenAIConstants.MAX_REQUEST_ATTEMPTS))
            .makeRequest(eq(languageDTO), anyString(), eq(OpenAIResponseFormat.JSON_SCHEMA));
    }

    @Test
    void getNews_whenOpenAIRequestException_shouldRetryAndEventuallyThrow() {
        String query = "climate";

        when(languageService.findByCode(language)).thenReturn(languageDTO);
        when(openAIService.makeRequest(eq(languageDTO), anyString(), eq(OpenAIResponseFormat.JSON_SCHEMA)))
            .thenThrow(new OpenAIRequestException(OpenAIConstants.ERROR_INVALID_OPENAI_RESPONSE));

        JsonResponseParseException thrown = assertThrows(
            JsonResponseParseException.class,
            () -> aiService.getNews(language, query));

        assertEquals(OpenAIConstants.ERROR_MAX_ATTEMPTS_REACHED, thrown.getMessage());

        verify(openAIService, times(OpenAIConstants.MAX_REQUEST_ATTEMPTS))
            .makeRequest(eq(languageDTO), anyString(), eq(OpenAIResponseFormat.JSON_SCHEMA));
    }

    @Test
    void getNews_whenOpenAIRespondsWithNoResponse_shouldThrowImmediately() {
        String query = "pollution";

        when(languageService.findByCode(language)).thenReturn(languageDTO);
        when(openAIService.makeRequest(eq(languageDTO), anyString(), eq(OpenAIResponseFormat.JSON_SCHEMA)))
            .thenThrow(new OpenAIRequestException(OpenAIConstants.ERROR_NO_OPENAI_RESPONSE));

        OpenAIRequestException thrown = assertThrows(
            OpenAIRequestException.class,
            () -> aiService.getNews(language, query));

        assertEquals(OpenAIConstants.ERROR_NO_OPENAI_RESPONSE, thrown.getMessage());

        verify(openAIService, times(1))
            .makeRequest(eq(languageDTO), anyString(), eq(OpenAIResponseFormat.JSON_SCHEMA));
    }

    @Test
    void generateAndSaveEcoNews_whenAiUserIsEmpty_shouldThrowWrongEmailException() {
        String jsonResponse = """
            {
                "title": "New Title",
                "content": "New eco content"
            }
            """;
        openAIResponseDTO.setContent(jsonResponse);

        when(languageService.findByCode(language)).thenReturn(languageDTO);
        when(openAIService.makeRequest(languageDTO, NEWS_WITHOUT_QUERY, OpenAIResponseFormat.JSON_SCHEMA))
            .thenReturn(openAIResponseDTO);

        when(userService.findNotDeactivatedByEmail(OpenAIConstants.AI_USER_EMAIL))
            .thenThrow(new WrongEmailException());

        assertThrows(WrongEmailException.class, () -> aiService.generateAndSaveEcoNews(language));
    }

    @Test
    void generateAndSaveEcoNews_whenTitleAndContentInText_shouldParseViaGetJsonNodes() {
        String languageCode = "en";
        String response = """
             Title: Clean Energy Future
            Governments around the world invest in renewables.
            """;
        openAIResponseDTO.setContent(response);

        when(languageService.findByCode(languageCode)).thenReturn(languageDTO);
        when(openAIService.makeRequest(languageDTO, NEWS_WITHOUT_QUERY, OpenAIResponseFormat.JSON_SCHEMA))
            .thenReturn(openAIResponseDTO);
        when(userService.findNotDeactivatedByEmail(OpenAIConstants.AI_USER_EMAIL)).thenReturn(ModelUtils.getUserVO());
        when(tagsRepo.findTagsByType(TagType.ECO_NEWS)).thenReturn(List.of(tag));
        when(modelMapper.map(userVO, User.class)).thenReturn(user);

        aiService.generateAndSaveEcoNews(languageCode);

        ArgumentCaptor<EcoNews> newsCaptor = ArgumentCaptor.forClass(EcoNews.class);
        verify(ecoNewsRepo).save(newsCaptor.capture());

        EcoNews saved = newsCaptor.getValue();
        assertEquals("Clean Energy Future", saved.getTitle());
        assertEquals("Governments around the world invest in renewables.", saved.getText());
        assertEquals(user, saved.getAuthor());
        assertTrue(saved.getTags().contains(tag));
    }

    @Test
    void getRelevanceForEcoNews_shouldSuccessfullyCalculateAndSaveRelevance() {
        String ecoNewsTitle = ecoNews.getTitle();
        Float[] expectedVector = {0.1f, 0.2f, 0.3f, 0.4f};
        OpenAIResponseDTO embeddingResponse = new OpenAIResponseDTO();
        embeddingResponse.setContent(Arrays.toString(expectedVector));

        when(ecoNewsRepo.findById(id)).thenReturn(Optional.of(ecoNews));
        when(openAIService.makeRequestEmbedding(ecoNewsTitle)).thenReturn(embeddingResponse);
        doReturn(expectedVector).when(floatArrayConverter)
            .convertToEntityAttribute(Arrays.toString(expectedVector));

        when(ecoNewsRelevanceRepo.save(any(EcoNewsRelevance.class))).thenReturn(null);

        aiService.getRelevanceForEcoNews(id);

        verify(ecoNewsRepo).findById(id);
        verify(openAIService).makeRequestEmbedding(ecoNewsTitle);
        verify(floatArrayConverter).convertToEntityAttribute(Arrays.toString(expectedVector));
        ArgumentCaptor<EcoNewsRelevance> relevanceCaptor = ArgumentCaptor.forClass(EcoNewsRelevance.class);
        verify(ecoNewsRelevanceRepo).save(relevanceCaptor.capture());

        EcoNewsRelevance savedRelevance = relevanceCaptor.getValue();
        assertNotNull(savedRelevance);
        assertEquals(ecoNews.getId(), savedRelevance.getId());
        assertEquals(ecoNews, savedRelevance.getEcoNews());
        assertArrayEquals(expectedVector, savedRelevance.getTitleVector());
    }

    @Test
    void insertRelevanceBatch_whenIdsNotEmpty_shouldSaveRelevanceList() {
        List<Long> ids = List.of(1L, 2L);
        EcoNews news1 = EcoNews.builder().id(1L).title("Title 1").build();
        EcoNews news2 = EcoNews.builder().id(2L).title("Title 2").build();

        List<EcoNews> ecoNewsList = List.of(news1, news2);

        OpenAIResponseDTO response1 = new OpenAIResponseDTO();
        response1.setContent("[1.0, 2.0]");
        OpenAIResponseDTO response2 = new OpenAIResponseDTO();
        response2.setContent("[3.0, 4.0]");

        when(ecoNewsRepo.findAllById(ids)).thenReturn(ecoNewsList);
        when(openAIService.makeRequestEmbeddings(List.of("Title 1", "Title 2")))
            .thenReturn(List.of(response1, response2));
        when(floatArrayConverter.convertToEntityAttribute("[1.0, 2.0]"))
            .thenReturn(new Float[] {1.0f, 2.0f});
        when(floatArrayConverter.convertToEntityAttribute("[3.0, 4.0]"))
            .thenReturn(new Float[] {3.0f, 4.0f});

        aiService.insertRelevanceBatch(ids);

        ArgumentCaptor<List<EcoNewsRelevance>> captor = ArgumentCaptor.forClass(List.class);
        verify(ecoNewsRelevanceRepo).saveAll(captor.capture());

        List<EcoNewsRelevance> savedList = captor.getValue();
        assertEquals(2, savedList.size());

        EcoNewsRelevance first = savedList.get(0);
        assertEquals(news1, first.getEcoNews());
        assertFalse(first.getIsOutdated());

        EcoNewsRelevance second = savedList.get(1);
        assertEquals(news2, second.getEcoNews());
        assertFalse(second.getIsOutdated());
    }

    @Test
    void insertRelevanceBatch_whenIdsEmpty_shouldDoNothing() {
        aiService.insertRelevanceBatch(Collections.emptyList());
        verifyNoInteractions(openAIService, floatArrayConverter, ecoNewsRelevanceRepo);
    }

    @Test
    void updateRelevanceBatch_whenIdsNotEmpty_shouldUpdateAndSaveRelevanceList() {
        List<Long> ids = List.of(1L, 2L);
        EcoNews news1 = EcoNews.builder().id(1L).title("Title 1").build();
        EcoNews news2 = EcoNews.builder().id(2L).title("Title 2").build();

        List<EcoNews> ecoNewsList = List.of(news1, news2);

        OpenAIResponseDTO response1 = new OpenAIResponseDTO();
        response1.setContent("[1.0, 2.0]");
        OpenAIResponseDTO response2 = new OpenAIResponseDTO();
        response2.setContent("[3.0, 4.0]");

        EcoNewsRelevance rel1 = EcoNewsRelevance.builder()
            .ecoNews(news1)
            .titleVector(new Float[] {0.0f, 0.0f})
            .isOutdated(true)
            .build();

        EcoNewsRelevance rel2 = EcoNewsRelevance.builder()
            .ecoNews(news2)
            .titleVector(new Float[] {0.0f, 0.0f})
            .isOutdated(true)
            .build();

        List<EcoNewsRelevance> relevanceList = List.of(rel1, rel2);

        when(ecoNewsRepo.findAllById(ids)).thenReturn(ecoNewsList);
        when(openAIService.makeRequestEmbeddings(List.of("Title 1", "Title 2")))
            .thenReturn(List.of(response1, response2));
        when(ecoNewsRelevanceRepo.findAllById(ids)).thenReturn(relevanceList);
        when(floatArrayConverter.convertToEntityAttribute("[1.0, 2.0]"))
            .thenReturn(new Float[] {1.0f, 2.0f});
        when(floatArrayConverter.convertToEntityAttribute("[3.0, 4.0]"))
            .thenReturn(new Float[] {3.0f, 4.0f});

        aiService.updateRelevanceBatch(ids);

        ArgumentCaptor<List<EcoNewsRelevance>> captor = ArgumentCaptor.forClass(List.class);
        verify(ecoNewsRelevanceRepo).saveAll(captor.capture());

        List<EcoNewsRelevance> savedList = captor.getValue();
        assertEquals(2, savedList.size());

        EcoNewsRelevance first = savedList.get(0);
        assertEquals(news1, first.getEcoNews());
        assertArrayEquals(new Float[] {1.0f, 2.0f}, first.getTitleVector());
        assertFalse(first.getIsOutdated());

        EcoNewsRelevance second = savedList.get(1);
        assertEquals(news2, second.getEcoNews());
        assertArrayEquals(new Float[] {3.0f, 4.0f}, second.getTitleVector());
        assertFalse(second.getIsOutdated());
    }

    @Test
    void updateRelevanceBatch_whenIdsEmpty_shouldDoNothing() {
        List<Long> ids = List.of();

        aiService.updateRelevanceBatch(ids);

        verifyNoInteractions(openAIService, ecoNewsRelevanceRepo, floatArrayConverter);
    }
}
