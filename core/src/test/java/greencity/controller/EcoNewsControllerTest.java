package greencity.controller;

import greencity.dto.PageableDto;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.econews.EcoNewsDto;
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
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
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
        when(ecoNewsService.getThreeLastEcoNews()).thenReturn(Collections.singletonList(new EcoNewsDto()));
        mockMvc.perform(get(ecoNewsLink + "/newest"))
                .andExpect(status().isOk());
    }

    @Test
    public void saveTest() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("");
        String json = "{\n" +
                "\"title\": \"title\",\n" +
                " \"tags\": [\"news\"],\n" +
                " \"text\": \"content content content\", \n" +
                "\"source\": \"\",\n" +
                " \"image\": null\n" +
                "}";
        MockMultipartFile jsonFile = new MockMultipartFile("addEcoNewsDtoRequest", "", "application/json", json.getBytes());

        when(ecoNewsService.save(any(AddEcoNewsDtoRequest.class), any(MultipartFile.class), anyString()))
                .thenReturn(new AddEcoNewsDtoResponse());

        this.mockMvc.perform(multipart(ecoNewsLink)
                .file(jsonFile)
                .principal(principal)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(ecoNewsService, times(1))
                .save(any(AddEcoNewsDtoRequest.class), any(), anyString());
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
        when(ecoNewsService.findDtoById(anyLong()))
                .thenReturn(new EcoNewsDto());

        mockMvc.perform(get(ecoNewsLink + "/{id}", 1))
                .andExpect(status().isOk());
    }

    @Test
    public void findAllTest() throws Exception {
        int pageNumber = 0;
        int pageSize = 1;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<EcoNewsDto> ecoNewsDTOS = Collections.singletonList(new EcoNewsDto());
        PageableDto<EcoNewsDto> pageableDto = new PageableDto<>(ecoNewsDTOS, ecoNewsDTOS.size(), 0);

        when(ecoNewsService.findAll(pageable))
                .thenReturn(pageableDto);

        mockMvc.perform(get(ecoNewsLink))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteTest() throws Exception {
        mockMvc.perform(delete(ecoNewsLink + "/{econewsId}", 1)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(ecoNewsService, times(1)).delete(eq(1L));
    }

    @Test
    public void getEcoNewsTest() throws Exception {
        int pageNumber = 5;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<EcoNewsDto> ecoNewsDTOS = Collections.singletonList(new EcoNewsDto());
        PageableDto<EcoNewsDto> pageableDto = new PageableDto<>(ecoNewsDTOS, ecoNewsDTOS.size(), 0);

        when(ecoNewsService
                .find(pageable, Collections.singletonList("eco")))
                .thenReturn(pageableDto);

        mockMvc.perform(get("/econews/tags?page=5&tags=eco"))
                .andExpect(status().isOk());
    }

    @Test
    public void getThreeRecommendedEcoNewsTest() throws Exception {
        when(ecoNewsService.getThreeRecommendedEcoNews(anyLong()))
                .thenReturn(Collections.singletonList(new EcoNewsDto()));
        mockMvc.perform(get(ecoNewsLink + "/recommended?openedEcoNewsId=" + 1L))
                .andExpect(status().isOk());
    }

    @Test
    public void findAllEcoNewsTagsTest() throws Exception {
        when(tagsService.findAllEcoNewsTags()).thenReturn(Collections.singletonList("tagName"));
        mockMvc.perform(get(ecoNewsLink + "/tags/all"))
                .andExpect(status().isOk());
    }
}
