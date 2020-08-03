package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.service.EcoNewsService;
import greencity.service.TagsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class EcoNewsControllerTest {
    private static final String ecoNewsLink = "/econews";
    private MockMvc mockMvc;
    @InjectMocks
    private EcoNewsController ecoNewsController;
    @Mock
    private EcoNewsService ecoNewsService;
    @Mock
    private TagsService tagsService;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(ecoNewsController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    public void getThreeLastEcoNewsTest() throws Exception {
        mockMvc.perform(get(ecoNewsLink + "/newest"))
                .andExpect(status().isOk());

        verify(ecoNewsService).getThreeLastEcoNews();
    }

    @Test
    public void saveTest() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("Olivia.Johnson@gmail.com");
        String json = "{\n" +
                "\"title\": \"title\",\n" +
                " \"tags\": [\"news\"],\n" +
                " \"text\": \"content content content\", \n" +
                "\"source\": \"\",\n" +
                " \"image\": null\n" +
                "}";
        MockMultipartFile jsonFile = new MockMultipartFile("addEcoNewsDtoRequest", "", "application/json", json.getBytes());

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
    public void saveBadRequestTest() throws Exception {
        mockMvc.perform(post(ecoNewsLink)
                .content("{}")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getEcoNewsById() throws Exception {
        mockMvc.perform(get(ecoNewsLink + "/{id}", 1))
                .andExpect(status().isOk());

        verify(ecoNewsService).findDtoById(eq(1L));
    }

    @Test
    public void findAllTest() throws Exception {
        int pageNumber = 1;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        mockMvc.perform(get(ecoNewsLink + "?page=1"))
                .andExpect(status().isOk());

        verify(ecoNewsService).findAll(eq(pageable));
    }

    @Test
    public void deleteTest() throws Exception {
        mockMvc.perform(delete(ecoNewsLink + "/{econewsId}", 1)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(ecoNewsService).delete(eq(1L));
    }

    @Test
    public void getEcoNewsTest() throws Exception {
        int pageNumber = 5;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<String> tags = Collections.singletonList("eco");

        mockMvc.perform(get("/econews/tags?page=5&tags=eco"))
                .andExpect(status().isOk());

        verify(ecoNewsService).find(eq(pageable), eq(tags));
    }

    @Test
    public void getEcoNewsEmptyTagsListTest() throws Exception {
        int pageNumber = 5;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        mockMvc.perform(get("/econews/tags?page=5"))
                .andExpect(status().isOk());

        verify(ecoNewsService).findAll(eq(pageable));
    }

    @Test
    public void getThreeRecommendedEcoNewsTest() throws Exception {
        mockMvc.perform(get(ecoNewsLink + "/recommended?openedEcoNewsId=" + 1L))
                .andExpect(status().isOk());

        verify(ecoNewsService).getThreeRecommendedEcoNews(eq(1L));
    }

    @Test
    public void findAllEcoNewsTagsTest() throws Exception {
        mockMvc.perform(get(ecoNewsLink + "/tags/all"))
                .andExpect(status().isOk());

        verify(tagsService).findAllEcoNewsTags();
    }
}
