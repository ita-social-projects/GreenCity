package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import static greencity.ModelUtils.getEcoNewsDto;
import static greencity.ModelUtils.getPrincipal;
import static greencity.ModelUtils.getUserClaims;
import static greencity.ModelUtils.getUserVO;
import static greencity.ModelUtils.getEcoNewsGroupedTagsDto;

import greencity.TestConst;
import greencity.constant.ErrorMessage;
import greencity.converters.UserArgumentResolver;
import greencity.converters.UserIdArgumentResolver;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.econews.UpdateEcoNewsDto;
import greencity.dto.user.UserClaims;
import greencity.dto.user.UserVO;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.properties.OpenAiProperties;
import greencity.security.jwt.JwtTool;
import greencity.service.EcoNewsRelevanceService;
import greencity.service.EcoNewsService;
import greencity.service.TagsService;
import greencity.service.UserService;
import java.security.Principal;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static greencity.ModelUtils.getUpdateEcoNewsDto;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EcoNewsControllerTest {
    private static final String ecoNewsLink = "/eco-news";
    private MockMvc mockMvc;
    @InjectMocks
    private EcoNewsController ecoNewsController;
    @Mock
    private EcoNewsService ecoNewsService;
    @Mock
    private EcoNewsRelevanceService ecoNewsRelevanceService;
    @Mock
    private TagsService tagsService;
    @Mock
    private UserService userService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private OpenAiProperties openAiProperties;
    @Mock
    JwtTool jwtTool;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final Principal principal = getPrincipal();

    private ErrorAttributes errorAttributes = new DefaultErrorAttributes();

    @BeforeAll
    static void setup() {
        OBJECT_MAPPER.findAndRegisterModules();
    }

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
            .standaloneSetup(ecoNewsController)
            .setCustomArgumentResolvers(
                new PageableHandlerMethodArgumentResolver(),
                new UserArgumentResolver(userService, modelMapper),
                new UserIdArgumentResolver(jwtTool))
            .setControllerAdvice(new CustomExceptionHandler(errorAttributes, OBJECT_MAPPER, null))
            .build();

        String jwt = "jwt";
        when(jwtTool.extractJwtFromNativeWebRequest(any()))
            .thenReturn(jwt);
        when(jwtTool.extractUserId(jwt))
            .thenReturn(TestConst.USER_ID);
        when(openAiProperties.getRelevance())
            .thenReturn("enabled");
    }

    @Test
    void saveTest() throws Exception {
        String json = """
                {
                    "title": "title",
                    "tags": ["news"],
                    "text": "content content content",
                    "source": ""
                }
            """;
        MockMultipartFile jsonFile =
            new MockMultipartFile("addEcoNewsDtoRequest", "", "application/json", json.getBytes());

        this.mockMvc.perform(multipart(ecoNewsLink)
            .file(jsonFile)
            .principal(principal)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
            .andExpect(status().isCreated());

        ObjectMapper mapper = new ObjectMapper();
        AddEcoNewsDtoRequest addEcoNewsDtoRequest = mapper.readValue(json, AddEcoNewsDtoRequest.class);

        verify(ecoNewsService)
            .saveEcoNews(eq(addEcoNewsDtoRequest), isNull(), eq("test@gmail.com"));
    }

    @Test
    void saveBadRequestTest() throws Exception {
        mockMvc.perform(post(ecoNewsLink)
            .content("{}")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getEcoNewsById() throws Exception {
        mockMvc.perform(get(ecoNewsLink + "/{ecoNewsId}", 1))
            .andExpect(status().isOk());

        verify(ecoNewsService).findDtoByIdAndLanguage(1L, "en");
    }

    @Test
    void findAllFavoriteTest() throws Exception {
        int pageNumber = 1;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        mockMvc.perform(get(ecoNewsLink + "?favorite=true&page=1")
            .principal(principal)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(ecoNewsService).find(pageable, null, null, null, true, TestConst.USER_ID);
    }

    @Test
    void findAllTest() throws Exception {
        int pageNumber = 0;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        mockMvc.perform(get(ecoNewsLink + "?page=0")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(ecoNewsService).find(pageable, null, null, null, false, TestConst.USER_ID);
    }

    @Test
    void findAllByUserTest() throws Exception {
        int pageNumber = 1;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        mockMvc.perform(get(ecoNewsLink + "?author-id=1&page=1")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(ecoNewsService).find(pageable, null, null, 1L, false, TestConst.USER_ID);
    }

    @Test
    void deleteTest() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findNotDeactivatedByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(delete(ecoNewsLink + "/{econewsId}", 1)
            .principal(principal))
            .andExpect(status().isOk());

        verify(ecoNewsService).delete(1L, userVO);
    }

    @Test
    void getThreeRecommendedEcoNewsTest() throws Exception {
        mockMvc.perform(get(ecoNewsLink + "/{ecoNewsId}/recommended", 1L))
            .andExpect(status().isOk());

        verify(ecoNewsService).getThreeRecommendedEcoNews(1L);
    }

    @Test
    void findAllEcoNewsTagsTest() throws Exception {
        String language = "en";
        mockMvc.perform(get(ecoNewsLink + "/tags?lang=" + language))
            .andExpect(status().isOk());

        verify(tagsService).findAllEcoNewsTags(language);
    }

    @Test
    void likeTest() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findNotDeactivatedByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(post(ecoNewsLink + "/{ecoNewsId}/likes", 1)
            .principal(principal))
            .andExpect(status().isOk());

        verify(ecoNewsService).like(userVO, 1L);
    }

    @Test
    void dislikeTest() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findNotDeactivatedByEmail(anyString())).thenReturn(userVO);
        mockMvc.perform(post(ecoNewsLink + "/{ecoNewsId}/dislikes", 1)
            .principal(principal))
            .andExpect(status().isOk());
        verify(ecoNewsService).dislike(userVO, 1L);

    }

    @Test
    void countLikesForEcoNewsTest() throws Exception {
        mockMvc.perform(get(ecoNewsLink + "/{ecoNewsId}/likes/count", 1L))
            .andExpect(status().isOk());

        verify(ecoNewsService).countLikesForEcoNews(1L);
    }

    @Test
    void countDislikesForEcoNewsTest() throws Exception {
        mockMvc.perform(get(ecoNewsLink + "/{ecoNewsId}/dislikes/count", 1L))
            .andExpect(status().isOk());

        verify(ecoNewsService).countDislikesForEcoNews(1L);
    }

    @Test
    void checkNewsIsLikedByUserTest() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findNotDeactivatedByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get(ecoNewsLink + "/{ecoNewsId}/likes/{userId}", 1, 1)
            .principal(principal))
            .andExpect(status().isOk());

        verify(ecoNewsService).checkNewsIsLikedByUser(1L, userVO.getId());
    }

    @Test
    void findAmountOfPublishedNews() throws Exception {
        mockMvc.perform(get(ecoNewsLink + "/count")
            .param("author-id", "1"))
            .andExpect(status().isOk());

        verify(ecoNewsService).getAmountOfPublishedNews(1L);
    }

    @Test
    void findAmountOfPublishedNewsExternal() throws Exception {
        mockMvc.perform(get(ecoNewsLink + "/count/external")
            .param("authorEmail", "test@email"))
            .andExpect(status().isOk());

        verify(ecoNewsService).getAmountOfPublishedNews("test@email");
    }

    @Test
    void getContentAndSourceForEcoNewsById() throws Exception {
        mockMvc.perform(get(ecoNewsLink + "/{ecoNewsId}/summary", 1L))
            .andExpect(status().isOk());

        verify(ecoNewsService).getContentAndSourceForEcoNewsById(1L);

    }

    @Test
    void getContentAndSourceForEcoNewsByIdNot_Found_Request() throws Exception {
        when(ecoNewsService.getContentAndSourceForEcoNewsById(1L)).thenThrow(NotFoundException.class);

        mockMvc.perform(get(ecoNewsLink + "/{ecoNewsId}/summary", 1L))
            .andExpect(status().isNotFound());

        verify(ecoNewsService).getContentAndSourceForEcoNewsById(1L);
    }

    @Test
    void addToFavoritesTest() throws Exception {
        mockMvc.perform(post(ecoNewsLink + "/{ecoNewsId}/favorites", 1L)
            .principal(principal)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(ecoNewsService).addToFavorites(1L, TestConst.USER_ID);
    }

    @Test
    void addToFavoritesNotFoundTest() throws Exception {
        doThrow(new NotFoundException("Resource not found")).when(ecoNewsService).addToFavorites(anyLong(),
            anyLong());

        mockMvc.perform(post(ecoNewsLink + "/{ecoNewsId}/favorites", 1L)
            .principal(principal)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        verify(ecoNewsService).addToFavorites(1L, TestConst.USER_ID);
    }

    @Test
    void addToFavoritesBadRequestTest() throws Exception {
        doThrow(new IllegalArgumentException("Bad request")).when(ecoNewsService).addToFavorites(anyLong(),
            anyLong());

        mockMvc.perform(post(ecoNewsLink + "/{ecoNewsId}/favorites", 1L)
            .principal(principal)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        verify(ecoNewsService).addToFavorites(1L, TestConst.USER_ID);
    }

    @Test
    void removeFromFavoritesNotFoundTest() throws Exception {
        doThrow(new NotFoundException("Resource not found")).when(ecoNewsService).removeFromFavorites(anyLong(),
            anyLong());

        mockMvc.perform(delete(ecoNewsLink + "/{ecoNewsId}/favorites", 1L)
            .principal(principal)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        verify(ecoNewsService).removeFromFavorites(1L, TestConst.USER_ID);
    }

    @Test
    void removeFromFavoritesBadRequestTest() throws Exception {
        doThrow(new IllegalArgumentException("Bad request")).when(ecoNewsService).removeFromFavorites(anyLong(),
            anyLong());

        mockMvc.perform(delete(ecoNewsLink + "/{ecoNewsId}/favorites", 1L)
            .principal(principal)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        verify(ecoNewsService).removeFromFavorites(1L, TestConst.USER_ID);
    }

    @Test
    void removeFromFavoritesTest() throws Exception {
        mockMvc.perform(delete(ecoNewsLink + "/{ecoNewsId}/favorites", 1L)
            .principal(principal)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(ecoNewsService).removeFromFavorites(1L, TestConst.USER_ID);
    }

    @Test
    @SneakyThrows
    void checkLikeV2WithValidDataReturnsCorrectBody() {
        long ecoNewsId = 1L;
        UserVO userVO = getUserVO();
        EcoNewsDto ecoNewsDto = getEcoNewsDto();
        when(userService.findNotDeactivatedByEmail(anyString())).thenReturn(userVO);
        when(ecoNewsService.likeV2(userVO, ecoNewsId)).thenReturn(ecoNewsDto);
        String expectedResponse = OBJECT_MAPPER.writeValueAsString(ecoNewsDto);
        mockMvc.perform(post(ecoNewsLink + "/{ecoNewsId}/likeV2", ecoNewsId)
            .principal(principal)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedResponse));
        verify(ecoNewsService, times(1)).likeV2(userVO, ecoNewsId);
    }

    @Test
    @SneakyThrows
    void checkDislikeV2WithValidDataReturnsCorrectBody() {
        long ecoNewsId = 1L;
        UserVO userVO = getUserVO();
        EcoNewsDto ecoNewsDto = getEcoNewsDto();
        when(userService.findNotDeactivatedByEmail(anyString())).thenReturn(userVO);
        when(ecoNewsService.dislikeV2(userVO, ecoNewsId)).thenReturn(ecoNewsDto);
        String expectedResponse = OBJECT_MAPPER.writeValueAsString(ecoNewsDto);
        mockMvc.perform(post(ecoNewsLink + "/{ecoNewsId}/dislikeV2", ecoNewsId)
            .principal(principal)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedResponse));
        verify(ecoNewsService, times(1)).dislikeV2(userVO, ecoNewsId);
    }

    @Test
    void getEcoNewsByIdV2Test() throws Exception {
        when(ecoNewsService.findDtoById(anyLong())).thenReturn(getEcoNewsGroupedTagsDto());
        mockMvc.perform(get(ecoNewsLink + "/{ecoNewsId}/v2", 1L)
            .principal(principal)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void getEcoNewsByIdV2NotFoundTest() throws Exception {
        when(ecoNewsService.findDtoById(1L))
            .thenThrow(new NotFoundException(ErrorMessage.ECO_NEW_NOT_FOUND_BY_ID + 1L));
        mockMvc.perform(get(ecoNewsLink + "/{ecoNewsId}/v2", 1L)
            .principal(principal)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    void updateEcoNewsTest() throws Exception {
        UserVO userVO = getUserVO();
        UpdateEcoNewsDto updateEcoNewsDto = getUpdateEcoNewsDto();
        long ecoNewsId = updateEcoNewsDto.getId();

        when(userService.findNotDeactivatedByEmail(anyString()))
            .thenReturn(userVO);

        String json = OBJECT_MAPPER.writeValueAsString(updateEcoNewsDto);
        MockMultipartFile jsonFile = new MockMultipartFile(
            "updateEcoNewsDto", "", "application/json", json.getBytes());
        mockMvc.perform(multipart(HttpMethod.PUT, ecoNewsLink + "/{ecoNewsId}", ecoNewsId)
            .file(jsonFile)
            .principal(principal)
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(ecoNewsService).update(any(UpdateEcoNewsDto.class), isNull(), any(UserClaims.class));
    }

    @Test
    void updateEcoNewsWithWrongIdTest() throws Exception {
        UserVO userVO = getUserVO();
        UpdateEcoNewsDto updateEcoNewsDto = getUpdateEcoNewsDto();
        UserClaims userClaims = getUserClaims();
        long ecoNewsId = updateEcoNewsDto.getId() + 1;

        when(userService.findNotDeactivatedByEmail(anyString()))
            .thenReturn(userVO);

        String json = OBJECT_MAPPER.writeValueAsString(updateEcoNewsDto);
        MockMultipartFile jsonFile = new MockMultipartFile(
            "updateEcoNewsDto", "", "application/json", json.getBytes());
        mockMvc.perform(multipart(HttpMethod.PUT, ecoNewsLink + "/{ecoNewsId}", ecoNewsId)
            .file(jsonFile)
            .principal(principal)
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        verify(ecoNewsService, never()).update(any(UpdateEcoNewsDto.class), isNull(), eq(userClaims));
    }

    @Test
    void findRelevantNewsTest() throws Exception {
        UserVO userVO = getUserVO();
        Pageable pageable = PageRequest.of(0, 20);
        String title = "Ecology";
        String author = "John Doe";

        when(userService.findNotDeactivatedByEmail(anyString()))
            .thenReturn(userVO);

        mockMvc.perform(get(ecoNewsLink + "/relevant")
            .param("page", String.valueOf(pageable.getPageNumber()))
            .param("size", String.valueOf(pageable.getPageSize()))
            .param("tags", "tags")
            .param("title", title)
            .param("author-name", author)
            .principal(principal)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(ecoNewsRelevanceService).findRelevantEcoNews(
            eq(pageable), anyList(), eq(title), eq(author), eq(userVO));
    }
}
