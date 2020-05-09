package greencity.service.impl;

import greencity.dto.PageableDto;
import greencity.dto.search.SearchNewsDto;
import greencity.service.EcoNewsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class SearchServiceImplTest {

    @InjectMocks
    private SearchServiceImpl searchService;

    @Mock
    private EcoNewsService ecoNewsService;

    @Test
    void searchTest() {
        SearchNewsDto searchNewsDto = new SearchNewsDto(1L, "title", null, null, Collections.singletonList("tag"));
        PageableDto<SearchNewsDto> pageableDto = new PageableDto<>(Collections.singletonList(searchNewsDto), 4, 1);

        when(ecoNewsService.search(anyString())).thenReturn(pageableDto);

        assertEquals(pageableDto.getPage(), searchService.search("test").getEcoNews());
        assertEquals(Long.valueOf(pageableDto.getTotalElements()), searchService.search("test").getCountOfResults());
    }
}