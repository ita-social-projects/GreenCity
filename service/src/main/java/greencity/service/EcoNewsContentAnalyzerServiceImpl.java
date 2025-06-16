package greencity.service;

import static greencity.constant.EcoNewsContentAnalyzerConstants.*;
import greencity.dto.econews.EcoNewsDto;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class EcoNewsContentAnalyzerServiceImpl implements EcoNewsContentAnalyzerService {
    @Override
    public Set<String> extractTags(EcoNewsDto ecoNews) {
        return ecoNews.getTagsEn().stream()
            .map(String::toLowerCase)
            .collect(Collectors.toSet());
    }

    /**
     * Extracts meaningful keywords from title and content
     */
    @Override
    public Set<String> extractContentKeywords(EcoNewsDto ecoNews) {
        String content = (ecoNews.getTitle() + EMPTY_STRING + ecoNews.getContent())
            .toLowerCase();
        return Arrays.stream(content.split(REGEX_SPLIT_PATTERN))
            .filter(word -> !STOP_WORDS.contains(word) && word.length() > MIN_WORD_LENGTH)
            .collect(Collectors.toSet());
    }

    /**
     * Converts list to lowercase set
     */
    @Override
    public Set<String> toLowerCaseSet(List<String> list) {
        return list.stream()
            .map(String::toLowerCase)
            .collect(Collectors.toSet());
    }
}
