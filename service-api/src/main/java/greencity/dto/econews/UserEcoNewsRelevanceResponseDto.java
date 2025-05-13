package greencity.dto.econews;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserEcoNewsRelevanceResponseDto {
    @NotNull(message = "greenCity.relevance.user_id_not_null")
    private Long userId;
    @NotNull(message = "greenCity.relevance.eco_news_id_not_null")
    private Long ecoNewsId;
    @Min(value = 0, message = "greenCity.relevance.min_relevance")
    @Max(value = 1, message = "greenCity.relevance.max_relevance")
    private Double relevance;
}
