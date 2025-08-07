package greencity.controller;

import greencity.ModelUtils;
import greencity.client.UserRemoteClient;
import greencity.converters.UserArgumentResolver;
import greencity.dto.user.UserVO;
import greencity.service.AIService;
import greencity.service.UserService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Import;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.security.Principal;
import static greencity.ModelUtils.getPrincipal;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@Import(AIController.class)
class AIControllerTest {
    @Mock
    private AIService aiService;
    @Mock
    private UserService userService;
    @Mock
    private UserRemoteClient userRemoteClient;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private AIController aiController;

    private MockMvc mockMvc;
    private Principal principal = getPrincipal();
    private UserVO userVO = ModelUtils.getUserVO();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(aiController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                new UserArgumentResolver(userService, modelMapper))
            .build();
    }

    @Test
    void forecastReturnsForecastFromAIServiceTest() throws Exception {
        when(userService.findNotDeactivatedByEmail(principal.getName())).thenReturn(userVO);
        userVO.getLanguageVO().setCode("англійська");

        mockMvc.perform(get("/ai/forecast")
            .principal(principal))
            .andExpect(status().isOk());

        verify(aiService, times(1)).getForecast(userVO.getId(), userVO.getLanguageVO().getCode());
    }

    @Test
    void creatingEcoNewsReturnsEcoNewsFromAIServiceTest() throws Exception {
        when(userService.findNotDeactivatedByEmail(principal.getName())).thenReturn(userVO);
        userVO.getLanguageVO().setCode("English");

        mockMvc.perform(get("/ai/generate/eco-news")
            .principal(principal))
            .andExpect(status().isOk());

        verify(aiService, times(1)).getNews(userVO.getLanguageVO().getCode(), null);
    }
}