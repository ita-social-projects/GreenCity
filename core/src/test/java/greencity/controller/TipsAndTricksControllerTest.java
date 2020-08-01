package greencity.controller;

import greencity.dto.tipsandtricks.TipsAndTricksDtoRequest;
import greencity.dto.tipsandtricks.TipsAndTricksDtoResponse;
import greencity.service.TagsService;
import greencity.service.TipsAndTricksService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TipsAndTricksControllerTest {
    private static final String tipsAndTricksLink = "/tipsandtricks";
    private MockMvc mockMvc;
    @InjectMocks
    private TipsAndTricksController tipsAndTricksController;
    @Mock
    private TipsAndTricksService tipsAndTricksService;
    @Mock
    private TagsService tagService;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(tipsAndTricksController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
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
        MockMultipartFile jsonFile = new MockMultipartFile("tipsAndTricksDtoRequest", "", "application/json", json.getBytes());

        when(tipsAndTricksService.save(any(TipsAndTricksDtoRequest.class), any(MultipartFile.class), anyString()))
                .thenReturn(new TipsAndTricksDtoResponse());

        this.mockMvc.perform(multipart(tipsAndTricksLink)
                .file(jsonFile)
                .principal(principal)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(tipsAndTricksService, times(1))
                .save(any(TipsAndTricksDtoRequest.class), any(), anyString());
    }

    @Test
    public void saveBadRequestTest() throws Exception {
        this.mockMvc.perform(post(tipsAndTricksLink)
                .content("{}")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getTipsAndTricksByIdTest() throws Exception {
        when(tipsAndTricksService.findDtoById(anyLong()))
                .thenReturn(new TipsAndTricksDtoResponse());

        this.mockMvc.perform(get(tipsAndTricksLink + "/{id}", 1))
                .andExpect(status().isOk());

        verify(tipsAndTricksService, times(1))
                .findDtoById(anyLong());
    }

    @Test
    public void findAllTest() throws Exception {
        this.mockMvc.perform(get(tipsAndTricksLink))
                .andExpect(status().isOk());

        verify(tipsAndTricksService, times(1))
                .findAll(any());
    }

    @Test
    public void deleteTest() throws Exception {
        this.mockMvc.perform(delete(tipsAndTricksLink + "/{id}", 1))
                .andExpect(status().isOk());

        verify(tipsAndTricksService, times(1))
                .delete(anyLong());
    }

    @Test
    public void getTipsAndTricksTest() throws Exception {
        this.mockMvc.perform(get(tipsAndTricksLink + "/tags"))
                .andExpect(status().isOk());

        verify(tipsAndTricksService, times(1))
                .find(any(), any());
    }

    @Test
    public void findAllTipsAndTricksTagsTest() throws Exception {
        this.mockMvc.perform(get(tipsAndTricksLink + "/tags/all"))
                .andExpect(status().isOk());

        verify(tagService, times(1))
                .findAllTipsAndTricksTags();
    }
}