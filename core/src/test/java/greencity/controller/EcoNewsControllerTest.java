package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.converters.UserArgumentResolver;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.user.UserVO;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.EcoNewsService;
import greencity.service.UserService;
import java.security.Principal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static greencity.ModelUtils.getPrincipal;
import static greencity.ModelUtils.getUserVO;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EcoNewsControllerTest {
    private static final String ecoNewsLink = "/eco-news";
    private MockMvc mockMvc;
    @InjectMocks
    private EcoNewsController ecoNewsController;
    @Mock
    private EcoNewsService ecoNewsService;
    @Mock
    private UserService userService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private ObjectMapper objectMapper;

    private final Principal principal = getPrincipal();
    private final ErrorAttributes errorAttributes = new DefaultErrorAttributes();

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders
            .standaloneSetup(ecoNewsController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                new UserArgumentResolver(userService, modelMapper))
            .setControllerAdvice(new CustomExceptionHandler(errorAttributes, objectMapper))
            .build();
    }

    @Test
    void saveTest() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("Olivia.Johnson@gmail.com");
        String json = "{\n" +
            "\"title\": \"title\",\n" +
            " \"tags\": [\"news\"],\n" +
            " \"text\": \"content content content\", \n" +
            "\"source\": \"\"\n" +
            "}";
        MockMultipartFile jsonFile =
            new MockMultipartFile("addEcoNewsDtoRequest", "", "application/json", json.getBytes());

        this.mockMvc.perform(multipart(ecoNewsLink)
            .file(jsonFile)
            .principal(principal)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());

        ObjectMapper mapper = new ObjectMapper();
        AddEcoNewsDtoRequest addEcoNewsDtoRequest = mapper.readValue(json, AddEcoNewsDtoRequest.class);

        verify(ecoNewsService)
            .saveEcoNews(eq(addEcoNewsDtoRequest), isNull(), eq("Olivia.Johnson@gmail.com"));
    }

    @Test
    void saveBadRequestTest() throws Exception {
        mockMvc.perform(post(ecoNewsLink)
            .content("{}")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getEcoNewsById() throws Exception {
        mockMvc.perform(get(ecoNewsLink + "/{ecoNewsId}", 1))
            .andExpect(status().isOk());

        verify(ecoNewsService).findDtoByIdAndLanguage(1L, "en");
    }

    @Test
    void findAllTest() throws Exception {
        int pageNumber = 1;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        mockMvc.perform(get(ecoNewsLink + "?page=1"))
            .andExpect(status().isOk());

        verify(ecoNewsService).find(pageable, null, null, null);
    }

    @Test
    void findAllByUserTest() throws Exception {
        int pageNumber = 1;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        mockMvc.perform(get(ecoNewsLink + "?author-id=1&page=1"))
            .andExpect(status().isOk());

        verify(ecoNewsService).find(pageable, null, null, 1L);
    }

    @Test
    void deleteTest() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

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
    void likeTest() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(post(ecoNewsLink + "/{ecoNewsId}/likes", 1)
            .principal(principal))
            .andExpect(status().isOk());

        verify(ecoNewsService).like(userVO, 1L);
    }

    @Test
    void dislikeTest() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);
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

        mockMvc.perform(get(ecoNewsLink + "/{ecoNewsId}/likes/{userId}", 1, userVO.getId())
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
    void getContentAndSourceForEcoNewsById() throws Exception {
        mockMvc.perform(get(ecoNewsLink + "/{ecoNewsId}/summary", 1L))
            .andExpect(status().isOk());

        verify(ecoNewsService).getContentAndSourceForEcoNewsById(1L);

    }

    @Test
    void getContentAndSourceForEcoNewsByIdNot_Found_Request() throws Exception {
        Mockito.when(ecoNewsService.getContentAndSourceForEcoNewsById(1L)).thenThrow(NotFoundException.class);

        mockMvc.perform(get(ecoNewsLink + "/{ecoNewsId}/summary", 1L))
            .andExpect(status().isNotFound());

        verify(ecoNewsService).getContentAndSourceForEcoNewsById(1L);
    }
}
