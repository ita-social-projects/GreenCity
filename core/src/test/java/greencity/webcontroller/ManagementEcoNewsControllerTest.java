package greencity.webcontroller;

import com.google.gson.Gson;
import greencity.converters.UserArgumentResolver;
import greencity.dto.PageableDto;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.econews.EcoNewsDtoManagement;
import greencity.dto.user.UserVO;
import greencity.service.EcoNewsService;
import greencity.service.UserService;
import java.security.Principal;
import java.util.Arrays;
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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static greencity.ModelUtils.getPrincipal;
import static greencity.ModelUtils.getUserVO;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ManagementEcoNewsControllerTest {
    private static final String managementEcoNewsLink = "/management/eco-news";
    @Mock
    EcoNewsService ecoNewsService;
    @InjectMocks
    ManagementEcoNewsController managementEcoNewsController;
    private MockMvc mockMvc;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private UserService userService;

    private Principal principal = getPrincipal();

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(managementEcoNewsController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                new UserArgumentResolver(userService, modelMapper))
            .build();
    }

    @Test
    void getAllEcoNews() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        List<EcoNewsDto> ecoNewsDtos = Collections.singletonList(new EcoNewsDto());
        PageableDto<EcoNewsDto> ecoNewsDtoPageableDto = new PageableDto<>(ecoNewsDtos, 2, 0, 3);
        when(ecoNewsService.findAll(pageable)).thenReturn(ecoNewsDtoPageableDto);

        this.mockMvc.perform(get(managementEcoNewsLink)
            .param("page", "0")
            .param("size", "10"))
            .andExpect(view().name("core/management_eco_news"))
            .andExpect(model().attribute("pageable", ecoNewsDtoPageableDto))
            .andExpect(status().isOk());

        verify(ecoNewsService).findAll(pageable);
    }

    @Test
    void delete() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        this.mockMvc.perform(MockMvcRequestBuilders
            .delete(managementEcoNewsLink + "/delete?id=1")
            .principal(principal))
            .andExpect(status().isOk());

        verify(ecoNewsService, times(1)).delete(1L, userVO);
    }

    @Test
    void deleteAll() throws Exception {
        List<Long> longList = Arrays.asList(1L, 2L);
        Gson gson = new Gson();
        String json = gson.toJson(longList);

        this.mockMvc.perform(MockMvcRequestBuilders.delete(managementEcoNewsLink + "/deleteAll")
            .content(json)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(ecoNewsService).deleteAll(longList);
    }

    @Test
    void getEcoNewsById() throws Exception {
        this.mockMvc.perform(get(managementEcoNewsLink + "/find?id=1"))
            .andExpect(status().isOk());

        verify(ecoNewsService).findDtoById(1L);
    }

    @Test
    void saveEcoNews() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        AddEcoNewsDtoRequest addEcoNewsDtoRequest = new AddEcoNewsDtoRequest();
        addEcoNewsDtoRequest.setText("TextTextTextTextTextText");
        addEcoNewsDtoRequest.setTitle("Title");
        addEcoNewsDtoRequest.setTags(Collections.singletonList("News"));
        Gson gson = new Gson();
        String json = gson.toJson(addEcoNewsDtoRequest);
        MockMultipartFile jsonFile =
            new MockMultipartFile("addEcoNewsDtoRequest", "", "application/json", json.getBytes());

        this.mockMvc.perform(multipart(managementEcoNewsLink + "/")
            .file(jsonFile)
            .principal(principal)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(ecoNewsService, never()).save(addEcoNewsDtoRequest, jsonFile, principal.getName());
    }

    @Test
    void update() throws Exception {
        EcoNewsDtoManagement ecoNewsDtoManagement = new EcoNewsDtoManagement();
        ecoNewsDtoManagement.setId(1L);
        ecoNewsDtoManagement.setTags(Collections.singletonList("News"));
        Gson gson = new Gson();
        String json = gson.toJson(ecoNewsDtoManagement);
        MockMultipartFile jsonFile =
            new MockMultipartFile("ecoNewsDtoManagement", "", "application/json", json.getBytes());

        this.mockMvc.perform(multipart(managementEcoNewsLink + "/")
            .file(jsonFile)
            .with(new RequestPostProcessor() {
                @Override
                public MockHttpServletRequest postProcessRequest(MockHttpServletRequest mockHttpServletRequest) {
                    mockHttpServletRequest.setMethod("PUT");
                    return mockHttpServletRequest;
                }
            })).andExpect(status().isOk());

        verify(ecoNewsService, never()).update(ecoNewsDtoManagement, jsonFile);
    }
}
