package greencity.service.impl;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.newssubscriber.NewsSubscriberResponseDto;
import greencity.entity.EcoNews;
import greencity.entity.NewsSubscriber;
import greencity.entity.localization.EcoNewsTranslation;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.repository.EcoNewsRepo;
import greencity.repository.EcoNewsTranslationRepo;
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
import org.modelmapper.TypeToken;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.multipart.MultipartFile;

@RunWith(MockitoJUnitRunner.class)
public class EcoNewsServiceImplTest {
    @Mock
    EcoNewsRepo ecoNewsRepo;

    @Mock
    EcoNewsTranslationRepo ecoNewsTranslationRepo;

    @Mock
    ModelMapper modelMapper;

    @Mock
    EmailServiceImpl emailService;

    @Mock
    NewsSubscriberRepo newsSubscriberRepo;

    @InjectMocks
    private EcoNewsServiceImpl ecoNewsService;

    private AddEcoNewsDtoRequest addEcoNewsDtoRequest =
        new AddEcoNewsDtoRequest(Collections.emptyList(), "test text", "test image path");
    private EcoNews entity =
        new EcoNews(1L, ZonedDateTime.now(), "test text", "test image path", Collections.emptyList());
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
        when(ecoNewsTranslationRepo.findByEcoNewsAndLanguageCode(entity, "en"))
            .thenReturn(new EcoNewsTranslation(1L, null, addEcoNewsDtoResponse.getTitle(), null));

        when(newsSubscriberRepo.findAll()).thenReturn(subscribersEntity);
        when(modelMapper.map(subscribersEntity, new TypeToken<List<NewsSubscriberResponseDto>>() {
        }.getType())).thenReturn(subscribers);

        when(ecoNewsRepo.save(entity)).thenReturn(entity);
        Assert.assertEquals(addEcoNewsDtoResponse, ecoNewsService.save(addEcoNewsDtoRequest, "en"));
    }

    @Test(expected = NotSavedException.class)
    public void saveThrowsNotSavedException() {
        when(modelMapper.map(addEcoNewsDtoRequest, EcoNews.class)).thenReturn(entity);
        when(ecoNewsRepo.save(entity)).thenThrow(DataIntegrityViolationException.class);
        ecoNewsService.save(addEcoNewsDtoRequest, "en");
    }

    @Test
    public void getThreeLastEcoNews() {
        ZonedDateTime zonedDateTime = ZonedDateTime.now();

        EcoNewsDto ecoNewsDto =
            new EcoNewsDto(1L, "test title", zonedDateTime, "test text", "test image path");
        EcoNewsTranslation ecoNewsTranslation =
            new EcoNewsTranslation(1L, null, "test title", null);

        List<EcoNewsDto> dtoList = Collections.singletonList(ecoNewsDto);

        when(ecoNewsTranslationRepo.getNLastEcoNewsByLanguageCode(3, "en"))
            .thenReturn(Collections.singletonList(ecoNewsTranslation));
        when(modelMapper.map(ecoNewsTranslation, EcoNewsDto.class)).thenReturn(ecoNewsDto);
        Assert.assertEquals(dtoList, ecoNewsService.getThreeLastEcoNews("en"));
    }

    @Test(expected = NotFoundException.class)
    public void getThreeLastEcoNewsNotFound() {
        List<EcoNewsTranslation> ecoNewsTranslations = new ArrayList<>();
        List<EcoNewsDto> ecoNewsDtoList = new ArrayList<>();
        when(ecoNewsTranslationRepo.getNLastEcoNewsByLanguageCode(anyInt(), anyString()))
            .thenReturn(ecoNewsTranslations);
        Assert.assertEquals(ecoNewsDtoList, ecoNewsService.getThreeLastEcoNews("en"));
    }

    @Test
    public void findAll() {
        ZonedDateTime now = ZonedDateTime.now();
        EcoNewsTranslation ecoNewsTranslation =
            new EcoNewsTranslation(1L, null, "test title", null);
        List<EcoNewsTranslation> ecoNewsTranslations = Collections.singletonList(ecoNewsTranslation);
        List<EcoNewsDto> dtoList = Collections.singletonList(
            new EcoNewsDto(1L, "test title", now, "test text", "test image path")
        );
        when(ecoNewsTranslationRepo.findAllByLanguageCode(anyString())).thenReturn(ecoNewsTranslations);
        when(modelMapper.map(ecoNewsTranslation, EcoNewsDto.class))
            .thenReturn(dtoList.get(0));
        Assert.assertEquals(dtoList, ecoNewsService.findAll("en"));
    }

    @Test
    public void findById() {
        EcoNews entity =
            new EcoNews(1L, ZonedDateTime.now(), "test text", "test image path", Collections.emptyList());
        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.of(entity));
        Assert.assertEquals(entity, ecoNewsService.findById(1L));
    }

    @Test
    public void delete() {
        doNothing().when(ecoNewsRepo).deleteById(1L);
        when(ecoNewsRepo.findById(anyLong()))
            .thenReturn(Optional.of(new EcoNews(1L, ZonedDateTime.now(), "test text",
                "test image path", Collections.emptyList())));
        ecoNewsService.delete(1L);
        verify(ecoNewsRepo, times(1)).deleteById(1L);
    }
}


