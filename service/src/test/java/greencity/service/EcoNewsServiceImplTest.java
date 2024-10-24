package greencity.service;

import greencity.ModelUtils;
import greencity.TestConst;
import greencity.achievement.AchievementCalculation;
import greencity.client.RestClient;
import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
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
import greencity.dto.language.LanguageDTO;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.tag.TagVO;
import greencity.dto.user.UserVO;
import greencity.entity.EcoNews;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.entity.VerifyEmail;
import greencity.entity.RatingPoints;
import greencity.enums.AchievementAction;
import greencity.enums.AchievementCategoryType;
import greencity.enums.NotificationType;
import greencity.enums.Role;
import greencity.enums.TagType;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.filters.EcoNewsSpecification;
import greencity.filters.SearchCriteria;
import greencity.rating.RatingCalculation;
import greencity.repository.EcoNewsRepo;
import greencity.repository.EcoNewsSearchRepo;
import greencity.repository.RatingPointsRepo;
import greencity.repository.UserRepo;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import static greencity.ModelUtils.getEcoNews;
import static greencity.ModelUtils.getEcoNewsDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class EcoNewsServiceImplTest {
    @Mock
    EcoNewsRepo ecoNewsRepo;
    @Mock
    ModelMapper modelMapper;
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
    private UserRepo userRepo;
    @Mock
    private RatingCalculation ratingCalculation;
    @Mock
    private AchievementCalculation achievementCalculation;
    @Mock
    private RatingPointsRepo ratingPointsRepo;
    @Mock
    private CommentService commentService;
    @InjectMocks
    private EcoNewsServiceImpl ecoNewsService;
    @Mock
    private UserNotificationService userNotificationService;

    private EcoNews ecoNews;
    private final AddEcoNewsDtoRequest addEcoNewsDtoRequest = ModelUtils.getAddEcoNewsDtoRequest();
    private final AddEcoNewsDtoResponse addEcoNewsDtoResponse = ModelUtils.getAddEcoNewsDtoResponse();
    private final EcoNewsGenericDto ecoNewsGenericDto = ModelUtils.getEcoNewsGenericDto();

    private static final String ECO_NEWS_JOIN_TAG = "tags";
    private static final String ECO_NEWS_TAG_TRANSLATION = "tagTranslations";
    private static final String ECO_NEWS_TAG_TRANSLATION_NAME = "name";

    @BeforeEach
    void setUp() {
        ecoNews = getEcoNews();
    }

    @Test
    void save() throws MalformedURLException {
        MultipartFile image = ModelUtils.getFile();
        LanguageDTO dto = new LanguageDTO(1L, "en");
        List<Tag> tags = ModelUtils.getTags();

        when(modelMapper.map(addEcoNewsDtoRequest, EcoNews.class)).thenReturn(ecoNews);
        when(modelMapper.map(ecoNews, AddEcoNewsDtoResponse.class)).thenReturn(addEcoNewsDtoResponse);
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
    void saveWithExistedImageTest() {
        List<TagVO> tagVOList = Collections.singletonList(ModelUtils.getTagVO());

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

        AddEcoNewsDtoResponse actual = ecoNewsService.save(addEcoNewsDtoRequest, null, TestConst.EMAIL);

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
    void saveEcoNews() throws Exception {
        when(modelMapper.map(addEcoNewsDtoRequest, EcoNews.class)).thenReturn(ecoNews);
        when(restClient.findByEmail(TestConst.EMAIL)).thenReturn(ModelUtils.getUserVO());
        when(commentService.countCommentsForEcoNews(ecoNews.getId())).thenReturn(1);
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
        EcoNewsGenericDto actual = ecoNewsService.saveEcoNews(addEcoNewsDtoRequest, null, TestConst.EMAIL);

        assertEquals(ecoNewsGenericDto, actual);
    }

    @Test
    void delete() {
        String accessToken = "Token";
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
        List<EcoNewsDto> dtoList = List.of(getEcoNewsDto());

        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.of(ecoNews));
        when(ecoNewsRepo.getThreeRecommendedEcoNews(1L)).thenReturn(List.of(ecoNews));
        when(modelMapper.map(ecoNews, EcoNewsDto.class)).thenReturn(dtoList.getFirst());

        List<EcoNewsDto> actual = ecoNewsService.getThreeRecommendedEcoNews(1L);

        assertEquals(dtoList, actual);
    }

    @Test
    void deleteThrowExceptionTest() {
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
        List<EcoNews> ecoNewsList = Collections.singletonList(getEcoNews());
        SearchNewsDto searchNewsDto = ModelUtils.getSearchNewsDto();
        Page<EcoNews> page = new PageImpl<>(ecoNewsList, pageable, 2);
        List<SearchNewsDto> searchNewsDtos = Collections.singletonList(searchNewsDto);
        PageableDto<SearchNewsDto> actual = new PageableDto<>(searchNewsDtos, page.getTotalElements(),
            page.getPageable().getPageNumber(), page.getTotalPages());
        when(ecoNewsSearchRepo.find(pageable, "query", "en")).thenReturn(page);
        when(modelMapper.map(ecoNewsList, SearchNewsDto.class)).thenReturn(searchNewsDto);
        PageableDto<SearchNewsDto> expected = ecoNewsService.search(pageable, "query", "en");
        assertEquals(expected.getTotalPages(), actual.getTotalPages());
    }

    @Test
    void getAmountOfPublishedNewsTest() {
        when(ecoNewsRepo.countByAuthorId(1L)).thenReturn(10L);
        Long actual = ecoNewsService.getAmountOfPublishedNews(1L);
        assertEquals(10L, actual);
    }

    @Test
    void getAllByUserTest() {
        UserVO userVO = ModelUtils.getUserVO();
        List<EcoNews> ecoNewsList = Collections.singletonList(getEcoNews());
        List<EcoNewsDto> dtoList = Collections.singletonList(modelMapper.map(ecoNewsList, EcoNewsDto.class));

        when(ecoNewsRepo.findAllByAuthorId(userVO.getId())).thenReturn(ecoNewsList);

        List<EcoNewsDto> actual = ecoNewsService.getAllByUser(userVO);
        assertEquals(dtoList, actual);
    }

    @Test
    void updateVoidTest() {
        EcoNewsDtoManagement ecoNewsDtoManagement = ModelUtils.getEcoNewsDtoManagement();
        EcoNewsVO ecoNewsVO = ModelUtils.getEcoNewsVO();
        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.of(ecoNews));
        when(modelMapper.map(ecoNews, EcoNewsVO.class)).thenReturn(ecoNewsVO);
        when(modelMapper.map(ecoNewsVO, EcoNews.class)).thenReturn(ecoNews);
        when(ecoNewsRepo.save(ecoNews)).thenReturn(ecoNews);
        ecoNewsService.update(ecoNewsDtoManagement, any(MultipartFile.class));
        assertEquals(ecoNewsDtoManagement.getTitle(), ecoNews.getTitle());
    }

    @Test
    void updateEcoNewsDtoTest() {
        EcoNewsVO ecoNewsVO = ModelUtils.getEcoNewsVO();
        EcoNewsGenericDto ecoNewsDto = ModelUtils.getEcoNewsGenericDto();
        UpdateEcoNewsDto updateEcoNewsDto = ModelUtils.getUpdateEcoNewsDto();
        MultipartFile file = ModelUtils.getFile();
        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.of(ecoNews));
        when(modelMapper.map(ecoNews, EcoNewsVO.class)).thenReturn(ecoNewsVO);
        when(commentService.countCommentsForEcoNews(ecoNews.getId())).thenReturn(1);
        when(modelMapper.map(ecoNewsVO, EcoNews.class)).thenReturn(ecoNews);
        when(ecoNewsRepo.save(ecoNews)).thenReturn(ecoNews);
        when(fileService.upload(file)).thenReturn("https://google.com/");
        when(modelMapper.map(ecoNews, EcoNewsGenericDto.class)).thenReturn(ecoNewsDto);
        List<TagVO> tags = ModelUtils.getEcoNewsVO().getTags();
        when(tagService.findTagsByNamesAndType(updateEcoNewsDto.getTags(), TagType.ECO_NEWS)).thenReturn(tags);
        when(modelMapper.map(tagService
            .findTagsByNamesAndType(updateEcoNewsDto.getTags(), TagType.ECO_NEWS),
            new TypeToken<List<Tag>>() {
            }.getType())).thenReturn(ecoNews.getTags());

        EcoNewsGenericDto actual =
            ecoNewsService.update(updateEcoNewsDto, file, ModelUtils.getUserVO());
        assertEquals(ecoNewsDto, actual);
    }

    @Test
    void updateEcoNewsDtoTest_whenEcoNewsNotSaved_throwException() {
        UserVO userVO = ModelUtils.getUserVO();
        EcoNewsVO ecoNewsVO = ModelUtils.getEcoNewsVO();
        EcoNewsGenericDto ecoNewsDto = ModelUtils.getEcoNewsGenericDto();
        UpdateEcoNewsDto updateEcoNewsDto = ModelUtils.getUpdateEcoNewsDto();
        MultipartFile file = ModelUtils.getFile();
        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.of(ecoNews));
        when(modelMapper.map(ecoNews, EcoNewsVO.class)).thenReturn(ecoNewsVO);
        when(modelMapper.map(ecoNewsVO, EcoNews.class)).thenReturn(ecoNews);
        when(modelMapper.map(ecoNews, EcoNewsGenericDto.class)).thenReturn(ecoNewsDto);
        List<TagVO> tags = ModelUtils.getEcoNewsVO().getTags();
        when(tagService.findTagsByNamesAndType(updateEcoNewsDto.getTags(), TagType.ECO_NEWS)).thenReturn(tags);
        when(modelMapper.map(tagService
            .findTagsByNamesAndType(updateEcoNewsDto.getTags(), TagType.ECO_NEWS),
            new TypeToken<List<Tag>>() {
            }.getType())).thenReturn(ecoNews.getTags());
        when(ecoNewsRepo.save(ecoNews)).thenThrow(new RuntimeException());

        assertThrows(NotSavedException.class,
            () -> ecoNewsService.update(updateEcoNewsDto, file, userVO));

        verify(fileService).delete(anyString());
    }

    @Test
    void updateEcoNewsDtoThrowsExceptionTest() {
        UserVO user = ModelUtils.getUserVO();
        ecoNews.getAuthor().setId(2L);
        EcoNewsVO ecoNewsVO = ModelUtils.getEcoNewsVO();
        UpdateEcoNewsDto updateEcoNewsDto = ModelUtils.getUpdateEcoNewsDto();
        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.of(ecoNews));
        when(modelMapper.map(ecoNews, EcoNewsVO.class)).thenReturn(ecoNewsVO);
        when(modelMapper.map(ecoNewsVO, EcoNews.class)).thenReturn(ecoNews);
        assertThrows(BadRequestException.class, () -> ecoNewsService.update(updateEcoNewsDto, null, user));

    }

    @Test
    void getFilteredDataForManagementByPageTest() {
        Pageable pageable = PageRequest.of(0, 2);
        List<EcoNews> ecoNewsList = Collections.singletonList(getEcoNews());
        Page<EcoNews> page = new PageImpl<>(ecoNewsList, pageable, ecoNewsList.size());
        EcoNewsViewDto ecoNewsViewDto = new EcoNewsViewDto();
        EcoNewsDto ecoNewsDto = getEcoNewsDto();
        when(ecoNewsRepo.findAllByOrderByCreationDateDesc(any(Pageable.class))).thenReturn(page);
        when(modelMapper.map(ecoNewsList, EcoNewsDto.class)).thenReturn(ecoNewsDto);
        PageableAdvancedDto<EcoNewsDto> actual =
            ecoNewsService.getFilteredDataForManagementByPage("", pageable, ecoNewsViewDto, Locale.getDefault());
        PageableAdvancedDto<EcoNewsDto> expected =
            new PageableAdvancedDto<>(Collections.singletonList(ecoNewsDto), 1, 1, 1,
                1, false, false, false, false);
        assertEquals(expected.getTotalElements(), actual.getTotalElements());
    }

    @Test
    void getFilteredDataForManagementByPageWithEcoNewsViewDtoTest() {
        Pageable pageable = PageRequest.of(0, 2);
        List<EcoNews> ecoNewsList = Collections.singletonList(getEcoNews());
        Page<EcoNews> page = new PageImpl<>(ecoNewsList, pageable, ecoNewsList.size());
        EcoNewsViewDto ecoNewsViewDto = ModelUtils.getEcoNewsViewDto();
        EcoNewsDto ecoNewsDto = getEcoNewsDto();
        when(ecoNewsRepo.findAll(any(EcoNewsSpecification.class), any(Pageable.class))).thenReturn(page);
        when(modelMapper.map(ecoNewsList, EcoNewsDto.class)).thenReturn(ecoNewsDto);
        PageableAdvancedDto<EcoNewsDto> actual =
            ecoNewsService.getFilteredDataForManagementByPage("", pageable, ecoNewsViewDto, Locale.getDefault());
        PageableAdvancedDto<EcoNewsDto> expected =
            new PageableAdvancedDto<>(Collections.singletonList(ecoNewsDto), 1, 1, 1,
                1, false, false, false, false);
        assertEquals(expected.getTotalElements(), actual.getTotalElements());
    }

    @Test
    void getFilteredDataForManagementByPageWithQueryTest() {
        Pageable pageable = PageRequest.of(0, 2);
        List<EcoNews> ecoNewsList = Collections.singletonList(getEcoNews());
        Page<EcoNews> page = new PageImpl<>(ecoNewsList, pageable, ecoNewsList.size());
        EcoNewsViewDto ecoNewsViewDto = null;
        EcoNewsDto ecoNewsDto = getEcoNewsDto();
        String query = "query";
        when(ecoNewsRepo.searchEcoNewsBy(any(Pageable.class), eq(query))).thenReturn(page);
        when(modelMapper.map(ecoNewsList, EcoNewsDto.class)).thenReturn(ecoNewsDto);
        PageableAdvancedDto<EcoNewsDto> actual =
            ecoNewsService.getFilteredDataForManagementByPage(query, pageable, ecoNewsViewDto, Locale.getDefault());
        PageableAdvancedDto<EcoNewsDto> expected =
            new PageableAdvancedDto<>(Collections.singletonList(ecoNewsDto), 1, 1, 1,
                1, false, false, false, false);
        assertEquals(expected.getTotalElements(), actual.getTotalElements());
    }

    @Test
    void getFilteredDataForManagementByPageWithQueryAndEcoNewsViewDtoTest() {
        Pageable pageable = PageRequest.of(0, 2);
        List<EcoNews> ecoNewsByFields = Collections.singletonList(getEcoNews());
        List<EcoNews> ecoNewsByQuery = new ArrayList<>();
        ecoNewsByQuery.add(getEcoNews());
        ecoNewsByQuery.add(EcoNews.builder().id(2L).build());
        Page<EcoNews> pageByFields = new PageImpl<>(ecoNewsByFields, pageable, ecoNewsByFields.size());
        Page<EcoNews> pageByQuery = new PageImpl<>(ecoNewsByQuery, pageable, ecoNewsByQuery.size());
        EcoNewsViewDto ecoNewsViewDto = ModelUtils.getEcoNewsViewDto();
        EcoNewsDto ecoNewsDto = getEcoNewsDto();
        String query = "query";
        when(ecoNewsRepo.findAll(any(EcoNewsSpecification.class), any(Pageable.class))).thenReturn(pageByFields);
        when(ecoNewsRepo.searchEcoNewsBy(any(Pageable.class), eq(query))).thenReturn(pageByQuery);
        when(modelMapper.map(ecoNewsByFields, EcoNewsDto.class)).thenReturn(ecoNewsDto);
        PageableAdvancedDto<EcoNewsDto> actual =
            ecoNewsService.getFilteredDataForManagementByPage(query, pageable, ecoNewsViewDto, Locale.getDefault());
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
    void buildSearchCriteriaWithDateRangeTest() {
        EcoNewsViewDto ecoNewsViewDto = ModelUtils.getEcoNewsViewDto();
        List<SearchCriteria> actual = ecoNewsService.buildSearchCriteria(ecoNewsViewDto);
        assertEquals(7, actual.size());
    }

    @Test
    void buildSearchCriteriaWithStartDateTest() {
        EcoNewsViewDto ecoNewsViewDto = ModelUtils.getEcoNewsViewDto();
        ecoNewsViewDto.setEndDate(null);
        List<SearchCriteria> actual = ecoNewsService.buildSearchCriteria(ecoNewsViewDto);
        assertEquals(7, actual.size());
    }

    @Test
    void likeTest() {
        UserVO userVO = ModelUtils.getUserVO();
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
        EcoNewsVO ecoNewsVO = ModelUtils.getEcoNewsVO();
        ecoNewsVO.setUsersLikedNews(new HashSet<>());

        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.of(ecoNews));
        when(modelMapper.map(ecoNews, EcoNewsVO.class)).thenReturn(ecoNewsVO);
        when(modelMapper.map(ecoNewsVO, EcoNews.class)).thenReturn(ecoNews);

        assertThrows(BadRequestException.class, () -> ecoNewsService.like(userVO, 1L));
    }

    @Test
    void givenEcoNewsLikedByUser_whenLikedByUser_shouldRemoveLike() {
        UserVO userVO = ModelUtils.getUserVO();
        EcoNewsVO ecoNewsVO = ModelUtils.getEcoNewsVO();
        ecoNewsVO.getAuthor().setId(2L);
        ecoNewsVO.setUsersLikedNews(new HashSet<>());
        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.of(ecoNews));
        when(modelMapper.map(ecoNews, EcoNewsVO.class)).thenReturn(ecoNewsVO);
        when(modelMapper.map(ecoNewsVO, EcoNews.class)).thenReturn(ecoNews);

        ecoNewsService.like(userVO, 1L);
        ecoNewsService.like(userVO, 1L);

        assertEquals(0, ecoNewsVO.getUsersLikedNews().size());
    }

    @Test
    void dislikeTest() {
        UserVO userVO = ModelUtils.getUserVO();
        EcoNewsVO ecoNewsVO = ModelUtils.getEcoNewsVO();
        ecoNewsVO.setUsersDislikedNews(new HashSet<>());
        when(ecoNewsRepo.findById(anyLong())).thenReturn(Optional.of(ecoNews));
        when(modelMapper.map(ecoNews, EcoNewsVO.class)).thenReturn(ecoNewsVO);
        when(modelMapper.map(ecoNewsVO, EcoNews.class)).thenReturn(ecoNews);

        ecoNewsService.dislike(userVO, 1L);

        assertTrue(ecoNewsVO.getUsersDislikedNews().contains(userVO));
        assertEquals(1, ecoNewsVO.getUsersDislikedNews().size());
        verify(ecoNewsRepo).save(ecoNews);
    }

    @Test
    void givenEcoNewsLikedByUser_whenDislikedByUser_shouldRemoveLikeAndAddDislike() {
        UserVO userVO = ModelUtils.getUserVO();
        EcoNewsVO ecoNewsVO = ModelUtils.getEcoNewsVO();
        ecoNewsVO.setUsersLikedNews(new HashSet<>(Set.of(userVO)));
        ecoNewsVO.setUsersDislikedNews(new HashSet<>());
        when(ecoNewsRepo.findById(anyLong())).thenReturn(Optional.of(ecoNews));
        when(modelMapper.map(ecoNews, EcoNewsVO.class)).thenReturn(ecoNewsVO);
        when(modelMapper.map(ecoNewsVO, EcoNews.class)).thenReturn(ecoNews);

        ecoNewsService.dislike(userVO, 1L);

        assertTrue(ecoNewsVO.getUsersDislikedNews().contains(userVO));
        assertTrue(ecoNewsVO.getUsersLikedNews().isEmpty());
        assertEquals(1, ecoNewsVO.getUsersDislikedNews().size());
        assertEquals(0, ecoNewsVO.getUsersLikedNews().size());
    }

    @Test
    void countLikesForEcoNews() {
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
        EcoNewsVO ecoNewsVO = ModelUtils.getEcoNewsVO();
        UserVO user1 = ModelUtils.getUserVO();
        UserVO user2 = UserVO.builder().id(2L).name("User2").build();
        ecoNewsVO.setUsersDislikedNews(Set.of(user1, user2));
        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.of(ecoNews));
        when(modelMapper.map(ecoNews, EcoNewsVO.class)).thenReturn(ecoNewsVO);

        Integer actual = ecoNewsService.countDislikesForEcoNews(1L);

        assertEquals(2, actual);
    }

    @Test
    void checkNewsIsLikedByUserTest() {
        UserVO userVO = ModelUtils.getUserVO();
        EcoNewsVO ecoNewsVO = ModelUtils.getEcoNewsVO();
        Set<UserVO> usersLiked = new HashSet<>();
        usersLiked.add(UserVO.builder().id(2L).build());
        usersLiked.add(UserVO.builder().id(3L).build());
        ecoNewsVO.setUsersLikedNews(usersLiked);
        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.of(ecoNews));
        when(modelMapper.map(ecoNews, EcoNewsVO.class)).thenReturn(ecoNewsVO);

        boolean isLikedByUser = ecoNewsService.checkNewsIsLikedByUser(1L, userVO.getId());

        assertFalse(isLikedByUser);
    }

    @Test
    void findDtoByIdAndLanguage() {
        EcoNews ecoNewsForFindDtoByIdAndLanguage = ModelUtils.getEcoNewsForFindDtoByIdAndLanguage();
        EcoNewsDto expected = ModelUtils.getEcoNewsDtoForFindDtoByIdAndLanguage();
        when(ecoNewsRepo.findById(anyLong())).thenReturn(Optional.of(ecoNewsForFindDtoByIdAndLanguage));
        assertEquals(expected, ecoNewsService.findDtoByIdAndLanguage(1L, "ua"));
    }

    @Test
    void find() {
        Pageable pageable = PageRequest.of(0, 2);
        List<EcoNews> ecoNewsList = Collections.singletonList(getEcoNews());
        Page<EcoNews> page = new PageImpl<>(ecoNewsList, pageable, ecoNewsList.size());
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

        Join userJoin = mock(Join.class);
        Path path = mock(Path.class);
        when(root.join("author")).thenReturn(userJoin);
        when(userJoin.get("id")).thenReturn(path);

        when(criteriaBuilder.lower(any())).thenReturn(name);
        when(criteriaBuilder.like(any(), anyString())).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.and(any())).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.equal(any(), any())).thenReturn(mock(Predicate.class));

        ecoNewsService.getPredicate(root, criteriaBuilder, tags, "1", 1L);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        verify(criteriaBuilder, times(tags.size() + 1)).like(any(), captor.capture());

        assertEquals(List.of("%новини%", "%news%", "%1%"), captor.getAllValues());

        when(ecoNewsRepo.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        ecoNewsService.find(pageable, tags, "1", 1L);
        verify(ecoNewsRepo, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void find_ReturnsCorrectResult_WhenTagsAndTitleAreEmpty() {
        Pageable pageable = PageRequest.of(0, 2);
        List<EcoNews> ecoNewsList = Collections.singletonList(getEcoNews());
        Page<EcoNews> page = new PageImpl<>(ecoNewsList, pageable, ecoNewsList.size());
        when(ecoNewsRepo.findAll(any(Pageable.class))).thenReturn(page);
        ecoNewsService.find(pageable, null, null, null);
        verify(ecoNewsRepo, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getContentAndSourceForEcoNewsById() {
        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.of(ecoNews));

        ecoNewsService.getContentAndSourceForEcoNewsById(1L);

        verify(ecoNewsRepo).findById(1L);
    }

    @Test
    void getContentAndSourceForEcoNewsByIdException() {
        assertThrows(NotFoundException.class, () -> ecoNewsService.getContentAndSourceForEcoNewsById(1L));
    }

    @Test
    void findUsersWhoLikedPost() {
        User user1 = ModelUtils.getUser();
        UserVO user1VO = ModelUtils.getUserVO();
        ecoNews.setUsersLikedNews(Set.of(user1));
        when(ecoNewsRepo.findById(anyLong())).thenReturn(Optional.of(ecoNews));
        when(modelMapper.map(user1, UserVO.class)).thenReturn(user1VO);

        Set<UserVO> usersWhoLikedPost = ecoNewsService.findUsersWhoLikedPost(1L);

        assertEquals(1, usersWhoLikedPost.size());
        assertTrue(usersWhoLikedPost.contains(user1VO));
    }

    @Test
    void findUsersWhoDislikedPost() {
        User user1 = ModelUtils.getUser();
        UserVO user1VO = ModelUtils.getUserVO();
        ecoNews.setUsersDislikedNews(Set.of(user1));
        when(ecoNewsRepo.findById(anyLong())).thenReturn(Optional.of(ecoNews));
        when(modelMapper.map(user1, UserVO.class)).thenReturn(user1VO);

        Set<UserVO> usersWhoDislikedPost = ecoNewsService.findUsersWhoDislikedPost(1L);

        assertEquals(1, usersWhoDislikedPost.size());
        assertTrue(usersWhoDislikedPost.contains(user1VO));
    }

    @Test
    void testLikeAddLike() {
        UserVO actionUser = ModelUtils.getUserVO();
        UserVO targetUser = ModelUtils.getAuthorVO();
        EcoNewsVO ecoNewsVO = ModelUtils.getEcoNewsVO();
        ecoNewsVO.setAuthor(targetUser);
        ecoNewsVO.setUsersLikedNews(new HashSet<>());
        EcoNews ecoNewsWithAuthor = getEcoNews();
        ecoNewsWithAuthor.setAuthor(User.builder()
            .id(targetUser.getId())
            .build());
        RatingPoints ratingPoints = RatingPoints.builder().id(1L).name("LIKE_NEWS").points(1).build();

        when(ratingPointsRepo.findByNameOrThrow("LIKE_NEWS")).thenReturn(ratingPoints);
        when(ecoNewsRepo.save(any(EcoNews.class))).thenReturn(ecoNewsWithAuthor);
        when(ecoNewsRepo.findById(anyLong())).thenReturn(Optional.of(ecoNewsWithAuthor));
        when(modelMapper.map(any(EcoNews.class), eq(EcoNewsVO.class))).thenReturn(ecoNewsVO);
        when(userService.findById(anyLong())).thenReturn(targetUser);

        ecoNewsService.like(actionUser, ecoNewsVO.getId());

        assertTrue(ecoNewsVO.getUsersLikedNews().contains(actionUser));
        verify(userNotificationService, times(1)).createOrUpdateLikeNotification(
            targetUser, actionUser, ecoNewsVO.getId(), ecoNewsVO.getTitle(), NotificationType.ECONEWS_LIKE, true);
        verify(achievementCalculation, times(1)).calculateAchievement(actionUser,
            AchievementCategoryType.LIKE_NEWS, AchievementAction.ASSIGN);
        verify(ratingCalculation, times(1))
            .ratingCalculation(ratingPoints, actionUser);
    }

    @Test
    void testLikeUndoLike() {
        User author = ModelUtils.getUser();
        User action = User.builder()
            .id(2L)
            .email(TestConst.EMAIL)
            .name(TestConst.NAME)
            .role(Role.ROLE_USER)
            .userStatus(UserStatus.ACTIVATED)
            .lastActivityTime(LocalDateTime.now())
            .verifyEmail(new VerifyEmail())
            .dateOfRegistration(LocalDateTime.now())
            .subscribedEvents(new HashSet<>())
            .favoriteEvents(new HashSet<>())
            .build();

        ModelMapper mapper = new ModelMapper();
        UserVO userVO = mapper.map(action, UserVO.class);
        UserVO actionUser = mapper.map(action, UserVO.class);

        EcoNews news = EcoNews.builder()
            .id(1L)
            .author(author)
            .title("test title")
            .usersLikedNews(new HashSet<>(Set.of(action)))
            .build();
        EcoNewsVO ecoNewsVO = mapper.map(news, EcoNewsVO.class);
        RatingPoints ratingPoints = RatingPoints.builder().id(2L).name("UNDO_LIKE_NEWS").points(-1).build();
        ecoNewsVO.setUsersLikedNews(new HashSet<>(ecoNewsVO.getUsersLikedNews()));

        when(ecoNewsRepo.findById(anyLong())).thenReturn(Optional.of(news));
        when(modelMapper.map(any(EcoNews.class), eq(EcoNewsVO.class))).thenReturn(ecoNewsVO);
        when(ratingPointsRepo.findByNameOrThrow("UNDO_LIKE_NEWS")).thenReturn(ratingPoints);
        ecoNewsService.like(userVO, news.getId());

        assertFalse(ecoNewsVO.getUsersLikedNews().contains(actionUser));

        verify(userNotificationService, times(1)).createOrUpdateLikeNotification(
            null, actionUser, ecoNewsVO.getId(), "test title",
            NotificationType.ECONEWS_LIKE, false);
        verify(achievementCalculation, times(1))
            .calculateAchievement(actionUser, AchievementCategoryType.LIKE_NEWS, AchievementAction.DELETE);
        verify(ratingCalculation, times(1))
            .ratingCalculation(ratingPoints, actionUser);
    }

    @Test
    void setHiddenValue() {
        String accessToken = "Token";
        when(httpServletRequest.getHeader("Authorization")).thenReturn(accessToken);
        UserVO adminVO = ModelUtils.getUserVO().setRole(Role.ROLE_ADMIN);
        EcoNews ecoNew = getEcoNews();
        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.of(ecoNew));

        ecoNewsService.setHiddenValue(1L, adminVO, true);
        verify(ecoNewsRepo, times(1)).save(ecoNew.setHidden(true));
    }

    @Test
    void setHiddenWithNotAdminValueThrowExceptionTest() {
        String accessToken = "Token";
        when(httpServletRequest.getHeader("Authorization")).thenReturn(accessToken);
        UserVO userVO = ModelUtils.getUserVO();

        assertThrows(BadRequestException.class, () -> ecoNewsService.setHiddenValue(1L, userVO, true));
    }

    @Test
    void setHiddenWithWrongIdValueThrowExceptionTest() {
        String accessToken = "Token";
        when(httpServletRequest.getHeader("Authorization")).thenReturn(accessToken);
        UserVO adminVO = ModelUtils.getUserVO().setRole(Role.ROLE_ADMIN);
        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> ecoNewsService.setHiddenValue(1L, adminVO, true));
    }

    @Test
    void findByTitleTest() {
        String title = "Test Title";
        EcoNews ecoNews = getEcoNews();
        List<EcoNews> ecoNewsList = List.of(ecoNews);
        EcoNewsDto ecoNewsDto = getEcoNewsDto();
        List<EcoNewsDto> ecoNewsDtoList = List.of(ecoNewsDto);

        when(ecoNewsRepo.findByTitleContaining(title)).thenReturn(ecoNewsList);
        when(modelMapper.map(ecoNews, EcoNewsDto.class)).thenReturn(ecoNewsDto);

        List<EcoNewsDto> actualEcoNewsDtoList = ecoNewsService.findByTitle(title);

        assertEquals(ecoNewsDtoList, actualEcoNewsDtoList);
        verify(ecoNewsRepo).findByTitleContaining(title);
        verify(modelMapper).map(ecoNews, EcoNewsDto.class);
    }

    @Test
    void addToFavorites_ShouldAddUserToFavorites() {
        User user = ModelUtils.getUser();

        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.of(ecoNews));
        when(userRepo.findByEmail(TestConst.EMAIL)).thenReturn(Optional.of(user));

        ecoNewsService.addToFavorites(1L, TestConst.EMAIL);

        assertTrue(ecoNews.getFollowers().contains(user));
        verify(ecoNewsRepo).save(ecoNews);
        verify(ecoNewsRepo).findById(1L);
        verify(userRepo).findByEmail(TestConst.EMAIL);
    }

    @Test
    void addToFavorites_ShouldThrowExceptionIfEcoNewsNotFound() {
        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                ecoNewsService.addToFavorites(1L, TestConst.EMAIL));

        assertEquals(ErrorMessage.ECO_NEW_NOT_FOUND_BY_ID + 1L, exception.getMessage());
        verify(ecoNewsRepo).findById(1L);
    }

    @Test
    void addToFavorites_ShouldThrowExceptionIfUserNotFound() {
        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.of(ecoNews));
        when(userRepo.findByEmail(TestConst.EMAIL)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                ecoNewsService.addToFavorites(1L, TestConst.EMAIL));

        assertEquals(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + TestConst.EMAIL, exception.getMessage());
        verify(ecoNewsRepo).findById(1L);
        verify(userRepo).findByEmail(TestConst.EMAIL);
    }

    @Test
    void addToFavorites_ShouldThrowExceptionIfUserAlreadyInFavorites() {
        User user = ModelUtils.getUser();

        ecoNews.setFollowers(Collections.singleton(user));
        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.of(ecoNews));
        when(userRepo.findByEmail(TestConst.EMAIL)).thenReturn(Optional.of(user));

        BadRequestException exception =
            assertThrows(BadRequestException.class, () -> ecoNewsService.addToFavorites(1L, TestConst.EMAIL));

        assertEquals(ErrorMessage.USER_HAS_ALREADY_ADDED_ECO_NEW_TO_FAVORITES, exception.getMessage());
        verify(ecoNewsRepo).findById(1L);
        verify(userRepo).findByEmail(TestConst.EMAIL);
    }

    @Test
    void removeFromFavorites_ShouldRemoveUserFromFavorites() {
        User user = ModelUtils.getUser();

        ecoNews.setFollowers(Collections.singleton(user));
        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.of(ecoNews));
        when(userRepo.findByEmail(TestConst.EMAIL)).thenReturn(Optional.of(user));

        ecoNewsService.removeFromFavorites(1L, TestConst.EMAIL);

        assertFalse(ecoNews.getFollowers().contains(user));
        verify(ecoNewsRepo).save(ecoNews);
        verify(ecoNewsRepo).findById(1L);
        verify(userRepo).findByEmail(TestConst.EMAIL);
    }

    @Test
    void removeFromFavorites_ShouldThrowExceptionIfEcoNewsNotFound() {
        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                ecoNewsService.removeFromFavorites(1L, TestConst.EMAIL));

        assertEquals(ErrorMessage.ECO_NEW_NOT_FOUND_BY_ID + 1L, exception.getMessage());
        verify(ecoNewsRepo).findById(1L);
    }

    @Test
    void removeFromFavorites_ShouldThrowExceptionIfUserNotFound() {
        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.of(ecoNews));
        when(userRepo.findByEmail(TestConst.EMAIL)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                ecoNewsService.removeFromFavorites(1L, TestConst.EMAIL));

        assertEquals(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + TestConst.EMAIL, exception.getMessage());
        verify(ecoNewsRepo).findById(1L);
        verify(userRepo).findByEmail(TestConst.EMAIL);
    }

    @Test
    void removeFromFavorites_ShouldThrowExceptionIfUserNotInFavorites() {
        User user = ModelUtils.getUser();

        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.of(ecoNews));
        when(userRepo.findByEmail(TestConst.EMAIL)).thenReturn(Optional.of(user));

        BadRequestException exception =
            assertThrows(BadRequestException.class, () -> ecoNewsService.removeFromFavorites(1L, TestConst.EMAIL));

        assertEquals(ErrorMessage.ECO_NEW_NOT_IN_FAVORITES, exception.getMessage());
        verify(ecoNewsRepo).findById(1L);
        verify(userRepo).findByEmail(TestConst.EMAIL);
    }

    @Test
    void getFavorites_ShouldReturnListOfFavoriteEcoNewsDto() {
        User user = ModelUtils.getUser();
        EcoNews ecoNews1 = ModelUtils.getEcoNews();
        EcoNews ecoNews2 = ModelUtils.getEcoNews();

        ecoNews1.setId(1L);
        ecoNews2.setId(2L);

        user.setFavoriteEcoNews(new HashSet<>(List.of(ecoNews1, ecoNews2)));

        when(userRepo.findByEmail(TestConst.EMAIL)).thenReturn(Optional.of(user));
        when(modelMapper.map(ecoNews1, EcoNewsDto.class)).thenReturn(ModelUtils.getEcoNewsDto());
        when(modelMapper.map(ecoNews2, EcoNewsDto.class)).thenReturn(ModelUtils.getEcoNewsDto());

        List<EcoNewsDto> favorites = ecoNewsService.getFavorites(TestConst.EMAIL);

        assertEquals(2, favorites.size());
        verify(userRepo).findByEmail(TestConst.EMAIL);
        verify(modelMapper).map(ecoNews1, EcoNewsDto.class);
        verify(modelMapper).map(ecoNews2, EcoNewsDto.class);
    }

    @Test
    void getFavorites_ShouldThrowExceptionIfUserNotFound() {
        when(userRepo.findByEmail(TestConst.EMAIL)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                ecoNewsService.getFavorites(TestConst.EMAIL));

        assertEquals(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + TestConst.EMAIL, exception.getMessage());
        verify(userRepo).findByEmail(TestConst.EMAIL);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void getFavorites_ShouldReturnEmptyListIfNoFavorites() {
        User user = ModelUtils.getUser();
        user.setFavoriteEcoNews(new HashSet<>());

        when(userRepo.findByEmail(TestConst.EMAIL)).thenReturn(Optional.of(user));

        List<EcoNewsDto> favorites = ecoNewsService.getFavorites(TestConst.EMAIL);

        assertTrue(favorites.isEmpty());
        verify(userRepo).findByEmail(TestConst.EMAIL);
        verifyNoInteractions(modelMapper);
    }
}