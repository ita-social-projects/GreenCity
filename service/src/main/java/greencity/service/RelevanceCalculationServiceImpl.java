package greencity.service;

import static greencity.constant.AIEcoNewsRelevanceConstants.COMMA_SEPARATOR;
import static greencity.constant.AIEcoNewsRelevanceConstants.NONE_STRING;
import static greencity.constant.OpenAIConstants.SCORE_PATTERN;
import static greencity.constant.OpenAIRequest.RELEVANCE_PROMPT_TEMPLATE;
import greencity.dto.econews.EcoNewsDto;
import greencity.enums.Language;
import greencity.exception.exceptions.OpenAIServiceException;
import greencity.repository.UserEcoNewsRelevanceRepo;
import java.util.List;
import java.util.regex.Matcher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RelevanceCalculationServiceImpl implements RelevanceCalculationService {
    private final UserEcoNewsRelevanceRepo userEcoNewsRelevanceRepo;
    private final OpenAIService openAIService;

    @Value("${default.score}")
    private double defaultScore;
    @Value("${min.score}")
    private double minScore;
    @Value("${max.score}")
    private double maxScore;

    @Override
    public double calculateRelevanceScore(EcoNewsDto news, List<String> habits, String language) {
        if (news == null || habits == null || habits.isEmpty()) {
            return defaultScore;
        }
        return calculateAIRelevanceScore(news, habits, news.getTagsEn());
    }

    @Override
    @Retryable(retryFor = OpenAIServiceException.class)
    public double calculateAIRelevanceScore(EcoNewsDto news, List<String> habits, List<String> tags) {
        if (news == null || news.getTitle() == null || news.getContent() == null || habits == null || tags == null) {
            return defaultScore;
        }

        String prompt = String.format(
            RELEVANCE_PROMPT_TEMPLATE,
            news.getTitle(),
            news.getContent(),
            habits.isEmpty() ? NONE_STRING : String.join(COMMA_SEPARATOR, habits),
            tags.isEmpty() ? NONE_STRING : String.join(COMMA_SEPARATOR, tags)
        );
        String aiResponse = openAIService.makeRequest(Language.ENGLISH, prompt);
        return parseScoreFromResponse(aiResponse);
    }

    @Override
    public List<Double> findScoresByNewsId(Long newsId) {
        return userEcoNewsRelevanceRepo.findRelevanceScoresByEcoNewsId(newsId);
    }

    private double parseScoreFromResponse(String response) {
        if (response == null || response.trim().isEmpty()) {
            return defaultScore;
        }

        return clampScore(response, minScore, maxScore, defaultScore);
    }

    static double clampScore(String response, double minScore, double maxScore, double defaultScore) {
        try {
            Matcher matcher = SCORE_PATTERN.matcher(response.trim());
            if (matcher.matches()) {
                double score = Double.parseDouble(response);
                return Math.max(minScore, Math.min(maxScore, score));
            } else {
                return defaultScore;
            }
        } catch (NumberFormatException e) {
            return defaultScore;
        }
    }
}