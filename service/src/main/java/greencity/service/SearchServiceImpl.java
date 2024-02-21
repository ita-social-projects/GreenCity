package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.search.SearchEventsDto;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.search.SearchResponseDto;
import greencity.entity.EcoNews;
import greencity.entity.event.Event;
import greencity.repository.EcoNewsRepo;
import greencity.repository.EventRepo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final EcoNewsService ecoNewsService;
    private final EventService eventService;
    private final EcoNewsRepo ecoNewsRepo;
    private final EventRepo eventRepo;
    private final ModelMapper modelMapper;

    /**
     * Method that allow you to search {@link SearchResponseDto}.
     *
     * @param searchQuery query to search
     * @return list of {@link SearchResponseDto}
     */
    @Override
    public SearchResponseDto search(String searchQuery, String languageCode) {
        PageableDto<SearchNewsDto> ecoNews = ecoNewsService.search(searchQuery, languageCode);
        PageableDto<SearchEventsDto> events = eventService.search(searchQuery, languageCode);

        return SearchResponseDto.builder()
            .ecoNews(ecoNews.getPage())
            .events(events.getPage())
            .countOfEcoNewsResults(ecoNews.getTotalElements())
            .countOfEventsResults(events.getTotalElements())
            .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<SearchNewsDto> searchAllNews(Pageable pageable, String searchQuery, String languageCode) {
        return ecoNewsService.search(pageable, searchQuery, languageCode);
    }

    @Override
    public PageableDto<SearchEventsDto> searchAllEvents(Pageable pageable, String searchQuery, String languageCode) {
        return eventService.search(pageable, searchQuery, languageCode);
    }

    @Override
    public SearchResponseDto searchByFunctionQuery(Pageable pageable, String searchQuery, String languageCode) {
        Page<EcoNews> ecoNews = ecoNewsRepo.searchEcoNews(pageable, searchQuery, languageCode);
        Page<Event> events = eventRepo.searchEvents(pageable, searchQuery, languageCode);

        List<SearchNewsDto> searchNews = ecoNews
            .getContent()
            .stream()
            .map(e -> modelMapper.map(e, SearchNewsDto.class))
            .collect(Collectors.toList());

        List<SearchEventsDto> searchEvents = events
            .getContent()
            .stream()
            .map(e -> modelMapper.map(e, SearchEventsDto.class))
            .collect(Collectors.toList());

        return SearchResponseDto.builder()
            .ecoNews(searchNews)
            .events(searchEvents)
            .countOfEcoNewsResults(ecoNews.getTotalElements())
            .countOfEventsResults(events.getTotalElements())
            .build();
    }
}
