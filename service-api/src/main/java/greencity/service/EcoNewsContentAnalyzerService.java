package greencity.service;

import greencity.dto.econews.EcoNewsDto;
import java.util.List;
import java.util.Set;

public interface EcoNewsContentAnalyzerService {
    Set<String> extractTags(EcoNewsDto ecoNews);
    Set<String> extractContentKeywords(EcoNewsDto ecoNews);
    Set<String> toLowerCaseSet(List<String> list);
}
