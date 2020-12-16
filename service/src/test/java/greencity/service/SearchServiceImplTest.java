/*package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.search.SearchTipsAndTricksDto;
import greencity.dto.user.AuthorDto;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
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
        PageableDto<SearchNewsDto> ecoNews = new PageableDto<>(Collections.singletonList(searchNewsDto), 4, 1, 1);
        SearchTipsAndTricksDto searchTipsAndTricksDto =
            new SearchTipsAndTricksDto(1L, "title", null, null, Collections.singletonList("tips_tag"));
        PageableDto<SearchTipsAndTricksDto> tipsAndTricks =
            new PageableDto<>(Collections.singletonList(searchTipsAndTricksDto), 4, 1, 1);

        when(ecoNewsService.search(anyString())).thenReturn(ecoNews);
        when(tipsAndTricksService.search(anyString())).thenReturn(tipsAndTricks);

        assertEquals(ecoNews.getPage(), searchService.search("tag").getEcoNews());
        assertEquals(tipsAndTricks.getPage(), searchService.search("tag").getTipsAndTricks());
        assertEquals(Long.valueOf(ecoNews.getTotalElements() + tipsAndTricks.getTotalElements()),
            searchService.search("tag").getCountOfResults());
    }

    @Test
    void searchTipsAndTricksTest() {
        PageRequest pageRequest = PageRequest.of(0, 2);
        AuthorDto author = new AuthorDto(1L, "Author");
        List<SearchTipsAndTricksDto> searchDto =
            Collections.singletonList(
                new SearchTipsAndTricksDto(1L, "title", author, null, null));
        PageableDto<SearchTipsAndTricksDto> pageableDto =
            new PageableDto<>(searchDto, searchDto.size(), 0, 1);

        when(tipsAndTricksService.search(pageRequest, "Author")).thenReturn(pageableDto);

        List<SearchTipsAndTricksDto> expected = pageableDto.getPage();
        List<SearchTipsAndTricksDto> actual =
            searchService.searchAllTipsAndTricks(pageRequest, "Author").getPage();

        assertEquals(expected, actual);
    }

    @Test
    void searchEcoNewsTest() {
        PageRequest pageRequest = PageRequest.of(0, 2);
        List<SearchNewsDto> searchDto =
            Arrays.asList(
                new SearchNewsDto(1L, "title", null, null, null),
                new SearchNewsDto(2L, "title", null, null, null));
        PageableDto<SearchNewsDto> pageableDto =
            new PageableDto<>(searchDto, searchDto.size(), 0, 1);

        when(ecoNewsService.search(pageRequest, "title")).thenReturn(pageableDto);

        List<SearchNewsDto> expected = pageableDto.getPage();
        List<SearchNewsDto> actual = searchService.searchAllNews(pageRequest, "title").getPage();

        assertEquals(expected, actual);
    }
}*/