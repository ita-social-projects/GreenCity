package greencity.service.impl;

import greencity.constant.AppConstant;
import greencity.constant.RabbitConstants;
import greencity.dto.PageableDto;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.tag.TagDto;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.entity.EcoNews;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.entity.enums.ROLE;
import greencity.entity.localization.EcoNewsTranslation;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.mapping.EcoNewsAuthorDtoMapper;
import greencity.message.AddEcoNewsMessage;
import greencity.repository.EcoNewsRepo;
import greencity.repository.EcoNewsTranslationRepo;
import greencity.service.NewsSubscriberService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EcoNewsServiceImplTest {
    @Mock
    EcoNewsRepo ecoNewsRepo;

    @Mock
    EcoNewsTranslationRepo ecoNewsTranslationRepo;

    @Mock
    ModelMapper modelMapper;

    @Mock
    RabbitTemplate rabbitTemplate;

    @Mock
    NewsSubscriberService newsSubscriberService;

    @InjectMocks
    private EcoNewsServiceImpl ecoNewsService;

    private User author =
            User.builder()
                    .id(1L)
                    .email("Nazar.stasyuk@gmail.com")
                    .firstName("Nazar")
                    .lastName("Stasyuk")
                    .role(ROLE.ROLE_USER)
                    .lastVisit(LocalDateTime.now())
                    .dateOfRegistration(LocalDateTime.now())
                    .build();

    private EcoNewsAuthorDtoMapper ecoNewsAuthorDtoMapper = new EcoNewsAuthorDtoMapper();

    private EcoNewsAuthorDto ecoNewsAuthorDto = ecoNewsAuthorDtoMapper.convert(author);

    private AddEcoNewsDtoRequest addEcoNewsDtoRequest =
            new AddEcoNewsDtoRequest(Collections.emptyList(), "test image path");
    private EcoNews entity =
            new EcoNews(1L, ZonedDateTime.now(), "test image path", author, Collections.emptyList(), new ArrayList<Tag>());
    private AddEcoNewsDtoResponse addEcoNewsDtoResponse =
            new AddEcoNewsDtoResponse(1L, "test title", "test text", ecoNewsAuthorDto, ZonedDateTime.now(), "test image path");

    @Test
    public void save() {
        when(modelMapper.map(addEcoNewsDtoRequest, EcoNews.class)).thenReturn(entity);
        when(modelMapper.map(entity, AddEcoNewsDtoResponse.class)).thenReturn(addEcoNewsDtoResponse);
        when(ecoNewsTranslationRepo.findByEcoNewsAndLanguageCode(entity, AppConstant.DEFAULT_LANGUAGE_CODE))
                .thenReturn(new EcoNewsTranslation(null, null, "Title", "Text", null));
        when(newsSubscriberService.findAll()).thenReturn(Collections.emptyList());

        when(ecoNewsRepo.save(entity)).thenReturn(entity);
        Assert.assertEquals(addEcoNewsDtoResponse, ecoNewsService.save(addEcoNewsDtoRequest, "en"));
        addEcoNewsDtoResponse.setTitle("Title");
        verify(rabbitTemplate).convertAndSend(null, RabbitConstants.ADD_ECO_NEWS_ROUTING_KEY,
                new AddEcoNewsMessage(Collections.emptyList(), addEcoNewsDtoResponse));
        addEcoNewsDtoResponse.setTitle("test title");
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
        List<TagDto> tagDtos = new ArrayList<>();

        EcoNewsDto ecoNewsDto =
                new EcoNewsDto(zonedDateTime, "test image path", 1L, "test title", "test text", ecoNewsAuthorDto, tagDtos);
        EcoNewsTranslation ecoNewsTranslation =
                new EcoNewsTranslation(1L, null, "test title", "test text", null);

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
        List<TagDto> tagDtos = new ArrayList<>();
        EcoNewsTranslation ecoNewsTranslation =
                new EcoNewsTranslation(1L, null, "test title", "test text", null);
        List<EcoNewsTranslation> ecoNewsTranslations = Collections.singletonList(ecoNewsTranslation);
        PageRequest pageRequest = new PageRequest(0, 2);
        Page<EcoNewsTranslation> pageTr = new PageImpl<EcoNewsTranslation>(ecoNewsTranslations, pageRequest, ecoNewsTranslations.size());

        List<EcoNewsDto> dtoList = Collections.singletonList(
                new EcoNewsDto(now, "test image path", 1L, "test title", "test text", ecoNewsAuthorDto, tagDtos)
        );
        PageableDto<EcoNewsDto> pageableDto = new PageableDto<EcoNewsDto>(dtoList, dtoList.size(), 0);

        when(ecoNewsTranslationRepo.findAllByLanguageCode(pageRequest, "en")).thenReturn(pageTr);
        when(modelMapper.map(ecoNewsTranslation, EcoNewsDto.class))
                .thenReturn(dtoList.get(0));
        Assert.assertEquals(pageableDto, ecoNewsService.findAll(pageRequest, "en"));
    }

    @Test
    public void findById() {
        EcoNews entity =
                new EcoNews(1L, ZonedDateTime.now(), "test image path", author, Collections.emptyList(), new ArrayList<Tag>());
        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.of(entity));
        Assert.assertEquals(entity, ecoNewsService.findById(1L));
    }

    @Test
    public void delete() {
        doNothing().when(ecoNewsRepo).deleteById(1L);
        when(ecoNewsRepo.findById(anyLong()))
                .thenReturn(Optional.of(new EcoNews(1L, ZonedDateTime.now(),
                        "test image path", author, Collections.emptyList(), new ArrayList<Tag>())));
        ecoNewsService.delete(1L);
        verify(ecoNewsRepo, times(1)).deleteById(1L);
    }
}


