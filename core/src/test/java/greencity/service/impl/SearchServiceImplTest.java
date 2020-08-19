package greencity.service.impl;

import greencity.dto.PageableDto;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.search.SearchTipsAndTricksDto;
import greencity.service.EcoNewsService;
import greencity.service.TipsAndTricksService;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class SearchServiceImplTest {
    @InjectMocks
    private SearchServiceImpl searchService;

    @Mock
    private EcoNewsService ecoNewsService;

    @Mock
    private TipsAndTricksService tipsAndTricksService;

    @Test
    void searchTest() {
        SearchNewsDto searchNewsDto = new SearchNewsDto(1L, "title", null, null, Collections.singletonList("tag"));
        PageableDto<SearchNewsDto> ecoNews = new PageableDto<>(Collections.singletonList(searchNewsDto), 4, 1,1);
        SearchTipsAndTricksDto searchTipsAndTricksDto =
            new SearchTipsAndTricksDto(1L, "title", null, null, Collections.singletonList("tips_tag"));
        PageableDto<SearchTipsAndTricksDto> tipsAndTricks =
            new PageableDto<>(Collections.singletonList(searchTipsAndTricksDto), 4, 1,1);

        when(ecoNewsService.search(anyString())).thenReturn(ecoNews);
        when(tipsAndTricksService.search(anyString())).thenReturn(tipsAndTricks);

        assertEquals(ecoNews.getPage(), searchService.search("tag").getEcoNews());
        assertEquals(tipsAndTricks.getPage(), searchService.search("tag").getTipsAndTricks());
        assertEquals(Long.valueOf(ecoNews.getTotalElements() + tipsAndTricks.getTotalElements()),
            searchService.search("tag").getCountOfResults());
    }
}