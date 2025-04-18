package greencity.controller;

import greencity.GreenCityApplication;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.LanguageService;
import greencity.service.UserNotificationService;
import greencity.service.UserService;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
@ContextConfiguration(classes = GreenCityApplication.class)
public class NotificationControllerAccessControlTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ModelMapper modelMapper;

    @MockBean
    private UserNotificationService notificationService;

    @MockBean
    private CustomExceptionHandler customExceptionHandler;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private UserService userService;

    @MockBean
    private LanguageService languageService;

    @Test
    void getNotificationsBySearchRequestFiltered401Test() throws Exception {
        mockMvc.perform(get("/notifications"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testUser")
    void getNotificationsBySearchRequestFiltered200Test() throws Exception {
        mockMvc.perform(get("/notifications"))
            .andExpect(status().isOk());
    }
}
