package greencity.service.impl;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.newssubscriber.NewsSubscriberResponseDto;
import greencity.entity.EcoNews;
import greencity.entity.NewsSubscriber;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.EcoNewsRepo;
import greencity.repository.NewsSubscriberRepo;
import java.time.ZonedDateTime;
import java.util.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

@RunWith(MockitoJUnitRunner.class)
public class EcoNewsServiceImplTest {

    @Mock
    EcoNewsRepo ecoNewsRepo;

    @Mock
    ModelMapper modelMapper;

    @Mock
    EmailServiceImpl emailService;

    @Mock
    NewsSubscriberRepo newsSubscriberRepo;

    @InjectMocks
    private EcoNewsServiceImpl ecoNewsService;

    private AddEcoNewsDtoRequest addEcoNewsDtoRequest =
        new AddEcoNewsDtoRequest("test title", "test text", "test image path");
    private EcoNews entity =
        new EcoNews(1L, "test title", ZonedDateTime.now(), "test text", "test image path");
    private AddEcoNewsDtoResponse addEcoNewsDtoResponse =
        new AddEcoNewsDtoResponse("test title", "test text", ZonedDateTime.now(), "test image path");

    private List<NewsSubscriberResponseDto> subscribers = Arrays.asList(
        new NewsSubscriberResponseDto("test1@mail.ua", "test token"),
        new NewsSubscriberResponseDto("test2@mail.ua", "test token1"));
    private List<NewsSubscriber> subscribersEntity = Arrays.asList(
        new NewsSubscriber(1L, "test1@mail.ua", "test token"),
        new NewsSubscriber(2L, "test2@mail.ua", "test token1"));


    @Test
    public void save() {
        when(modelMapper.map(addEcoNewsDtoRequest, EcoNews.class)).thenReturn(entity);
        when(modelMapper.map(entity, AddEcoNewsDtoResponse.class)).thenReturn(addEcoNewsDtoResponse);
        when(ecoNewsRepo.save(entity)).thenReturn(entity);

        Assert.assertEquals(addEcoNewsDtoResponse, ecoNewsService.save(addEcoNewsDtoRequest));
    }

    @Test
    public void getThreeLastEcoNews() {
        ZonedDateTime zonedDateTime = ZonedDateTime.now();

        EcoNews entity =
            new EcoNews(1L, "test title", zonedDateTime, "test text", "test image path");
        EcoNewsDto ecoNewsDto =
            new EcoNewsDto(1L, "test title", zonedDateTime, "test text", "test image path");

        List<EcoNews> entityList = Collections.singletonList(entity);
        List<EcoNewsDto> dtoList = Collections.singletonList(ecoNewsDto);

        when(ecoNewsRepo.getThreeLastEcoNews()).thenReturn(entityList);
        when(modelMapper.map(entity, EcoNewsDto.class)).thenReturn(ecoNewsDto);
        Assert.assertEquals(dtoList, ecoNewsService.getThreeLastEcoNews());
    }

    @Test(expected = NotFoundException.class)
    public void getThreeLastEcoNewsNotFound() {
        List<EcoNews> ecoNewsList = new ArrayList<>();
        List<EcoNewsDto> ecoNewsDtoList = new ArrayList<>();
        when(ecoNewsRepo.getThreeLastEcoNews()).thenReturn(ecoNewsList);
        Assert.assertEquals(ecoNewsDtoList, ecoNewsService.getThreeLastEcoNews());
    }

    @Test
    public void findAll() {
        ZonedDateTime now = ZonedDateTime.now();
        EcoNews entity =
            new EcoNews(1L, "test title", now, "test text", "test image path");
        List<EcoNews> entityList = Collections.singletonList(entity);
        List<EcoNewsDto> dtoList = Collections.singletonList(
            new EcoNewsDto(1L, "test title", now, "test text", "test image path")
        );
        when(ecoNewsRepo.findAll()).thenReturn(entityList);
        when(modelMapper.map(entity, EcoNewsDto.class))
            .thenReturn(new EcoNewsDto(1L, "test title", now, "test text", "test image path"));
        Assert.assertEquals(dtoList, ecoNewsService.findAll());

    }

    @Test
    public void findById() {
        EcoNews entity =
            new EcoNews(1L, "test title", ZonedDateTime.now(), "test text", "test image path");
        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.of(entity));
        Assert.assertEquals(entity, ecoNewsService.findById(1L));
    }

    @Test
    public void delete() {
        doNothing().when(ecoNewsRepo).deleteById(1L);
        when(ecoNewsRepo.findById(anyLong()))
            .thenReturn(Optional.of(new EcoNews(1L, "test title", ZonedDateTime.now(), "test text", "test image path")));
        ecoNewsService.delete(1L);
        verify(ecoNewsRepo, times(1)).deleteById(1L);
    }
}


