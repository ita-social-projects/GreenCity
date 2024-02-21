package greencity.dto.search;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import java.util.List;

@Getter
@Builder
@EqualsAndHashCode
@ToString
public class SearchResponseDto {
    List<SearchNewsDto> ecoNews;

    List<SearchEventsDto> events;

    Long countOfEcoNewsResults;

    Long countOfEventsResults;
}
