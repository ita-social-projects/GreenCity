package greencity.webcontroller.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.GreenCityApplication;
import greencity.IntegrationTestBase;
import greencity.repository.EventRepo;
import greencity.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = GreenCityApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
@AutoConfigureMockMvc
//@TestConfiguration
@Slf4j
public class ManagementEventsControllerIntegrationTest extends IntegrationTestBase {

    private static final String managementAddEventLink = "/management/events/create";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EventRepo eventRepo;

//    @BeforeEach
//    void setup() {
//        mockMvc = MockMvcBuilders
//                .webAppContextSetup(webApplicationContext)
//                .apply(springSecurity())
//                .build();
//    }

    @Mock
    private EventService eventService;
    private ObjectMapper objectMapper;


    @Value("classpath:testdata/event/validCreateEventDto.json")
    private Resource validCreateEventDto;

    @Value("classpath:testdata/event/invalidCreateEventDto.json")
    private Resource invalidCreateEventDto;

    @Test
    @WithMockUser(value = "service@greencity.ua", roles = "ADMIN")
    void test() throws Exception {
        String content = asString(validCreateEventDto);

        mockMvc.perform(post(managementAddEventLink)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

    }


    @Test
    @WithAnonymousUser
    void shouldNotSaveEventAndReturnUnauthorizedStatus() throws Exception {
        //given
        String content = asString(validCreateEventDto);

        //expect
        mockMvc.perform(post(managementAddEventLink)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isUnauthorized())
                .andDo(print());

        verify(eventService, never()).save(any(), any(), any());
    }

    @Test
    @WithMockUser(username = "service@greencity.ua", roles = "ADMIN", password = "")
    void shouldSaveEventAndReturnCreatedStatus() throws Exception {
        //given
        String content = asString(validCreateEventDto);

        //expect
        mockMvc.perform(post(managementAddEventLink)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjb2RlcmFsaWdhdG9yQGdtYWlsLmNvbSIsInJvbGUiOlsiUk9MRV9BRE1JTiJdLCJpYXQiOjE2NjkzODk0NDgsImV4cCI6MTY2OTM5NjY0OH0.M7MqSoH3s4ixX5Yd8Y86Wkq-AEDaE4uMFfHQ8OLnPtQ")
                        .with(SecurityMockMvcRequestPostProcessors.user("coderaligator@gmail.com").roles("ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content(content))
                .andExpect(status().isCreated())
                .andDo(print());

        verify(eventService, times(1)).save(any(), any(), any());
    }

    public static String asString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
