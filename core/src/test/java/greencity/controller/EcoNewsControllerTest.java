package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.client.RestClient;
import greencity.converters.UserArgumentResolver;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.user.UserVO;
import greencity.service.EcoNewsService;
import greencity.service.TagsService;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static greencity.ModelUtils.getPrincipal;
import static greencity.ModelUtils.getUserVO;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EcoNewsControllerTest {
    private static final String ecoNewsLink = "/econews";
    private MockMvc mockMvc;
    @InjectMocks
    private EcoNewsController ecoNewsController;
    @Mock
    private EcoNewsService ecoNewsService;
    @Mock
    private TagsService tagsService;
    @Mock
    private RestClient restClient;
    @Mock
    private ModelMapper modelMapper;

    private Principal principal = getPrincipal();

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders
            .standaloneSetup(ecoNewsController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                new UserArgumentResolver(restClient, modelMapper))
            .build();
    }

    @Test
    void getThreeLastEcoNewsTest() throws Exception {
        mockMvc.perform(get(ecoNewsLink + "/newest"))
            .andExpect(status().isOk());

        verify(ecoNewsService).getThreeLastEcoNews();
    }

    @Test
    void saveTest() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("Olivia.Johnson@gmail.com");
        String json = "{\n" +
            "\"title\": \"title\",\n" +
            " \"tags\": [\"news\"],\n" +
            " \"text\": \"content content content\", \n" +
            "\"source\": \"\",\n" +
            " \"image\": null\n" +
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
            .save(eq(addEcoNewsDtoRequest), isNull(), eq("Olivia.Johnson@gmail.com"));
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
        mockMvc.perform(get(ecoNewsLink + "/{id}", 1))
            .andExpect(status().isOk());

        verify(ecoNewsService).findDtoById(1L);
    }

    @Test
    void findAllTest() throws Exception {
        int pageNumber = 1;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        mockMvc.perform(get(ecoNewsLink + "?page=1"))
            .andExpect(status().isOk());

        verify(ecoNewsService).findAll(pageable);
    }

    @Test
    void deleteTest() throws Exception {
        UserVO userVO = getUserVO();
        when(restClient.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(delete(ecoNewsLink + "/{econewsId}", 1)
            .principal(principal))
            .andExpect(status().isOk());

        verify(ecoNewsService).delete(1L, userVO);
    }

    @Test
    void getEcoNewsTest() throws Exception {
        int pageNumber = 5;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<String> tags = Collections.singletonList("eco");

        mockMvc.perform(get("/econews/tags?page=5&tags=eco"))
            .andExpect(status().isOk());

        verify(ecoNewsService).find(pageable, tags);
    }

    @Test
    void getEcoNewsEmptyTagsListTest() throws Exception {
        int pageNumber = 5;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        mockMvc.perform(get("/econews/tags?page=5"))
            .andExpect(status().isOk());

        verify(ecoNewsService).findAll(pageable);
    }

    @Test
    void getThreeRecommendedEcoNewsTest() throws Exception {
        mockMvc.perform(get(ecoNewsLink + "/recommended?openedEcoNewsId=" + 1L))
            .andExpect(status().isOk());

        verify(ecoNewsService).getThreeRecommendedEcoNews(1L);
    }

    @Test
    void findAllEcoNewsTagsTest() throws Exception {
        String language = "en";
        mockMvc.perform(get(ecoNewsLink + "/tags/all?lang=" + language))
            .andExpect(status().isOk());

        verify(tagsService).findAllEcoNewsTags(language);
    }
}
