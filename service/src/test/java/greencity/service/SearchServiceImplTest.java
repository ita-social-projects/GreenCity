package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.search.SearchNewsDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class SearchServiceImplTest {
    @InjectMocks
    private SearchServiceImpl searchService;

    @Mock
    private EcoNewsService ecoNewsService;

    @Test
    void searchTest() {
        String languageCode = "en";
        SearchNewsDto searchNewsDto = new SearchNewsDto(1L, "title", null, null, Collections.singletonList("tag"));
        PageableDto<SearchNewsDto> ecoNews = new PageableDto<>(Collections.singletonList(searchNewsDto), 4, 1, 1);

        when(ecoNewsService.search(anyString(), eq(languageCode))).thenReturn(ecoNews);

        assertEquals(ecoNews.getPage(), searchService.search("tag", languageCode).getEcoNews());
        assertEquals(Long.valueOf(ecoNews.getTotalElements()),
            searchService.search("tag", languageCode).getCountOfResults());
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

        when(ecoNewsService.search(pageRequest, "title", "en")).thenReturn(pageableDto);

        List<SearchNewsDto> expected = pageableDto.getPage();
        List<SearchNewsDto> actual = searchService.searchAllNews(pageRequest, "title", "en").getPage();

        assertEquals(expected, actual);
    }
}