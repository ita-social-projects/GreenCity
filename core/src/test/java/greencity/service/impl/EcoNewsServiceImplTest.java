package greencity.service.impl;

import greencity.ModelUtils;
import greencity.TestConst;
import greencity.constant.AppConstant;
import greencity.constant.RabbitConstants;
import greencity.dto.PageableDto;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.search.SearchNewsDto;
import greencity.entity.EcoNews;
import greencity.entity.Tag;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.message.AddEcoNewsMessage;
import greencity.repository.EcoNewsRepo;
import greencity.service.*;
import java.net.MalformedURLException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(SpringExtension.class)
public class EcoNewsServiceImplTest {
    @Mock
    EcoNewsRepo ecoNewsRepo;

    @Mock
    ModelMapper modelMapper;

    @Mock
    RabbitTemplate rabbitTemplate;

    @Mock
    NewsSubscriberService newsSubscriberService;

    @Mock
    UserService userService;

    @Mock
    TagsService tagService;

    @Mock
    LanguageService languageService;

    @Mock
    FileService fileService;

    @InjectMocks
    private EcoNewsServiceImpl ecoNewsService;

    private AddEcoNewsDtoRequest addEcoNewsDtoRequest = ModelUtils.getAddEcoNewsDtoRequest();
    private EcoNews ecoNews = ModelUtils.getEcoNews();
    private AddEcoNewsDtoResponse addEcoNewsDtoResponse = ModelUtils.getAddEcoNewsDtoResponse();
    private Tag tag = ModelUtils.getTag();

    @Test
    public void save() throws MalformedURLException {
        MultipartFile image = ModelUtils.getFile();
        when(modelMapper.map(addEcoNewsDtoRequest, EcoNews.class)).thenReturn(ecoNews);
        when(modelMapper.map(ecoNews, AddEcoNewsDtoResponse.class)).thenReturn(addEcoNewsDtoResponse);
        when(languageService.extractLanguageCodeFromRequest()).thenReturn(AppConstant.DEFAULT_LANGUAGE_CODE);
        when(newsSubscriberService.findAll()).thenReturn(Collections.emptyList());
        when(userService.findByEmail(TestConst.EMAIL)).thenReturn(ModelUtils.getUser());
        when(tagService.findEcoNewsTagsByNames(anyList())).thenReturn(Collections.singletonList(tag));
        when(languageService.findByCode(AppConstant.DEFAULT_LANGUAGE_CODE))
            .thenReturn(ModelUtils.getLanguage());
        when(ecoNewsRepo.save(ecoNews)).thenReturn(ecoNews);
        when(fileService.upload(image)).thenReturn(ModelUtils.getUrl());

        assertEquals(addEcoNewsDtoResponse, ecoNewsService.save(addEcoNewsDtoRequest,
            image, TestConst.EMAIL));
        addEcoNewsDtoResponse.setTitle("Title");
        verify(rabbitTemplate).convertAndSend(null, RabbitConstants.ADD_ECO_NEWS_ROUTING_KEY,
            new AddEcoNewsMessage(Collections.emptyList(), addEcoNewsDtoResponse));
        addEcoNewsDtoResponse.setTitle("test title");
    }

    @Test()
    public void saveThrowsNotSavedException() throws MalformedURLException {
        MultipartFile image = ModelUtils.getFile();
        when(modelMapper.map(addEcoNewsDtoRequest, EcoNews.class)).thenReturn(ecoNews);
        when(ecoNewsRepo.save(ecoNews)).thenThrow(DataIntegrityViolationException.class);
        when(userService.findByEmail(TestConst.EMAIL)).thenReturn(ModelUtils.getUser());
        when(fileService.upload(image)).thenReturn(ModelUtils.getUrl());

        assertThrows(NotSavedException.class, () ->
            ecoNewsService.save(addEcoNewsDtoRequest, image, TestConst.EMAIL)
        );
    }

    @Test
    public void getThreeLastEcoNews() {
        ZonedDateTime zonedDateTime = ZonedDateTime.now();

        EcoNewsDto ecoNewsDto =
            new EcoNewsDto(zonedDateTime, "test image path", 1L, "test title", "test text", null,
                ModelUtils.getEcoNewsAuthorDto(), Collections.emptyList());
        EcoNews ecoNews = ModelUtils.getEcoNews();

        List<EcoNewsDto> dtoList = Collections.singletonList(ecoNewsDto);

        when(ecoNewsRepo.getThreeLastEcoNews()).thenReturn(Collections.singletonList(ecoNews));
        when(modelMapper.map(ecoNews, EcoNewsDto.class)).thenReturn(ecoNewsDto);
        assertEquals(dtoList, ecoNewsService.getThreeLastEcoNews());
    }

    @Test
    public void getThreeLastEcoNewsNotFound() {
        List<EcoNews> ecoNews = new ArrayList<>();
        List<EcoNewsDto> ecoNewsDtoList = new ArrayList<>();

        when(ecoNewsRepo.getThreeLastEcoNews())
            .thenReturn(ecoNews);

        assertThrows(NotFoundException.class, () ->
            assertEquals(ecoNewsDtoList, ecoNewsService.getThreeLastEcoNews())
        );
    }

    @Test
    public void findAll() {
        ZonedDateTime now = ZonedDateTime.now();

        List<EcoNews> ecoNews = Collections.singletonList(ModelUtils.getEcoNews());

        PageRequest pageRequest = PageRequest.of(0, 2);
        Page<EcoNews> translationPage = new PageImpl<>(ecoNews,
            pageRequest, ecoNews.size());

        List<EcoNewsDto> dtoList = Collections.singletonList(
            new EcoNewsDto(now, "test image path", 1L, "test title", "test text", null,
                ModelUtils.getEcoNewsAuthorDto(), Collections.emptyList())
        );
        PageableDto<EcoNewsDto> pageableDto = new PageableDto<>(dtoList, dtoList.size(), 0);

        when(ecoNewsRepo.findAllByOrderByCreationDateDesc(pageRequest)).thenReturn(translationPage);
        when(modelMapper.map(ecoNews.get(0), EcoNewsDto.class)).thenReturn(dtoList.get(0));
        assertEquals(pageableDto, ecoNewsService.findAll(pageRequest));
    }

    @Test
    public void find() {
        List<EcoNews> ecoNews = Collections.singletonList(ModelUtils.getEcoNews());
        PageRequest pageRequest = PageRequest.of(0, 2);
        Page<EcoNews> page = new PageImpl<>(ecoNews, pageRequest, ecoNews.size());
        List<EcoNewsDto> dtoList = Collections.singletonList(modelMapper.map(ecoNews, EcoNewsDto.class));
        PageableDto<EcoNewsDto> pageableDto = new PageableDto<>(dtoList, dtoList.size(), 0);
        when(modelMapper.map(ecoNews.get(0), EcoNewsDto.class)).thenReturn(dtoList.get(0));
        when(ecoNewsRepo.find(pageRequest, Collections.singletonList(ModelUtils.getTag().getName())))
            .thenReturn(page);
        assertEquals(pageableDto, ecoNewsService.find(pageRequest, Collections.singletonList(ModelUtils.getTag().getName())));
    }

    @Test
    public void findById() {
        EcoNews ecoNews = ModelUtils.getEcoNews();
        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.of(ecoNews));
        assertEquals(ecoNews, ecoNewsService.findById(1L));
    }

    @Test
    public void findDtoById() {
        EcoNewsDto ecoNewsDto = modelMapper.map(ecoNews, EcoNewsDto.class);
        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.of(ecoNews));
        when(modelMapper.map(ecoNews, EcoNewsDto.class)).thenReturn(ecoNewsDto);
        assertEquals(ecoNewsDto, ecoNewsService.findDtoById(1L));
    }

    @Test
    public void delete() {
        doNothing().when(ecoNewsRepo).deleteById(1L);
        when(ecoNewsRepo.findById(anyLong()))
            .thenReturn(Optional.of(ModelUtils.getEcoNews()));
        ecoNewsService.delete(1L);
        verify(ecoNewsRepo, times(1)).deleteById(1L);
    }

    @Test
    void search() {
        SearchNewsDto searchNewsDto = new SearchNewsDto(1L, "title", null, null, Collections.singletonList("tag"));
        PageableDto<SearchNewsDto> pageableDto = new PageableDto<>(Collections.singletonList(searchNewsDto), 4, 1);
        Page<EcoNews> page = new PageImpl<>(Collections.singletonList(ecoNews), PageRequest.of(1, 3), 1);

        when(ecoNewsRepo.searchEcoNews(PageRequest.of(0, 3), "test"))
            .thenReturn(page);
        when(modelMapper.map(ecoNews, SearchNewsDto.class))
            .thenReturn(searchNewsDto);

        assertEquals(pageableDto, ecoNewsService.search("test"));
    }

    @Test
    void getThreeRecommendedEcoNews() {
        List<EcoNewsDto> dtoList = Collections.singletonList(modelMapper.map(ecoNews, EcoNewsDto.class));
        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.ofNullable(ecoNews));
        when(ecoNewsRepo.getThreeRecommendedEcoNews(ecoNews.getTags().size(), 1L, 0L, 0L, 1L))
            .thenReturn(Collections.singletonList(ecoNews));
        when(ecoNewsRepo.getThreeLastEcoNews()).thenReturn(Collections.singletonList(ecoNews));
        when(modelMapper.map(ecoNews, EcoNewsDto.class)).thenReturn(dtoList.get(0));
        assertEquals(dtoList, ecoNewsService.getThreeRecommendedEcoNews(1L));

    }
}


