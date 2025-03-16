package greencity.service;

import greencity.constant.OpenAIRequest;
import greencity.dto.habit.DurationHabitDto;
import greencity.dto.habit.ShortHabitDto;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.HabitTranslation;
import greencity.enums.HabitAssignStatus;
import greencity.repository.HabitAssignRepo;
import greencity.repository.HabitRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import java.util.Collections;
import java.util.List;
import static greencity.ModelUtils.getHabitAssign;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class AIServiceImplTest {
//    @Mock
//    private OpenAIService openAIService;
//    @Mock
//    private HabitAssignRepo habitAssignRepo;
//    @Mock
//    private HabitRepo habitRepo;
//    @Mock
//    private ModelMapper modelMapper;
//    @InjectMocks
//    private AIServiceImpl aiServiceImpl;
//    private HabitAssign habitAssign = getHabitAssign(HabitAssignStatus.INPROGRESS);
//    private DurationHabitDto durationHabitDto = new DurationHabitDto("", 0L);
//    private Long id = 1L;
//    private String language = "en";
//    private final String habitTranslationName = "Test Habit";
//    private final HabitTranslation habitTranslation = HabitTranslation.builder().name(habitTranslationName).build();
//
//    private final Habit habit = Habit.builder()
//        .id(id)
//        .habitTranslations(Collections.singletonList(habitTranslation))
//        .build();
//    private final ShortHabitDto shortHabitDto = new ShortHabitDto(id, habitTranslationName);
//
//    @Test
//    void getForecastReturnsResponseFromOpenAIServiceTest() {
//        when(habitAssignRepo.findAllByUserId(id)).thenReturn(List.of(habitAssign));
//        when(modelMapper.map(habitAssign, DurationHabitDto.class)).thenReturn(durationHabitDto);
//        when(openAIService.makeRequest("en" + OpenAIRequest.FORECAST + List.of(durationHabitDto)))
//            .thenReturn("Forecast Response");
//
//        String result = aiServiceImpl.getForecast(id, language);
//
//        assertThat(result).isEqualTo("Forecast Response");
//        verify(habitAssignRepo).findAllByUserId(id);
//        verify(modelMapper).map(habitAssign, DurationHabitDto.class);
//        verify(openAIService).makeRequest("en" + OpenAIRequest.FORECAST + List.of(durationHabitDto));
//    }
//
//    @Test
//    void getForecastThrowsExceptionWhenOpenAIServiceFailsTest() {
//        when(habitAssignRepo.findAllByUserId(id)).thenReturn(List.of(habitAssign));
//        when(modelMapper.map(habitAssign, DurationHabitDto.class)).thenReturn(durationHabitDto);
//        when(openAIService.makeRequest("en" + OpenAIRequest.FORECAST + List.of(durationHabitDto)))
//            .thenThrow(new RuntimeException("OpenAI Service Failed"));
//
//        assertThrows(RuntimeException.class, () -> aiServiceImpl.getForecast(id, language));
//
//        verify(habitAssignRepo).findAllByUserId(id);
//        verify(modelMapper).map(habitAssign, DurationHabitDto.class);
//        verify(openAIService).makeRequest("en" + OpenAIRequest.FORECAST + List.of(durationHabitDto));
//    }
//
//    @Test
//    void getAdviceReturnsResponseFromOpenAIServiceTest() {
//        when(habitRepo.findRandomHabit()).thenReturn(habit);
//        when(modelMapper.map(habit, ShortHabitDto.class)).thenReturn(shortHabitDto);
//        when(openAIService.makeRequest("en" + OpenAIRequest.ADVICE + shortHabitDto))
//            .thenReturn("Advice Response");
//
//        String result = aiServiceImpl.getAdvice(id, language);
//
//        assertThat(result).isEqualTo("Advice Response");
//        verify(habitRepo).findRandomHabit();
//        verify(modelMapper).map(habit, ShortHabitDto.class);
//        verify(openAIService).makeRequest("en" + OpenAIRequest.ADVICE + shortHabitDto);
//    }
//
//    @Test
//    void getAdviceThrowsExceptionWhenOpenAIServiceFailsTest() {
//        when(habitRepo.findRandomHabit()).thenReturn(habit);
//        when(modelMapper.map(habit, ShortHabitDto.class)).thenReturn(shortHabitDto);
//        when(openAIService.makeRequest("en" + OpenAIRequest.ADVICE + shortHabitDto))
//            .thenThrow(new RuntimeException("OpenAI Service Failed"));
//
//        assertThrows(RuntimeException.class, () -> aiServiceImpl.getAdvice(id, language));
//
//        verify(habitRepo).findRandomHabit();
//        verify(modelMapper).map(habit, ShortHabitDto.class);
//        verify(openAIService).makeRequest("en" + OpenAIRequest.ADVICE + shortHabitDto);
//    }
//
//    @Test
//    void getNewsShouldReturnWithoutQueryWhenQueryIsNullTest() {
//        String expectedResponse = "Mocked Response";
//        when(openAIService.makeRequest(language + OpenAIRequest.NEWS_WITHOUT_QUERY))
//            .thenReturn(expectedResponse);
//
//        String actualResponse = aiServiceImpl.getNews(language, null);
//
//        assertEquals(expectedResponse, actualResponse);
//        verify(openAIService, times(1))
//            .makeRequest(language + OpenAIRequest.NEWS_WITHOUT_QUERY);
//        verifyNoMoreInteractions(openAIService);
//    }
//
//    @Test
//    void getNewsShouldReturnWithQueryWhenQueryIsNotNullTest() {
//        String query = "climate change";
//        String expectedResponse = "Mocked Response";
//        when(openAIService.makeRequest(language + OpenAIRequest.NEWS_BY_QUERY + query))
//            .thenReturn(expectedResponse);
//
//        String actualResponse = aiServiceImpl.getNews(language, query);
//
//        assertEquals(expectedResponse, actualResponse);
//        verify(openAIService, times(1))
//            .makeRequest(language + OpenAIRequest.NEWS_BY_QUERY + query);
//        verifyNoMoreInteractions(openAIService);
//    }
//
//    @Test
//    void generateEcoNewsBasedOnHabitsReturnsNewsByHabitsWhenHabitAssignsIsNotEmptyTest() {
//        habit.setHabitAssigns(Collections.singletonList(habitAssign));
//        when(habitAssignRepo.findAllByUserId(id)).thenReturn(List.of(habitAssign));
//        when(openAIService.makeRequest(anyString())).thenReturn("News By Habits Response");
//
//        String result = aiServiceImpl.generateEcoNewsBasedOnHabits(id, language);
//
//        assertThat(result).isEqualTo("News By Habits Response");
//        verify(habitAssignRepo).findAllByUserId(id);
//        verify(openAIService).makeRequest(anyString());
//    }
//
//    @Test
//    void generateEcoNewsBasedOnHabitsReturnsNewsByMultipleHabitsTest() {
//        HabitTranslation habitTranslation1 = new HabitTranslation();
//        habitTranslation1.setName("Habit 1");
//        Habit habit1 = new Habit();
//        habit1.setHabitTranslations(Collections.singletonList(habitTranslation1));
//
//        HabitTranslation habitTranslation2 = new HabitTranslation();
//        habitTranslation2.setName("Habit 2");
//        Habit habit2 = new Habit();
//        habit2.setHabitTranslations(Collections.singletonList(habitTranslation2));
//
//        HabitAssign habitAssign1 = new HabitAssign();
//        habitAssign1.setHabit(habit1);
//        HabitAssign habitAssign2 = new HabitAssign();
//        habitAssign2.setHabit(habit2);
//
//        List<HabitAssign> habitAssigns = List.of(habitAssign1, habitAssign2);
//
//        when(habitAssignRepo.findAllByUserId(id)).thenReturn(habitAssigns);
//        when(openAIService.makeRequest(anyString())).thenReturn("News By Multiple Habits Response");
//
//        String result = aiServiceImpl.generateEcoNewsBasedOnHabits(id, language);
//
//        assertThat(result).isEqualTo("News By Multiple Habits Response");
//        verify(habitAssignRepo).findAllByUserId(id);
//        verify(openAIService).makeRequest(anyString());
//    }
//
//    @Test
//    void generateEcoNewsBasedOnHabitsThrowsExceptionWhenHabitHasNoTranslationsTest() {
//        habit.setHabitTranslations(Collections.emptyList());
//        habitAssign.setHabit(habit);
//        List<HabitAssign> habitAssigns = List.of(habitAssign);
//
//        when(habitAssignRepo.findAllByUserId(id)).thenReturn(habitAssigns);
//
//        String result = assertThrows(IllegalArgumentException.class,
//            () -> aiServiceImpl.generateEcoNewsBasedOnHabits(id, language)).getMessage();
//
//        assertThat(result).isEqualTo("Habit must have valid HabitTranslations");
//
//        verify(habitAssignRepo).findAllByUserId(id);
//        verify(openAIService, times(0)).makeRequest(anyString());
//    }
//
//
//    @Test
//    void generateEcoNewsBasedOnHabitsReturnsNewsByHabitsWithMultipleTranslationsTest() {
//        HabitTranslation habitTranslation1 = new HabitTranslation();
//        habitTranslation1.setName("Habit Translation 1");
//        HabitTranslation habitTranslation2 = new HabitTranslation();
//        habitTranslation2.setName("Habit Translation 2");
//        habit.setHabitTranslations(List.of(habitTranslation1, habitTranslation2));
//        habitAssign.setHabit(habit);
//        List<HabitAssign> habitAssigns = List.of(habitAssign);
//
//        when(habitAssignRepo.findAllByUserId(id)).thenReturn(habitAssigns);
//        when(openAIService.makeRequest(anyString())).thenReturn("News By Habits With Multiple Translations Response");
//
//        String result = aiServiceImpl.generateEcoNewsBasedOnHabits(id, language);
//
//        assertThat(result).isEqualTo("News By Habits With Multiple Translations Response");
//        verify(habitAssignRepo).findAllByUserId(id);
//        verify(openAIService).makeRequest(anyString());
//    }
//
//    @Test
//    void generateEcoNewsBasedOnHabitsThrowsExceptionWhenHabitHasNullTranslationTest() {
//        habit.setHabitTranslations(Collections.singletonList(null));
//        habitAssign.setHabit(habit);
//        List<HabitAssign> habitAssigns = List.of(habitAssign);
//
//        when(habitAssignRepo.findAllByUserId(id)).thenReturn(habitAssigns);
//
//        String result = assertThrows(IllegalArgumentException.class,
//            () -> aiServiceImpl.generateEcoNewsBasedOnHabits(id, language)).getMessage();
//
//        assertThat(result).isEqualTo("Habit must have valid HabitTranslations");
//
//        verify(habitAssignRepo).findAllByUserId(id);
//        verify(openAIService, times(0)).makeRequest(anyString());
//    }
//
//    @Test
//    void generateEcoNewsBasedOnHabitsThrowsExceptionWhenUserIdIsNullTest() {
//        assertThrows(IllegalArgumentException.class,
//            () -> aiServiceImpl.generateEcoNewsBasedOnHabits(null, language));
//    }
//
//    @Test
//    void generateEcoNewsBasedOnHabitsThrowsExceptionWhenLanguageIsNullTest() {
//        assertThrows(IllegalArgumentException.class,
//            () -> aiServiceImpl.generateEcoNewsBasedOnHabits(id, null));
//    }
}