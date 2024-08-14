package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.search.SearchEventsDto;
import greencity.dto.search.SearchNewsDto;
import greencity.repository.EcoNewsRepo;
import greencity.repository.EventRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static greencity.ModelUtils.getSearchEvents;
import static greencity.ModelUtils.getSearchNews;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class SearchServiceImplTest {
    @InjectMocks
    private SearchServiceImpl searchService;

    @Mock
    private EcoNewsService ecoNewsService;

    @Mock
    private EventService eventService;

    @Mock
    private EcoNewsRepo ecoNewsRepo;

    @Mock
    private EventRepo eventRepo;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void searchTest() {
        String languageCode = "en";
        SearchNewsDto searchNewsDto = getSearchNews();
        var ecoNews = new PageableDto<>(Collections.singletonList(searchNewsDto), 4, 1, 1);

        SearchEventsDto searchEventsDto = getSearchEvents();
        var events = new PageableDto<>(Collections.singletonList(searchEventsDto), 4, 1, 1);

        when(ecoNewsService.search(anyString(), eq(languageCode))).thenReturn(ecoNews);
        when(eventService.search(anyString(), eq(languageCode))).thenReturn(events);

        assertEquals(ecoNews.getPage(), searchService.search("tag", languageCode).getEcoNews());
        assertEquals(Long.valueOf(ecoNews.getTotalElements()),
            searchService.search("tag", languageCode).getCountOfEcoNewsResults());

        assertEquals(events.getPage(), searchService.search("event", languageCode).getEvents());
        assertEquals(Long.valueOf(events.getTotalElements()),
            searchService.search("event", languageCode).getCountOfEcoNewsResults());

        verify(ecoNewsService, times(4)).search(anyString(), eq(languageCode));
        verify(eventService, times(4)).search(anyString(), eq(languageCode));
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