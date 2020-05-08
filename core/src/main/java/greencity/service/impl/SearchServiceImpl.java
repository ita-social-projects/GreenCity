package greencity.service.impl;

import greencity.dto.PageableDto;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.search.SearchResponseDto;
import greencity.service.EcoNewsService;
import greencity.service.SearchService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final EcoNewsService ecoNewsService;

    /**
     * Method that allow you to search {@link SearchResponseDto}.
     *
     * @param searchQuery query to search
     * @return list of {@link SearchResponseDto}
     */
    @Override
    public SearchResponseDto search(String searchQuery) {
        PageableDto<SearchNewsDto> ecoNews = ecoNewsService.search(searchQuery);

        return SearchResponseDto.builder()
            .ecoNews(ecoNews.getPage())
            .countOfResults(ecoNews.getTotalElements())
            .build();
    }
}
