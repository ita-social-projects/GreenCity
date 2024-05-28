package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.service.UserNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static greencity.ModelUtils.getActionDto;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import org.springframework.validation.Validator;
import java.security.Principal;
import static greencity.ModelUtils.getFilterNotificationDto;
import static greencity.ModelUtils.getPrincipal;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class NotificationControllerTest {
    private static final String notificationLink = "/notification";
    private MockMvc mockMvc;
    private final Principal principal = getPrincipal();

    @InjectMocks
    private NotificationController notificationController;

    @Mock
    private UserNotificationService userNotificationService;

    @Mock
    private Validator mockValidator;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(notificationController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setValidator(mockValidator)
            .build();
    }

    @Test
    void getThreeLastNotificationsTest() throws Exception {
        mockMvc.perform(get(notificationLink + "/new").principal(principal)).andExpect(status().isOk());
        verify(userNotificationService).getThreeLastNotifications(principal, "en");
    }

    @Test
    void getEventFilteredTest() throws Exception {
        var pageable = PageRequest.of(0, 20);
        var dto = getFilterNotificationDto();
        mockMvc.perform(get(notificationLink + "/all").principal(principal)).andExpect(status().isOk());
        verify(userNotificationService).getNotificationsFiltered(pageable, principal, dto, "en");
    }

    @Test
    void viewNotificationTest() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        var dto = getFilterNotificationDto();
        String content = objectMapper.writeValueAsString(dto);
        mockMvc.perform(patch(notificationLink + "/view/" + 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isOk());
        verify(userNotificationService).viewNotification(1L);
    }

    @Test
    void unreadNotificationTest() throws Exception {
        mockMvc.perform(patch(notificationLink + "/unread/" + 1L)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(userNotificationService).unreadNotification(1L);
    }

    @Test
    void deleteNotificationTest() throws Exception {
        mockMvc.perform(delete(notificationLink + "/" + 1L)
            .principal(principal)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(userNotificationService).deleteNotification(principal, 1L);
    }

    @Test
    void notificationSocketTest() {
        var user = getActionDto();
        notificationController.notificationSocket(user);
        verify(userNotificationService).notificationSocket(user);
    }
}
