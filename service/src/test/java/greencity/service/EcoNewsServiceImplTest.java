package greencity.service;

import greencity.ModelUtils;
import greencity.TestConst;
import greencity.achievement.AchievementCalculation;
import greencity.client.RestClient;
import greencity.constant.AppConstant;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.PageableDto;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.econews.EcoNewsDtoManagement;
import greencity.dto.econews.EcoNewsGenericDto;
import greencity.dto.econews.EcoNewsVO;
import greencity.dto.econews.EcoNewsViewDto;
import greencity.dto.econews.UpdateEcoNewsDto;
import greencity.dto.econewscomment.EcoNewsCommentVO;
import greencity.dto.language.LanguageDTO;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.tag.TagVO;
import greencity.dto.user.UserVO;
import greencity.entity.EcoNews;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.enums.NotificationType;
import greencity.enums.RatingCalculationEnum;
import greencity.enums.TagType;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.exception.exceptions.UnsupportedSortException;
import greencity.filters.EcoNewsSpecification;
import greencity.filters.SearchCriteria;
import greencity.rating.RatingCalculation;
import greencity.repository.AchievementCategoryRepo;
import greencity.repository.AchievementRepo;
import greencity.repository.EcoNewsRepo;
import greencity.repository.EcoNewsSearchRepo;
import greencity.repository.UserAchievementRepo;
import greencity.repository.UserRepo;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class EcoNewsServiceImplTest {
    @Mock
    EcoNewsRepo ecoNewsRepo;
    @Mock
    ModelMapper modelMapper;
    @Mock
    NewsSubscriberService newsSubscriberService;
    @Mock
    RestClient restClient;
    @Mock
    TagsService tagService;
    @Mock
    LanguageService languageService;
    @Mock
    FileService fileService;
    @Mock
    HttpServletRequest httpServletRequest;
    @Mock
    EcoNewsSearchRepo ecoNewsSearchRepo;
    @Mock
    private UserService userService;
    @Mock
    private RatingCalculation ratingCalculation;
    @Mock
    private AchievementService achievementService;
    @Mock
    private AchievementCalculation achievementCalculation;
    @InjectMocks
    private EcoNewsServiceImpl ecoNewsService;
    @Mock
    private UserActionService userActionService;
    @Mock
    private AchievementCategoryService achievementCategoryService;
    @Mock
    private UserAchievementRepo userAchievementRepo;
    @Mock
    private AchievementRepo achievementRepo;
    @Mock
    private UserRepo userRepo;
    @Mock
    private AchievementCategoryRepo achievementCategoryRepo;
    @Mock
    private RatingCalculationEnum ratingCalculationEnum;

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserNotificationService userNotificationService;

    private final AddEcoNewsDtoRequest addEcoNewsDtoRequest = ModelUtils.getAddEcoNewsDtoRequest();
    private final EcoNews ecoNews = ModelUtils.getEcoNews();
    private final AddEcoNewsDtoResponse addEcoNewsDtoResponse = ModelUtils.getAddEcoNewsDtoResponse();
    private final EcoNewsGenericDto ecoNewsGenericDto = ModelUtils.getEcoNewsGenericDto();

    private static final String ECO_NEWS_TITLE = "title";
    private static final String ECO_NEWS_JOIN_TAG = "tags";
    private static final String ECO_NEWS_TAG_TRANSLATION = "tagTranslations";
    private static final String ECO_NEWS_TAG_TRANSLATION_NAME = "name";

    @Test
    void save() throws MalformedURLException {
        MultipartFile image = ModelUtils.getFile();
        LanguageDTO dto = new LanguageDTO(1L, "en");
        List<Tag> tags = ModelUtils.getTags();

        when(modelMapper.map(addEcoNewsDtoRequest, EcoNews.class)).thenReturn(ecoNews);
        when(modelMapper.map(ecoNews, AddEcoNewsDtoResponse.class)).thenReturn(addEcoNewsDtoResponse);
        when(languageService.extractLanguageCodeFromRequest()).thenReturn(AppConstant.DEFAULT_LANGUAGE_CODE);
        when(newsSubscriberService.findAll()).thenReturn(Collections.emptyList());
        when(restClient.findByEmail(TestConst.EMAIL)).thenReturn(ModelUtils.getUserVO());
        List<TagVO> tagVOList = Collections.singletonList(ModelUtils.getTagVO());
        when(tagService.findTagsByNamesAndType(anyList(), eq(TagType.ECO_NEWS))).thenReturn(tagVOList);
        when(modelMapper.map(tagVOList, new TypeToken<List<Tag>>() {
        }.getType())).thenReturn(tags);
        ecoNews.setTags(tags);
        when(languageService.findByCode(AppConstant.DEFAULT_LANGUAGE_CODE))
            .thenReturn(dto);
        when(ecoNewsRepo.save(ecoNews)).thenReturn(ecoNews);
        when(fileService.upload(image)).thenReturn(ModelUtils.getUrl().toString());

        assertNotEquals(null, addEcoNewsDtoResponse);
    }

    @Test
    void saveWithExistedImageTest() throws IOException {
        var image = ModelUtils.getFile();
        var imageToEncode = Base64.getEncoder().encodeToString(image.getBytes());
        var tagVOList = Collections.singletonList(ModelUtils.getTagVO());

        addEcoNewsDtoRequest.setImage(imageToEncode);

        when(modelMapper.map(addEcoNewsDtoRequest, EcoNews.class)).thenReturn(ecoNews);
        when(restClient.findByEmail(TestConst.EMAIL)).thenReturn(ModelUtils.getUserVO());

        when(tagService.findTagsByNamesAndType(anyList(), eq(TagType.ECO_NEWS))).thenReturn(tagVOList);
        doNothing().when(userNotificationService).createNewNotification(ModelUtils.getUserVO(),
            NotificationType.EVENT_CREATED, ecoNews.getId(), ecoNews.getTitle());
        when(ecoNewsRepo.save(any(EcoNews.class))).thenReturn(ecoNews);
        addEcoNewsDtoResponse.setEcoNewsAuthorDto(ModelUtils.getEcoNewsAuthorDto());
        when(modelMapper.map(ecoNews, AddEcoNewsDtoResponse.class)).thenReturn(addEcoNewsDtoResponse);
        when(modelMapper.map(ModelUtils.getUserVO(), User.class)).thenReturn(ModelUtils.getUser());
        when(userService.findById(anyLong())).thenReturn(ModelUtils.getUserVO());

        AddEcoNewsDtoResponse actual = ecoNewsService.save(addEcoNewsDtoRequest, image, TestConst.EMAIL);

        assertEquals(addEcoNewsDtoResponse, actual);

        verify(modelMapper).map(addEcoNewsDtoRequest, EcoNews.class);
        verify(restClient).findByEmail(TestConst.EMAIL);
        verify(tagService).findTagsByNamesAndType(anyList(), eq(TagType.ECO_NEWS));
        verify(ecoNewsRepo).save(any(EcoNews.class));
        verify(modelMapper).map(ecoNews, AddEcoNewsDtoResponse.class);
        verify(modelMapper).map(ModelUtils.getUserVO(), User.class);
        verify(userService).findById(anyLong());
    }

    @Test
    void saveFailedTest() {
        addEcoNewsDtoRequest.setTags(Arrays.asList("tags", "tags"));

        when(modelMapper.map(addEcoNewsDtoRequest, EcoNews.class)).thenReturn(ecoNews);
        when(restClient.findByEmail(TestConst.EMAIL)).thenReturn(ModelUtils.getUserVO());

        assertThrows(NotSavedException.class, () -> ecoNewsService.save(addEcoNewsDtoRequest, null, TestConst.EMAIL));
    }

    @Test()
    void saveThrowsNotSavedException() throws MalformedURLException {
        MultipartFile image = ModelUtils.getFile();

        when(modelMapper.map(addEcoNewsDtoRequest, EcoNews.class)).thenReturn(ecoNews);
        when(ecoNewsRepo.save(ecoNews)).thenThrow(DataIntegrityViolationException.class);
        when(restClient.findByEmail(TestConst.EMAIL)).thenReturn(ModelUtils.getUserVO());
        when(fileService.upload(image)).thenReturn(ModelUtils.getUrl().toString());

        assertThrows(NotSavedException.class, () -> ecoNewsService.save(addEcoNewsDtoRequest, image, TestConst.EMAIL));
    }

    @Test
    @SneakyThrows
    void saveEcoNews() {
        MultipartFile image = ModelUtils.getFile();
        String imageToEncode = Base64.getEncoder().encodeToString(image.getBytes());
        addEcoNewsDtoRequest.setImage(imageToEncode);

        when(modelMapper.map(addEcoNewsDtoRequest, EcoNews.class)).thenReturn(ecoNews);
        when(restClient.findByEmail(TestConst.EMAIL)).thenReturn(ModelUtils.getUserVO());
        when(modelMapper.map(ModelUtils.getUserVO(), User.class)).thenReturn(ModelUtils.getUser());
        when(fileService.upload(any(MultipartFile.class))).thenReturn(ModelUtils.getUrl().toString());
        List<TagVO> tagVOList = Collections.singletonList(ModelUtils.getTagVO());
        List<Tag> tags = ModelUtils.getTags();
        when(tagService.findTagsByNamesAndType(anyList(), eq(TagType.ECO_NEWS))).thenReturn(tagVOList);
        when(ecoNewsRepo.save(any(EcoNews.class))).thenReturn(ecoNews);
        when(modelMapper.map(ecoNews, EcoNewsGenericDto.class)).thenReturn(ecoNewsGenericDto);

        when(modelMapper.map(tagVOList,
            new TypeToken<List<Tag>>() {
            }.getType())).thenReturn(tags);
        when(userService.findByEmail(anyString())).thenReturn(ModelUtils.getUserVO());
        EcoNewsGenericDto actual = ecoNewsService.saveEcoNews(addEcoNewsDtoRequest, image, TestConst.EMAIL);

        assertEquals(ecoNewsGenericDto, actual);
    }

    @Test
    void getThreeLastEcoNews() {
        ZonedDateTime zonedDateTime = ZonedDateTime.now();

        EcoNewsDto ecoNewsDto = EcoNewsDto.builder()
            .creationDate(zonedDateTime)
            .imagePath("test image path")
            .id(1L)
            .title("test title")
            .content("content")
            .shortInfo(null)
            .author(ModelUtils.getEcoNewsAuthorDto())
            .tags(Collections.emptyList())
            .tagsUa(Collections.emptyList())
            .likes(1)
            .countComments(0)
            .build();

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
            EcoNewsDto.builder()
                .creationDate(now)
                .imagePath("test image path")
                .id(1L)
                .title("test title")
                .content("content")
                .shortInfo(null)
                .author(ModelUtils.getEcoNewsAuthorDto())
                .tags(Collections.emptyList())
                .tagsUa(Collections.emptyList())
                .likes(1)
                .countComments(0)
                .build());
        PageableAdvancedDto<EcoNewsDto> pageableDto = new PageableAdvancedDto<>(dtoList, dtoList.size(), 0, 1,
            0, false, false, true, true);

        when(ecoNewsRepo.findAllByOrderByCreationDateDesc(pageRequest)).thenReturn(translationPage);
        when(modelMapper.map(ecoNews.getFirst(), EcoNewsDto.class)).thenReturn(dtoList.getFirst());

        PageableAdvancedDto<EcoNewsDto> actual = ecoNewsService.findAll(pageRequest);

        assertEquals(pageableDto, actual);
    }

    @Test
    void findAllByUserPageIsSort() {
        List<EcoNews> ecoNews = Collections.singletonList(ModelUtils.getEcoNews());
        PageRequest pageRequest = PageRequest.of(0, 2);
        Page<EcoNews> translationPage = new PageImpl<>(ecoNews,
            pageRequest, ecoNews.size());

        List<EcoNewsGenericDto> dtoList = Collections.singletonList(
            ModelUtils.getEcoNewsGenericDto());
        PageableAdvancedDto<EcoNewsGenericDto> pageableDto = new PageableAdvancedDto<>(dtoList, dtoList.size(), 0, 1,
            0, false, false, true, true);

        UserVO userVO = UserVO.builder().id(1L).build();
        User user = User.builder().id(1L).build();
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(ecoNewsRepo.findAllByAuthorOrderByCreationDateDesc(user, pageRequest)).thenReturn(translationPage);
        when(modelMapper.map(ecoNews.getFirst(), EcoNewsGenericDto.class)).thenReturn(dtoList.getFirst());

        PageableAdvancedDto<EcoNewsGenericDto> actual = ecoNewsService.findAllByUser(userVO, pageRequest);

        assertEquals(pageableDto, actual);
    }

    @Test
    void findAllByUserPageInvalidSorted() {
        PageRequest pageRequest = PageRequest.of(0, 1, Sort.by("id"));

        UserVO userVO = UserVO.builder().id(1L).build();

        assertThrows(UnsupportedSortException.class, () -> {
            ecoNewsService.findAllByUser(userVO, pageRequest);
        });
    }

    @Test
    void find() {
        List<EcoNews> ecoNews = Collections.singletonList(ModelUtils.getEcoNews());
        PageRequest pageRequest = PageRequest.of(0, 2);
        Page<EcoNews> page = new PageImpl<>(ecoNews, pageRequest, ecoNews.size());
        List<EcoNewsGenericDto> dtoList = Collections.singletonList(ModelUtils.getEcoNewsGenericDto());
        PageableAdvancedDto<EcoNewsGenericDto> pageableDto = new PageableAdvancedDto<>(dtoList, dtoList.size(), 0, 1,
            0, false, false, true, true);
        List<String> tags = Collections.singletonList(ModelUtils.getTagTranslations().get(0).getName());
        List<String> lowerCaseTags = tags.stream().map(String::toLowerCase).collect(Collectors.toList());

        when(modelMapper.map(ecoNews.getFirst(), EcoNewsGenericDto.class)).thenReturn(dtoList.getFirst());
        when(ecoNewsRepo.findByTags(pageRequest, lowerCaseTags))
            .thenReturn(page);

        PageableAdvancedDto<EcoNewsGenericDto> actual =
            ecoNewsService.find(pageRequest, tags);

        assertEquals(pageableDto, actual);
    }

    @Test
    void findDtoById() {
        EcoNewsDto ecoNewsDto = modelMapper.map(ecoNews, EcoNewsDto.class);

        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.of(ecoNews));
        when(modelMapper.map(ecoNews, EcoNewsDto.class)).thenReturn(ecoNewsDto);

        EcoNewsDto actual = ecoNewsService.getById(1L);

        assertEquals(ecoNewsDto, actual);
    }

    @Test
    void delete() {
        String accessToken = "Token";
        EcoNews ecoNews = ModelUtils.getEcoNews();
        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.of(ecoNews));
        EcoNewsVO ecoNewsVO = ModelUtils.getEcoNewsVO();
        when(httpServletRequest.getHeader("Authorization")).thenReturn(accessToken);
        when(modelMapper.map(ecoNews, EcoNewsVO.class)).thenReturn(ecoNewsVO);
        ecoNewsService.delete(1L, ecoNewsVO.getAuthor());

        verify(ecoNewsRepo, times(1)).deleteById(1L);
    }

    @Test
    void search() {
        SearchNewsDto searchNewsDto = new SearchNewsDto(1L, "title", null, null, Collections.singletonList("tag"));
        PageableDto<SearchNewsDto> pageableDto = new PageableDto<>(Collections.singletonList(searchNewsDto), 4, 1, 2);
        Page<EcoNews> page = new PageImpl<>(Collections.singletonList(ecoNews), PageRequest.of(1, 3), 1);

        when(ecoNewsSearchRepo.find(PageRequest.of(0, 3), "test", "en")).thenReturn(page);
        when(modelMapper.map(ecoNews, SearchNewsDto.class)).thenReturn(searchNewsDto);

        PageableDto<SearchNewsDto> actual = ecoNewsService.search("test", "en");

        assertEquals(pageableDto, actual);
    }

    @Test
    void getThreeRecommendedEcoNews() {
        List<EcoNewsDto> dtoList = Collections.singletonList(modelMapper.map(ecoNews, EcoNewsDto.class));

        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.ofNullable(ecoNews));
        when(ecoNewsRepo.getThreeRecommendedEcoNews(1L)).thenReturn(Collections.singletonList(ecoNews));
        when(ecoNewsRepo.getThreeLastEcoNews()).thenReturn(Collections.singletonList(ecoNews));
        when(modelMapper.map(ecoNews, EcoNewsDto.class)).thenReturn(dtoList.getFirst());

        List<EcoNewsDto> actual = ecoNewsService.getThreeRecommendedEcoNews(1L);

        assertEquals(dtoList, actual);
    }

    @Test
    void deleteThrowExceptionTest() {
        EcoNews ecoNews = ModelUtils.getEcoNews();
        EcoNewsVO ecoNewsVO = ModelUtils.getEcoNewsVO();
        UserVO author = ModelUtils.getUserVO();
        author.setId(2L);
        UserVO userVO = ModelUtils.getUserVO();
        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.of(ecoNews));
        when(modelMapper.map(ecoNews, EcoNewsVO.class)).thenReturn(ecoNewsVO);
        ecoNewsVO.setAuthor(author);
        assertThrows(BadRequestException.class, () -> ecoNewsService.delete(1L, userVO));
    }

    @Test
    void deleteAllTest() {
        List<Long> listId = Collections.singletonList(1L);
        doNothing().when(ecoNewsRepo).deleteEcoNewsWithIds(listId);
        ecoNewsService.deleteAll(listId);
        verify(ecoNewsRepo, times(1)).deleteEcoNewsWithIds(listId);
    }

    @Test
    void searchTest() {
        Pageable pageable = PageRequest.of(0, 2);
        List<EcoNews> ecoNews = Collections.singletonList(ModelUtils.getEcoNews());
        SearchNewsDto searchNewsDto = ModelUtils.getSearchNewsDto();
        Page<EcoNews> page = new PageImpl<>(ecoNews, pageable, 2);
        List<SearchNewsDto> searchNewsDtos = Collections.singletonList(searchNewsDto);
        PageableDto<SearchNewsDto> actual = new PageableDto<>(searchNewsDtos, page.getTotalElements(),
            page.getPageable().getPageNumber(), page.getTotalPages());
        when(ecoNewsSearchRepo.find(pageable, "query", "en")).thenReturn(page);
        when(modelMapper.map(ecoNews, SearchNewsDto.class)).thenReturn(searchNewsDto);
        PageableDto<SearchNewsDto> expected = ecoNewsService.search(pageable, "query", "en");
        assertEquals(expected.getTotalPages(), actual.getTotalPages());
    }

    @Test
    void getAllPublishedNewsByUserId() {
        List<EcoNews> ecoNews = Collections.singletonList(ModelUtils.getEcoNews());
        List<EcoNewsDto> dtoList = Collections.singletonList(modelMapper.map(ecoNews, EcoNewsDto.class));

        when(modelMapper.map(ecoNews.getFirst(), EcoNewsDto.class)).thenReturn(dtoList.getFirst());
        when(ecoNewsRepo.findAllByUserId(1L)).thenReturn(ecoNews);

        List<EcoNewsDto> actual = ecoNewsService.getAllPublishedNewsByUserId(1L);

        assertEquals(dtoList, actual);
    }

    @Test
    void getAmountOfPublishedNewsByUserIdTest() {
        when(ecoNewsRepo.getAmountOfPublishedNewsByUserId(1L)).thenReturn(10L);
        Long actual = ecoNewsService.getAmountOfPublishedNewsByUserId(1L);
        assertEquals(10L, actual);
    }

    @Test
    void getAllPublishedNewsByUserTest() {
        UserVO userVO = ModelUtils.getUserVO();
        List<EcoNews> ecoNews = Collections.singletonList(ModelUtils.getEcoNews());
        List<EcoNewsDto> dtoList = Collections.singletonList(modelMapper.map(ecoNews, EcoNewsDto.class));

        when(ecoNewsRepo.findAllByUserId(userVO.getId())).thenReturn(ecoNews);

        List<EcoNewsDto> actual = ecoNewsService.getAllPublishedNewsByUser(userVO);
        assertEquals(dtoList, actual);
    }

    @Test
    void likeCommentTest() {
        UserVO userVO = ModelUtils.getUserVO();
        EcoNewsCommentVO ecoNewsCommentVO = ModelUtils.getEcoNewsCommentVO();
        ecoNewsService.likeComment(userVO, ecoNewsCommentVO);
        assertEquals(1, ecoNewsCommentVO.getUsersLiked().size());
    }

    @Test
    void unlikeCommentTest() {
        UserVO userVO = ModelUtils.getUserVO();
        EcoNewsCommentVO ecoNewsCommentVO = ModelUtils.getEcoNewsCommentVO();
        ecoNewsService.unlikeComment(userVO, ecoNewsCommentVO);
        assertEquals(0, ecoNewsCommentVO.getUsersLiked().size());
    }

    @Test
    void searchEcoNewsBy() {
        Pageable pageable = PageRequest.of(0, 2);
        List<EcoNews> ecoNews = Collections.singletonList(ModelUtils.getEcoNews());
        Page<EcoNews> page = new PageImpl<>(ecoNews, pageable, 2);
        EcoNews ecoNews1 = ModelUtils.getEcoNews();
        EcoNewsDto ecoNewsDto = ModelUtils.getEcoNewsDto();
        when(ecoNewsRepo.searchEcoNewsBy(pageable, "query")).thenReturn(page);
        when(modelMapper.map(ecoNews1, EcoNewsDto.class)).thenReturn(ecoNewsDto);
        PageableAdvancedDto<EcoNewsDto> actual =
            new PageableAdvancedDto<>(Collections.singletonList(ecoNewsDto),
                2, 1, 2, 1, true, true, true, false);
        PageableAdvancedDto<EcoNewsDto> expected = ecoNewsService.searchEcoNewsBy(pageable, "query");
        assertEquals(expected.getTotalElements(), actual.getTotalElements());
    }

    @Test
    void updateVoidTest() {
        EcoNews ecoNews = ModelUtils.getEcoNews();
        EcoNewsDtoManagement ecoNewsDtoManagement = ModelUtils.getEcoNewsDtoManagement();
        EcoNewsVO ecoNewsVO = ModelUtils.getEcoNewsVO();
        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.of(ecoNews));
        when(ecoNewsService.findById(1L)).thenReturn(ecoNewsVO);
        when(modelMapper.map(ecoNewsVO, EcoNews.class)).thenReturn(ecoNews);
        when(modelMapper.map(ecoNews, EcoNewsVO.class)).thenReturn(ecoNewsVO);
        when(ecoNewsRepo.save(ecoNews)).thenReturn(ecoNews);
        ecoNewsService.update(ecoNewsDtoManagement, any(MultipartFile.class));
        assertEquals(ecoNewsDtoManagement.getTitle(), ecoNews.getTitle());
    }

    @Test
    void updateEcoNewsDtoTest() {
        EcoNews ecoNews = ModelUtils.getEcoNews();
        EcoNewsVO ecoNewsVO = ModelUtils.getEcoNewsVO();
        EcoNewsGenericDto ecoNewsDto = ModelUtils.getEcoNewsGenericDto();
        UpdateEcoNewsDto updateEcoNewsDto = ModelUtils.getUpdateEcoNewsDto();
        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.of(ecoNews));
        when(ecoNewsService.findById(1L)).thenReturn(ecoNewsVO);
        when(modelMapper.map(ecoNewsVO, EcoNews.class)).thenReturn(ecoNews);
        when(ecoNewsRepo.save(ecoNews)).thenReturn(ecoNews);
        when(modelMapper.map(ecoNews, EcoNewsGenericDto.class)).thenReturn(ecoNewsDto);
        List<TagVO> tags = ModelUtils.getEcoNewsVO().getTags();
        when(tagService.findTagsByNamesAndType(updateEcoNewsDto.getTags(), TagType.ECO_NEWS)).thenReturn(tags);
        when(modelMapper.map(tagService
            .findTagsByNamesAndType(updateEcoNewsDto.getTags(), TagType.ECO_NEWS),
            new TypeToken<List<Tag>>() {
            }.getType())).thenReturn(ecoNews.getTags());

        EcoNewsGenericDto actual =
            ecoNewsService.update(updateEcoNewsDto, any(MultipartFile.class), ModelUtils.getUserVO());
        assertEquals(ecoNewsDto, actual);
    }

    @Test
    void updateEcoNewsDtoThrowsExceptionTest() {
        EcoNews ecoNews = ModelUtils.getEcoNews();
        UserVO user = ModelUtils.getUserVO();
        ecoNews.getAuthor().setId(2L);
        EcoNewsVO ecoNewsVO = ModelUtils.getEcoNewsVO();
        EcoNewsDto ecoNewsDto = ModelUtils.getEcoNewsDto();
        UpdateEcoNewsDto updateEcoNewsDto = ModelUtils.getUpdateEcoNewsDto();
        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.of(ecoNews));
        when(ecoNewsService.findById(1L)).thenReturn(ecoNewsVO);
        when(modelMapper.map(ecoNewsVO, EcoNews.class)).thenReturn(ecoNews);
        assertThrows(BadRequestException.class, () -> ecoNewsService.update(updateEcoNewsDto, null, user));

    }

    @Test
    void getFilteredDataForManagementByPageTest() {
        Pageable pageable = PageRequest.of(0, 2);
        List<EcoNews> ecoNews = Collections.singletonList(ModelUtils.getEcoNews());
        Page<EcoNews> page = new PageImpl<>(ecoNews, pageable, ecoNews.size());
        EcoNewsViewDto ecoNewsViewDto = ModelUtils.getEcoNewsViewDto();
        EcoNewsDto ecoNewsDto = ModelUtils.getEcoNewsDto();
        EcoNewsSpecification ecoNewsSpecification = ecoNewsService.getSpecification(ecoNewsViewDto);
        when(ecoNewsRepo.findAll(any(EcoNewsSpecification.class), any(Pageable.class))).thenReturn(page);
        when(modelMapper.map(ecoNews, EcoNewsDto.class)).thenReturn(ecoNewsDto);
        PageableAdvancedDto<EcoNewsDto> actual =
            ecoNewsService.getFilteredDataForManagementByPage(pageable, ecoNewsViewDto);
        PageableAdvancedDto<EcoNewsDto> expected =
            new PageableAdvancedDto<>(Collections.singletonList(ecoNewsDto), 1, 1, 1,
                1, false, false, false, false);
        assertEquals(expected.getTotalElements(), actual.getTotalElements());
    }

    @Test
    void getSpecificationTest() {
        EcoNewsViewDto ecoNewsViewDto = ModelUtils.getEcoNewsViewDto();
        List<SearchCriteria> searchCriteriaList = ecoNewsService.buildSearchCriteria(ecoNewsViewDto);
        EcoNewsSpecification expected = new EcoNewsSpecification(searchCriteriaList);
        EcoNewsSpecification actual = ecoNewsService.getSpecification(ecoNewsViewDto);
        assertNotEquals(expected, actual);
    }

    @Test
    void buildSearchCriteriaTest() {
        EcoNewsViewDto ecoNewsViewDto = ModelUtils.getEcoNewsViewDto();
        List<SearchCriteria> actual = ecoNewsService.buildSearchCriteria(ecoNewsViewDto);
        assertEquals(6, actual.size());
    }

    @Test
    void likeTest() {
        UserVO userVO = ModelUtils.getUserVO();
        EcoNews ecoNews = ModelUtils.getEcoNews();
        EcoNewsVO ecoNewsVO = ModelUtils.getEcoNewsVO();
        ecoNewsVO.getAuthor().setId(2L);
        ecoNewsVO.setUsersLikedNews(new HashSet<>());

        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.of(ecoNews));
        when(modelMapper.map(ecoNews, EcoNewsVO.class)).thenReturn(ecoNewsVO);
        when(modelMapper.map(ecoNewsVO, EcoNews.class)).thenReturn(ecoNews);

        ecoNewsService.like(userVO, 1L);

        assertEquals(1, ecoNewsVO.getUsersLikedNews().size());
        assertTrue(ecoNewsVO.getUsersLikedNews().contains(userVO));
        verify(ecoNewsRepo).save(ecoNews);
    }

    @Test
    void likeOwnTest() {
        UserVO userVO = ModelUtils.getUserVO();
        EcoNews ecoNews = ModelUtils.getEcoNews();
        EcoNewsVO ecoNewsVO = ModelUtils.getEcoNewsVO();
        ecoNewsVO.setUsersLikedNews(new HashSet<>());

        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.of(ecoNews));
        when(modelMapper.map(ecoNews, EcoNewsVO.class)).thenReturn(ecoNewsVO);
        when(modelMapper.map(ecoNewsVO, EcoNews.class)).thenReturn(ecoNews);

        assertThrows(BadRequestException.class, () -> ecoNewsService.like(userVO, 1L));
    }

    @Test
    void givenEcoNewsLikedByUser_whenLikedByUser_shouldRemoveLike() {
        // given
        UserVO userVO = ModelUtils.getUserVO();
        EcoNews ecoNews = ModelUtils.getEcoNews();
        EcoNewsVO ecoNewsVO = ModelUtils.getEcoNewsVO();
        ecoNewsVO.getAuthor().setId(2L);
        ecoNewsVO.setUsersLikedNews(new HashSet<>());
        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.of(ecoNews));
        when(modelMapper.map(ecoNews, EcoNewsVO.class)).thenReturn(ecoNewsVO);
        when(modelMapper.map(ecoNewsVO, EcoNews.class)).thenReturn(ecoNews);

        // when
        ecoNewsService.like(userVO, 1L);
        ecoNewsService.like(userVO, 1L);

        // then
        assertEquals(0, ecoNewsVO.getUsersLikedNews().size());
    }

    @Test
    void dislikeTest() {
        // given
        UserVO userVO = ModelUtils.getUserVO();
        EcoNews ecoNews = ModelUtils.getEcoNews();
        EcoNewsVO ecoNewsVO = ModelUtils.getEcoNewsVO();
        ecoNewsVO.setUsersDislikedNews(new HashSet<>());
        when(ecoNewsRepo.findById(anyLong())).thenReturn(Optional.of(ecoNews));
        when(modelMapper.map(ecoNews, EcoNewsVO.class)).thenReturn(ecoNewsVO);
        when(modelMapper.map(ecoNewsVO, EcoNews.class)).thenReturn(ecoNews);

        // when
        ecoNewsService.dislike(userVO, 1L);
        // then
        assertTrue(ecoNewsVO.getUsersDislikedNews().contains(userVO));
        assertEquals(1, ecoNewsVO.getUsersDislikedNews().size());
        verify(ecoNewsRepo).save(ecoNews);
    }

    @Test
    void givenEcoNewsLikedByUser_whenDislikedByUser_shouldRemoveLikeAndAddDislike() {
        // given
        UserVO userVO = ModelUtils.getUserVO();
        EcoNews ecoNews = ModelUtils.getEcoNews();
        EcoNewsVO ecoNewsVO = ModelUtils.getEcoNewsVO();
        ecoNewsVO.setUsersLikedNews(new HashSet<>(Set.of(userVO)));
        ecoNewsVO.setUsersDislikedNews(new HashSet<>());
        when(ecoNewsRepo.findById(anyLong())).thenReturn(Optional.of(ecoNews));
        when(modelMapper.map(ecoNews, EcoNewsVO.class)).thenReturn(ecoNewsVO);
        when(modelMapper.map(ecoNewsVO, EcoNews.class)).thenReturn(ecoNews);
        // when
        ecoNewsService.dislike(userVO, 1L);
        // then
        assertTrue(ecoNewsVO.getUsersDislikedNews().contains(userVO));
        assertTrue(ecoNewsVO.getUsersLikedNews().isEmpty());
        assertEquals(1, ecoNewsVO.getUsersDislikedNews().size());
        assertEquals(0, ecoNewsVO.getUsersLikedNews().size());
    }

    @Test
    void countLikesForEcoNews() {
        EcoNews ecoNews = ModelUtils.getEcoNews();
        EcoNewsVO ecoNewsVO = ModelUtils.getEcoNewsVO();
        Set<UserVO> usersLiked = new HashSet<>();
        usersLiked.add(UserVO.builder().id(1L).build());
        usersLiked.add(UserVO.builder().id(2L).build());

        ecoNewsVO.setUsersLikedNews(usersLiked);
        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.of(ecoNews));
        when(modelMapper.map(ecoNews, EcoNewsVO.class)).thenReturn(ecoNewsVO);

        int actualAmountOfLikes = ecoNewsService.countLikesForEcoNews(1L);

        assertEquals(2, actualAmountOfLikes);
    }

    @Test
    void countDislikesForEcoNews() {
        // given
        EcoNews ecoNews = ModelUtils.getEcoNews();
        EcoNewsVO ecoNewsVO = ModelUtils.getEcoNewsVO();
        UserVO user1 = ModelUtils.getUserVO();
        UserVO user2 = UserVO.builder().id(2L).name("User2").build();
        ecoNewsVO.setUsersDislikedNews(Set.of(user1, user2));
        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.of(ecoNews));
        when(modelMapper.map(ecoNews, EcoNewsVO.class)).thenReturn(ecoNewsVO);

        // when
        Integer actual = ecoNewsService.countDislikesForEcoNews(1L);

        // then
        assertEquals(2, actual);
    }

    @Test
    void checkNewsIsLikedByUserTest() {
        UserVO userVO = ModelUtils.getUserVO();
        EcoNews ecoNews = ModelUtils.getEcoNews();
        EcoNewsVO ecoNewsVO = ModelUtils.getEcoNewsVO();
        Set<UserVO> usersLiked = new HashSet<>();
        usersLiked.add(UserVO.builder().id(2L).build());
        usersLiked.add(UserVO.builder().id(3L).build());
        ecoNewsVO.setUsersLikedNews(usersLiked);
        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.of(ecoNews));
        when(modelMapper.map(ecoNews, EcoNewsVO.class)).thenReturn(ecoNewsVO);

        boolean isLikedByUser = ecoNewsService.checkNewsIsLikedByUser(1L, userVO);

        assertFalse(isLikedByUser);
    }

    @Test
    void findDtoByIdAndLanguage() {
        EcoNews ecoNews = ModelUtils.getEcoNewsForFindDtoByIdAndLanguage();
        EcoNewsDto expected = ModelUtils.getEcoNewsDtoForFindDtoByIdAndLanguage();
        when(ecoNewsRepo.findById(anyLong())).thenReturn(Optional.of(ecoNews));
        assertEquals(expected, ecoNewsService.findDtoByIdAndLanguage(1L, "ua"));
    }

    @Test
    void findByFilters() {
        Pageable pageable = PageRequest.of(0, 2);
        List<EcoNews> ecoNews = Collections.singletonList(ModelUtils.getEcoNews());
        Page<EcoNews> page = new PageImpl<>(ecoNews, pageable, ecoNews.size());
        ArrayList<String> tags = new ArrayList<>();
        tags.add("новини");
        tags.add("news");
        Root<EcoNews> root = mock(Root.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        Join join = mock(Join.class);
        when(root.join(ECO_NEWS_JOIN_TAG)).thenReturn(join);
        Path tagTranslations = mock(Path.class);
        Path name = mock(Path.class);
        when(join.get(ECO_NEWS_TAG_TRANSLATION)).thenReturn(tagTranslations);
        when(tagTranslations.get(ECO_NEWS_TAG_TRANSLATION_NAME)).thenReturn(name);

        when(criteriaBuilder.lower(any())).thenReturn(name);
        when(criteriaBuilder.like(any(), anyString())).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.and(any())).thenReturn(mock(Predicate.class));

        ecoNewsService.getPredicate(root, criteriaBuilder, tags, "1");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        verify(criteriaBuilder, times(tags.size() + 1)).like(any(), captor.capture());

        assertEquals(List.of("%новини%", "%news%", "%1%"), captor.getAllValues());

        when(ecoNewsRepo.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        ecoNewsService.findByFilters(pageable, tags, "1");
        verify(ecoNewsRepo, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void findByFilters_ReturnsCorrectResult_WhenTagsAndTitleAreEmpty() {
        Pageable pageable = PageRequest.of(0, 2);
        List<EcoNews> ecoNews = Collections.singletonList(ModelUtils.getEcoNews());
        Page<EcoNews> page = new PageImpl<>(ecoNews, pageable, ecoNews.size());
        when(ecoNewsRepo.findAll(any(Pageable.class))).thenReturn(page);
        ecoNewsService.findByFilters(pageable, null, null);
        verify(ecoNewsRepo, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getContentAndSourceForEcoNewsById() {
        EcoNews ecoNews = ModelUtils.getEcoNews();
        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.of(ecoNews));

        ecoNewsService.getContentAndSourceForEcoNewsById(1L);

        verify(ecoNewsRepo).findById(1L);
    }

    @Test
    void uploadImages() {
        MultipartFile[] multipartFiles = {ModelUtils.getFile()};
        ecoNewsService.uploadImages(multipartFiles);
        Arrays.stream(multipartFiles).forEach(multipartFile -> verify(fileService).upload(multipartFile));
    }

    @Test
    void getContentAndSourceForEcoNewsByIdException() {
        assertThrows(NotFoundException.class, () -> ecoNewsService.getContentAndSourceForEcoNewsById(1L));
    }

    @Test
    void findUsersWhoLikedPost() {
        // given
        EcoNews ecoNews = ModelUtils.getEcoNews();
        User user1 = ModelUtils.getUser();
        UserVO user1VO = ModelUtils.getUserVO();
        ecoNews.setUsersLikedNews(Set.of(user1));
        when(ecoNewsRepo.findById(anyLong())).thenReturn(Optional.of(ecoNews));
        when(modelMapper.map(user1, UserVO.class)).thenReturn(user1VO);
        // when
        Set<UserVO> usersWhoLikedPost = ecoNewsService.findUsersWhoLikedPost(1L);
        // then
        assertEquals(1, usersWhoLikedPost.size());
        assertTrue(usersWhoLikedPost.contains(user1VO));
    }

    @Test
    void findUsersWhoDislikedPost() {
        // given
        EcoNews ecoNews = ModelUtils.getEcoNews();
        User user1 = ModelUtils.getUser();
        UserVO user1VO = ModelUtils.getUserVO();
        ecoNews.setUsersDislikedNews(Set.of(user1));
        when(ecoNewsRepo.findById(anyLong())).thenReturn(Optional.of(ecoNews));
        when(modelMapper.map(user1, UserVO.class)).thenReturn(user1VO);
        // when
        Set<UserVO> usersWhoDislikedPost = ecoNewsService.findUsersWhoDislikedPost(1L);
        // then
        assertEquals(1, usersWhoDislikedPost.size());
        assertTrue(usersWhoDislikedPost.contains(user1VO));
    }
}