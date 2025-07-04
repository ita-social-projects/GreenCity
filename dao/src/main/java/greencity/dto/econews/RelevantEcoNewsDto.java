package greencity.dto.econews;

import greencity.entity.EcoNews;
import greencity.entity.EcoNewsRelevance;

public record RelevantEcoNewsDto(
    EcoNews ecoNews,
    EcoNewsRelevance ecoNewsRelevance
) {
}
