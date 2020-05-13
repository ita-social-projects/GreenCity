package greencity.service.impl;

import greencity.dto.PageableDto;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.search.SearchRequestDto;
import greencity.dto.search.SearchResponseDto;
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
     * @param page             page of request
     * @param searchRequestDto search query and sort method
     * @return {@link SearchResponseDto} with results of search
     */
    @Override
    public SearchResponseDto searchAll(Pageable page, SearchRequestDto searchRequestDto) {
//        TODO do this method
        return null;
    }
}
