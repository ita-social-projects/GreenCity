package greencity.service;

import greencity.constant.OpenAIRequest;
import greencity.dto.habit.DurationHabitDto;
import greencity.dto.habit.ShortHabitDto;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.repository.HabitAssignRepo;
import greencity.repository.HabitRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class AIServiceImpl implements AIService {
    private final OpenAIService openAIService;
    private final HabitAssignRepo habitAssignRepo;
    private final HabitRepo habitRepo;
    private final ModelMapper modelMapper;

    @Override
    public String getForecast(Long userId, String language) {
        List<HabitAssign> habitAssigns = habitAssignRepo.findAllByUserId(userId);
        if (habitAssigns.isEmpty()) {
            return getAdvice(userId, language);
        }
        List<DurationHabitDto> durationHabitDtos = habitAssigns.stream()
            .map(habitAssign -> modelMapper.map(habitAssign, DurationHabitDto.class)).toList();
        return openAIService.makeRequest(language + OpenAIRequest.FORECAST + durationHabitDtos);
    }

    @Override
    public String getAdvice(Long userId, String language) {
        Habit habit = habitRepo.findRandomHabit();
        ShortHabitDto shortHabitDto = modelMapper.map(habit, ShortHabitDto.class);
        return openAIService.makeRequest(language + OpenAIRequest.ADVICE + shortHabitDto);
    }

    @Override
    public String getNews(String language, String query) {
        return query == null ? openAIService.makeRequest(language + OpenAIRequest.NEWS_WITHOUT_QUERY)
            : openAIService.makeRequest(language + OpenAIRequest.NEWS_BY_QUERY + query);
    }

    @Override
    public String getEcoFact(Long userId, String language) {
        List<ShortHabitDto> shortHabitDtos = habitAssignRepo.findAllByUserId(userId).stream()
            .map((habitAssign -> modelMapper.map(habitAssign.getHabit(), ShortHabitDto.class))).toList();
        if (shortHabitDtos.isEmpty()) {
            return openAIService.makeRequest(language + OpenAIRequest.ECO_FACT);
        }
        return openAIService.makeRequest(language + OpenAIRequest.ECO_FACT_BY_HABITS + shortHabitDtos);
    }

    @Override
    public String getEcoFact(String language, String query) {
        return query == null ? openAIService.makeRequest(language + OpenAIRequest.ECO_FACT)
            : openAIService.makeRequest(language + OpenAIRequest.ECO_FACT_BY_QUERY + query);
    }
}
