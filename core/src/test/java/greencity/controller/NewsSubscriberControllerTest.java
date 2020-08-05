package greencity.controller;

import greencity.dto.newssubscriber.NewsSubscriberRequestDto;
import greencity.dto.newssubscriber.NewsSubscriberResponseDto;
import greencity.service.NewsSubscriberService;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class NewsSubscriberControllerTest {
    private static final String newsSubscriberControllerLink = "/newsSubscriber";
    private MockMvc mockMvc;

    @InjectMocks
    private NewsSubscriberController newsSubscriberController;

    @Mock
    private NewsSubscriberService newsSubscriberService;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(newsSubscriberController)
            .build();
    }

    @Test
    void getAll() throws Exception {
        when(newsSubscriberService.findAll())
            .thenReturn(Collections.singletonList(new NewsSubscriberResponseDto()));

        mockMvc.perform(get(newsSubscriberControllerLink)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void saveBadRequest() throws Exception {
        mockMvc.perform(post(newsSubscriberControllerLink)
            .content("{}")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void saveResponseOk() throws Exception {
        when(newsSubscriberService.save(any(NewsSubscriberRequestDto.class)))
            .thenReturn(new NewsSubscriberRequestDto());

        mockMvc.perform(post(newsSubscriberControllerLink)
            .content("{\n"
                + "  \"email\": \"datskomar@gmail.com\"\n"
                + "}")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void deleteResponseOk() throws Exception {
        when(newsSubscriberService.unsubscribe(anyString(), anyString())).thenReturn(1L);

        mockMvc.perform(get(newsSubscriberControllerLink)
            .param("datskomar@gmail.com", "55275726-de58-45f4-818d-f6dcb5403946")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
}