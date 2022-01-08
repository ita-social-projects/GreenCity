package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.search.SearchResponseDto;
import greencity.dto.search.SearchTipsAndTricksDto;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final EcoNewsService ecoNewsService;
    private final TipsAndTricksService tipsAndTricksService;

    /**
     * Method that allow you to search {@link SearchResponseDto}.
     *
     * @param searchQuery query to search
     * @return list of {@link SearchResponseDto} and {@link SearchTipsAndTricksDto}
     */
    @Override
    public SearchResponseDto search(String searchQuery, String languageCode) {
        PageableDto<SearchNewsDto> ecoNews = ecoNewsService.search(searchQuery, languageCode);
//        PageableDto<SearchTipsAndTricksDto> tipsAndTricks = tipsAndTricksService.search(searchQuery, languageCode);

        return SearchResponseDto.builder()
            .ecoNews(ecoNews.getPage())
//            .tipsAndTricks(tipsAndTricks.getPage())
            .countOfResults(ecoNews.getTotalElements() /*+ tipsAndTricks.getTotalElements()*/)
            .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<SearchNewsDto> searchAllNews(Pageable pageable, String searchQuery, String languageCode) {
        return ecoNewsService.search(pageable, searchQuery, languageCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<SearchTipsAndTricksDto> searchAllTipsAndTricks(Pageable pageable, String searchQuery,
        String languageCode) {
        return tipsAndTricksService.search(pageable, searchQuery, languageCode);
    }
}
