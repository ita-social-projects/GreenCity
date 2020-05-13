package greencity.service.impl;

import greencity.dto.PageableDto;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.search.SearchRequestDto;
import greencity.dto.search.SearchResponseDto;
import greencity.dto.search.SortingType;
import greencity.service.EcoNewsService;
import greencity.service.SearchService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        PageableDto<SearchNewsDto> ecoNews = ecoNewsService.search(searchQuery, PageRequest.of(0, 3));

        return SearchResponseDto.builder()
            .ecoNews(ecoNews.getPage())
            .countOfResults(ecoNews.getTotalElements())
            .build();
    }

    /**
     * Method that allow you to search all results in {@link SearchResponseDto} by page .
     *
     * @param page  page of request
     * @param query search query
     * @return {@link SearchResponseDto} with results of search
     */
    @Override
    public SearchResponseDto search(Pageable page, String query, SortingType type) {
        SearchRequestDto searchRequestDto = new SearchRequestDto(query, type);
        PageableDto<SearchNewsDto> ecoNews = ecoNewsService.search(searchRequestDto, page);

        return SearchResponseDto.builder()
            .ecoNews(ecoNews.getPage())
            .countOfResults(ecoNews.getTotalElements())
            .build();
    }
}
