package greencity.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.dto.habit.DurationHabitDto;
import greencity.dto.habit.ShortHabitDto;
import greencity.entity.*;
import greencity.enums.HabitAssignStatus;
import greencity.enums.Language;
import greencity.enums.TagType;
import greencity.exception.exceptions.*;
import greencity.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static greencity.ModelUtils.getHabitAssign;
import static greencity.constant.OpenAIConstants.*;
import static greencity.constant.OpenAIRequest.NEWS_WITHOUT_QUERY;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AIServiceImplTest {

    @InjectMocks
    private AIServiceImpl aiService;

    @Mock
    private OpenAIService openAIService;

    @Mock
    private EcoNewsRepo ecoNewsRepo;

    @Mock
    private HabitAssignRepo habitAssignRepo;

    @Mock
    private TagsRepo tagsRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private HabitRepo habitRepo;

    @Mock
    private ModelMapper modelMapper;
    @Mock
    private ObjectMapper objectMapper;

    private HabitAssign habitAssign = getHabitAssign(HabitAssignStatus.INPROGRESS);
    private DurationHabitDto durationHabitDto = new DurationHabitDto("", 0L);
    private Long id = 1L;
    private String language = "en";
    private final String habitTranslationName = "Test Habit";
    private final HabitTranslation habitTranslation = HabitTranslation.builder().name(habitTranslationName).build();

    private final Habit habit = Habit.builder()
            .id(id)
            .habitTranslations(Collections.singletonList(habitTranslation))
            .build();
    private final ShortHabitDto shortHabitDto = new ShortHabitDto(id, habitTranslationName);

    @Test
    void getForecast_whenHabitAssignsAreEmpty_shouldCallGetAdvice() {

        when(habitAssignRepo.findAllByUserId(id)).thenReturn(Collections.emptyList());


        when(habitRepo.findRandomHabit()).thenReturn(habit);
        when(modelMapper.map(habit, ShortHabitDto.class)).thenReturn(shortHabitDto);
        when(openAIService.makeRequest(any(), anyString())).thenReturn("{\"content\": \"advice\"}");

        String result = aiService.getForecast(id, language);

        assertTrue(result.contains("advice"));
        verify(habitRepo).findRandomHabit();
        verify(openAIService).makeRequest(any(), contains("advice"));
    }

    @Test
    void getForecast_whenHabitsExist_shouldReturnSanitizedForecast() {
        when(habitAssignRepo.findAllByUserId(id)).thenReturn(List.of(habitAssign));
        when(modelMapper.map(habitAssign, DurationHabitDto.class)).thenReturn(durationHabitDto);
        when(openAIService.makeRequest(any(), anyString())).thenReturn("{\"content\": \"forecast \\n text\"}");

        String result = aiService.getForecast(id, language);

        assertTrue(result.contains("forecast"));
        verify(openAIService).makeRequest(any(), anyString());
    }
    @Test
    void getAdvice_shouldReturnSanitizedResponse() {

        when(habitRepo.findRandomHabit()).thenReturn(habit);
        when(modelMapper.map(habit, ShortHabitDto.class)).thenReturn(shortHabitDto);
        when(openAIService.makeRequest(any(), anyString())).thenReturn("{\"content\": \"some advice\\n\"}");

        String result = aiService.getAdvice(id, language);

        assertTrue(result.contains("some advice"));
        verify(habitRepo).findRandomHabit();
        verify(modelMapper).map(habit, ShortHabitDto.class);
        verify(openAIService).makeRequest(any(), anyString());
    }
    @Test
    void getNews_validJson_shouldReturnParsedContent() throws Exception {
        String query = "climate";
        String jsonResponse = "{\"title\":\"Test title\",\"content\":\"Test content\"}";

        when(openAIService.makeRequest(any(), anyString())).thenReturn(jsonResponse);
        when(objectMapper.readTree(anyString())).thenReturn(
                new ObjectMapper().readTree(jsonResponse)
        );

        String result = aiService.getNews("en", query);

        assertEquals("Test content", result);
        verify(openAIService).makeRequest(eq(Language.ENGLISH), contains(query));
    }


    @Test
    void getNews_jsonWithoutContent_shouldThrowJsonParseException() throws Exception {
        String jsonResponse = "{\"title\":\"Only title\"}";

        when(openAIService.makeRequest(any(), anyString())).thenReturn(jsonResponse);
        when(objectMapper.readTree(anyString())).thenReturn(
                new ObjectMapper().readTree(jsonResponse)
        );

        assertThrows(JsonResponseParseException.class,
                () -> aiService.getNews("en", "anything"));
    }

    @Test
    void generateAndSaveEcoNews_validLanguage_shouldCallMakeRequestAndSave() {
        String language = "en";
        String jsonResponse = "{\"title\":\"Some title\",\"content\":\"Some content\"}";

        when(openAIService.makeRequest(any(), eq(NEWS_WITHOUT_QUERY))).thenReturn(jsonResponse);

        User mockUser = new User();
        Tag mockTag = new Tag();

        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(tagsRepo.findTagsByType(any())).thenReturn(List.of(mockTag));

        aiService.generateAndSaveEcoNews(language);

        verify(openAIService).makeRequest(Language.fromCode(language), NEWS_WITHOUT_QUERY);
        verify(ecoNewsRepo).save(any(EcoNews.class));
    }

    @Test
    void generateAndSaveEcoNews_noTagsFound_shouldThrowEcoNewsCreationException() {
        String language = "en";
        String json = "{\"title\":\"Some Title\",\"content\":\"Some content\"}";

        when(openAIService.makeRequest(any(), eq(NEWS_WITHOUT_QUERY))).thenReturn(json);
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(new User()));
        when(tagsRepo.findTagsByType(TagType.ECO_NEWS)).thenReturn(Collections.emptyList());

        assertThrows(EcoNewsCreationException.class, () -> aiService.generateAndSaveEcoNews(language));

        verify(ecoNewsRepo, never()).save(any());
    }

    @Test
    void generateAndSaveEcoNews_titlePrefix_shouldUseGetJsonNodesAndSaveEcoNews() {
        String language = "en";
        String jsonResponse = "Title: Save the Planet\nThis is eco content generated by AI";

        User mockUser = new User();
        Tag mockTag = new Tag();

        when(openAIService.makeRequest(any(), eq(NEWS_WITHOUT_QUERY))).thenReturn(jsonResponse);
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(tagsRepo.findTagsByType(TagType.ECO_NEWS)).thenReturn(List.of(mockTag));

        aiService.generateAndSaveEcoNews(language);

        ArgumentCaptor<EcoNews> captor = ArgumentCaptor.forClass(EcoNews.class);
        verify(ecoNewsRepo).save(captor.capture());

        EcoNews saved = captor.getValue();
        assertEquals("Save the Planet", saved.getTitle());
        assertEquals("This is eco content generated by AI", saved.getText());
        assertEquals(mockUser, saved.getAuthor());
        assertEquals(List.of(mockTag), saved.getTags());
    }

    @Test
    void generateAndSaveEcoNews_shouldCreateAiUserWhenMissing() {
        String language = "en";
        String jsonResponse = "Title: Eco AI\nGenerated eco content";

        Tag mockTag = new Tag();
        User savedUser = new User();

        when(openAIService.makeRequest(any(), eq(NEWS_WITHOUT_QUERY))).thenReturn(jsonResponse);
        when(userRepo.findByEmail(AI_USER_EMAIL)).thenReturn(Optional.empty());
        when(userRepo.save(any(User.class))).thenReturn(savedUser);
        when(tagsRepo.findTagsByType(TagType.ECO_NEWS)).thenReturn(List.of(mockTag));

        aiService.generateAndSaveEcoNews(language);

        verify(userRepo).save(argThat(user -> AI_USER_EMAIL.equals(user.getEmail())));
        verify(ecoNewsRepo).save(any(EcoNews.class));
    }

    @Test
    void getNews_withMalformedJson_shouldThrowJsonResponseParseException_withInvalidJsonFormatCause() {
        String invalidJson = "{ invalid json";

        when(openAIService.makeRequest(any(), any())).thenReturn(invalidJson);

        JsonResponseParseException ex = assertThrows(
                JsonResponseParseException.class,
                () -> aiService.getNews("en", "query")
        );

        assertInstanceOf(InvalidJsonFormatException.class, ex.getCause());
    }

    @Test
    void getNews_withNullJson_shouldThrowJsonResponseParseException_withNullPointerCause() {
        when(openAIService.makeRequest(any(), any())).thenReturn(null);

        JsonResponseParseException ex = assertThrows(
                JsonResponseParseException.class,
                () -> aiService.getNews("en", "query")
        );

        assertInstanceOf(NullPointerException.class, ex.getCause());
    }





}
