package greencity.service;

import greencity.ModelUtils;
import greencity.TestConst;
import greencity.constant.AppConstant;
import greencity.constant.RabbitConstants;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.PageableDto;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.econews.EcoNewsVO;
import greencity.dto.language.LanguageDTO;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.tag.TagVO;
import greencity.entity.EcoNews;
import greencity.enums.TagType;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.message.AddEcoNewsMessage;
import greencity.repository.EcoNewsRepo;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class EcoNewsServiceImplTest {
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

    @Test
    void save() throws MalformedURLException {
        MultipartFile image = ModelUtils.getFile();
        LanguageDTO dto = new LanguageDTO(1L, "en");

        when(modelMapper.map(addEcoNewsDtoRequest, EcoNews.class)).thenReturn(ecoNews);
        when(modelMapper.map(ecoNews, AddEcoNewsDtoResponse.class)).thenReturn(addEcoNewsDtoResponse);
        when(languageService.extractLanguageCodeFromRequest()).thenReturn(AppConstant.DEFAULT_LANGUAGE_CODE);
        when(newsSubscriberService.findAll()).thenReturn(Collections.emptyList());
        when(userService.findByEmail(TestConst.EMAIL)).thenReturn(ModelUtils.getUserVO());
        List<TagVO> tagVOList = Collections.singletonList(ModelUtils.getTagVO());
        when(tagService.findTagsByNamesAndType(anyList(), eq(TagType.ECO_NEWS))).thenReturn(tagVOList);
        when(languageService.findByCode(AppConstant.DEFAULT_LANGUAGE_CODE))
            .thenReturn(dto);
        when(ecoNewsRepo.save(ecoNews)).thenReturn(ecoNews);
        when(fileService.upload(image)).thenReturn(ModelUtils.getUrl());

        AddEcoNewsDtoResponse actual = ecoNewsService.save(addEcoNewsDtoRequest, image, TestConst.EMAIL);

        assertEquals(addEcoNewsDtoResponse, actual);

        addEcoNewsDtoResponse.setTitle("Title");

        verify(rabbitTemplate).convertAndSend(null, RabbitConstants.ADD_ECO_NEWS_ROUTING_KEY,
            new AddEcoNewsMessage(Collections.emptyList(), addEcoNewsDtoResponse));
    }

    @Test
    void saveWithExistedImage() throws IOException {
        MultipartFile image = ModelUtils.getFile();
        String imageToEncode = Base64.getEncoder().encodeToString(image.getBytes());
        addEcoNewsDtoRequest.setImage(imageToEncode);

        when(modelMapper.map(addEcoNewsDtoRequest, EcoNews.class)).thenReturn(ecoNews);
        when(userService.findByEmail(TestConst.EMAIL)).thenReturn(ModelUtils.getUserVO());
        when(fileService.upload(any(MultipartFile.class))).thenReturn(ModelUtils.getUrl());
        List<TagVO> tagVOList = Collections.singletonList(ModelUtils.getTagVO());
        when(tagService.findTagsByNamesAndType(anyList(), TagType.ECO_NEWS)).thenReturn(tagVOList);
        when(ecoNewsRepo.save(any(EcoNews.class))).thenReturn(ecoNews);
        when(modelMapper.map(ecoNews, AddEcoNewsDtoResponse.class)).thenReturn(addEcoNewsDtoResponse);

        AddEcoNewsDtoResponse actual = ecoNewsService.save(addEcoNewsDtoRequest, image, TestConst.EMAIL);

        assertEquals(addEcoNewsDtoResponse, actual);
    }

    @Test
    void saveFailedTest() {
        addEcoNewsDtoRequest.setTags(Arrays.asList("tags", "tags"));

        when(modelMapper.map(addEcoNewsDtoRequest, EcoNews.class)).thenReturn(ecoNews);
        when(userService.findByEmail(TestConst.EMAIL)).thenReturn(ModelUtils.getUserVO());

        assertThrows(NotSavedException.class, () -> ecoNewsService.save(addEcoNewsDtoRequest, null, TestConst.EMAIL));
    }

    @Test()
    void saveThrowsNotSavedException() throws MalformedURLException {
        MultipartFile image = ModelUtils.getFile();

        when(modelMapper.map(addEcoNewsDtoRequest, EcoNews.class)).thenReturn(ecoNews);
        when(ecoNewsRepo.save(ecoNews)).thenThrow(DataIntegrityViolationException.class);
        when(userService.findByEmail(TestConst.EMAIL)).thenReturn(ModelUtils.getUserVO());
        when(fileService.upload(image)).thenReturn(ModelUtils.getUrl());

        assertThrows(NotSavedException.class, () -> ecoNewsService.save(addEcoNewsDtoRequest, image, TestConst.EMAIL));
    }

    @Test
    void getThreeLastEcoNews() {
        ZonedDateTime zonedDateTime = ZonedDateTime.now();

        EcoNewsDto ecoNewsDto =
            new EcoNewsDto(zonedDateTime, "test image path", 1L, "test title", "test text", null,
                ModelUtils.getEcoNewsAuthorDto(), Collections.emptyList());
        EcoNews ecoNews = ModelUtils.getEcoNews();

        List<EcoNewsDto> dtoList = Collections.singletonList(ecoNewsDto);

        when(ecoNewsRepo.getThreeLastEcoNews()).thenReturn(Collections.singletonList(ecoNews));
        when(modelMapper.map(ecoNews, EcoNewsDto.class)).thenReturn(ecoNewsDto);

        List<EcoNewsDto> actual = ecoNewsService.getThreeLastEcoNews();

        assertEquals(dtoList, actual);
    }

    @Test
    void getThreeLastEcoNewsNotFound() {
        List<EcoNews> ecoNews = Collections.emptyList();

        when(ecoNewsRepo.getThreeLastEcoNews())
            .thenReturn(ecoNews);

        assertThrows(NotFoundException.class, () -> ecoNewsService.getThreeLastEcoNews());
    }

    @Test
    void findAll() {
        ZonedDateTime now = ZonedDateTime.now();

        List<EcoNews> ecoNews = Collections.singletonList(ModelUtils.getEcoNews());

        PageRequest pageRequest = PageRequest.of(0, 2);
        Page<EcoNews> translationPage = new PageImpl<>(ecoNews,
            pageRequest, ecoNews.size());

        List<EcoNewsDto> dtoList = Collections.singletonList(
            new EcoNewsDto(now, "test image path", 1L, "test title", "test text", null,
                ModelUtils.getEcoNewsAuthorDto(), Collections.emptyList()));
        PageableAdvancedDto<EcoNewsDto> pageableDto = new PageableAdvancedDto<>(dtoList, dtoList.size(), 0, 1,
            0, false, false, true, true);

        when(ecoNewsRepo.findAllByOrderByCreationDateDesc(pageRequest)).thenReturn(translationPage);
        when(modelMapper.map(ecoNews.get(0), EcoNewsDto.class)).thenReturn(dtoList.get(0));

        PageableAdvancedDto<EcoNewsDto> actual = ecoNewsService.findAll(pageRequest);

        assertEquals(pageableDto, actual);
    }

    @Test
    void find() {
        List<EcoNews> ecoNews = Collections.singletonList(ModelUtils.getEcoNews());
        PageRequest pageRequest = PageRequest.of(0, 2);
        Page<EcoNews> page = new PageImpl<>(ecoNews, pageRequest, ecoNews.size());
        List<EcoNewsDto> dtoList = Collections.singletonList(modelMapper.map(ecoNews, EcoNewsDto.class));
        PageableAdvancedDto<EcoNewsDto> pageableDto = new PageableAdvancedDto<>(dtoList, dtoList.size(), 0, 1,
            0, false, false, true, true);
        List<String> tags = Collections.singletonList(ModelUtils.getTagTranslations().get(0).getName());
        List<String> lowerCaseTags = tags.stream().map(String::toLowerCase).collect(Collectors.toList());

        when(modelMapper.map(ecoNews.get(0), EcoNewsDto.class)).thenReturn(dtoList.get(0));
        when(ecoNewsRepo.findByTags(pageRequest, lowerCaseTags))
            .thenReturn(page);

        PageableAdvancedDto<EcoNewsDto> actual =
            ecoNewsService.find(pageRequest, tags);

        assertEquals(pageableDto, actual);
    }

    @Test
    void findDtoById() {
        EcoNewsDto ecoNewsDto = modelMapper.map(ecoNews, EcoNewsDto.class);

        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.of(ecoNews));
        when(modelMapper.map(ecoNews, EcoNewsDto.class)).thenReturn(ecoNewsDto);

        EcoNewsDto actual = ecoNewsService.findDtoById(1L);

        assertEquals(ecoNewsDto, actual);
    }

    @Test
    void delete() {
        EcoNews ecoNews = ModelUtils.getEcoNews();
        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.of(ecoNews));
        EcoNewsVO ecoNewsVO = ModelUtils.getEcoNewsVO();
        when(modelMapper.map(ecoNews, EcoNewsVO.class)).thenReturn(ecoNewsVO);
        ecoNewsService.delete(1L, ecoNewsVO.getAuthor());

        verify(ecoNewsRepo, times(1)).deleteById(1L);
    }

    @Test
    void search() {
        SearchNewsDto searchNewsDto = new SearchNewsDto(1L, "title", null, null, Collections.singletonList("tag"));
        PageableDto<SearchNewsDto> pageableDto = new PageableDto<>(Collections.singletonList(searchNewsDto), 4, 1, 2);
        Page<EcoNews> page = new PageImpl<>(Collections.singletonList(ecoNews), PageRequest.of(1, 3), 1);

        when(ecoNewsRepo.searchEcoNews(PageRequest.of(0, 3), "test")).thenReturn(page);
        when(modelMapper.map(ecoNews, SearchNewsDto.class)).thenReturn(searchNewsDto);

        PageableDto<SearchNewsDto> actual = ecoNewsService.search("test");

        assertEquals(pageableDto, actual);
    }

    @Test
    void getThreeRecommendedEcoNews() {
        List<EcoNewsDto> dtoList = Collections.singletonList(modelMapper.map(ecoNews, EcoNewsDto.class));

        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.ofNullable(ecoNews));
        when(ecoNewsRepo.getThreeRecommendedEcoNews(1L)).thenReturn(Collections.singletonList(ecoNews));
        when(ecoNewsRepo.getThreeLastEcoNews()).thenReturn(Collections.singletonList(ecoNews));
        when(modelMapper.map(ecoNews, EcoNewsDto.class)).thenReturn(dtoList.get(0));

        List<EcoNewsDto> actual = ecoNewsService.getThreeRecommendedEcoNews(1L);

        assertEquals(dtoList, actual);
    }
}
