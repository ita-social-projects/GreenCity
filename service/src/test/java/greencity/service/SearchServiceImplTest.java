package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.search.SearchEventsDto;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.search.SearchResponseDto;
import greencity.entity.EcoNews;
import greencity.entity.event.Event;
import greencity.repository.EcoNewsRepo;
import greencity.repository.EventRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static greencity.ModelUtils.getEcoNews;
import static greencity.ModelUtils.getEvent;
import static greencity.ModelUtils.getSearchEvents;
import static greencity.ModelUtils.getSearchNews;
import static greencity.ModelUtils.getSearchResponseDto;
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

    @Test
    void searchAllEventsTest() {
        PageRequest pageRequest = PageRequest.of(0, 2);
        List<SearchEventsDto> searchDto = Collections.singletonList(getSearchEvents());
        var pageableDto = new PageableDto<>(searchDto, searchDto.size(), 0, 1);

        when(eventService.search(pageRequest, "title", "en")).thenReturn(pageableDto);

        List<SearchEventsDto> expected = pageableDto.getPage();
        List<SearchEventsDto> actual = searchService.searchAllEvents(pageRequest, "title", "en").getPage();

        assertEquals(expected, actual);
        verify(eventService).search(pageRequest, "title", "en");
    }

    @Test
    void searchByFunctionQueryTest() {
        Event event = getEvent();
        EcoNews ecoNews = getEcoNews();

        SearchNewsDto searchNewsDto = getSearchNews();
        SearchEventsDto searchEventsDto = getSearchEvents();
        SearchResponseDto dto = getSearchResponseDto();

        var eventsPage = new PageImpl<>(Collections.singletonList(event), PageRequest.of(1, 3), 1);
        var ecoNewsPage = new PageImpl<>(Collections.singletonList(ecoNews), PageRequest.of(1, 3), 1);

        when(ecoNewsRepo.searchEcoNews(PageRequest.of(0, 3), "query", "ua")).thenReturn(ecoNewsPage);
        when(eventRepo.searchEvents(PageRequest.of(0, 3), "query", "ua")).thenReturn(eventsPage);

        when(modelMapper.map(ecoNews, SearchNewsDto.class)).thenReturn(searchNewsDto);
        when(modelMapper.map(event, SearchEventsDto.class)).thenReturn(searchEventsDto);

        SearchResponseDto searchResponseDto = searchService
            .searchByFunctionQuery(PageRequest.of(0, 3), "query", "ua");
        assertEquals(searchResponseDto, dto);

        verify(ecoNewsRepo).searchEcoNews(PageRequest.of(0, 3), "query", "ua");
        verify(eventRepo).searchEvents(PageRequest.of(0, 3), "query", "ua");
        verify(modelMapper).map(ecoNews, SearchNewsDto.class);
        verify(modelMapper).map(event, SearchEventsDto.class);
    }
}