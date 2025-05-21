package greencity.controller;

import greencity.TestConst;
import greencity.converters.UserIdArgumentResolver;
import greencity.dto.achievement.ActionDto;
import greencity.security.jwt.JwtTool;
import greencity.service.UserNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import java.security.Principal;

import static greencity.ModelUtils.getActionDto;
import static greencity.ModelUtils.getPrincipal;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {
    private static final String notificationLink = "/notifications";
    private final Principal principal = getPrincipal();
    private MockMvc mockMvc;
    @InjectMocks
    private NotificationController notificationController;

    @Mock
    private UserNotificationService userNotificationService;

    @Mock
    private Validator mockValidator;

    @Mock
    JwtTool jwtTool;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(notificationController)
            .setCustomArgumentResolvers(
                    new PageableHandlerMethodArgumentResolver(),
                    new UserIdArgumentResolver(jwtTool))
            .setValidator(mockValidator)
            .build();
    }

    @Test
    void getNotificationsFilteredTest() throws Exception {
        var pageable = PageRequest.of(0, 20);
        String jwt = "jwt";
        when(jwtTool.extractJwtFromNativeWebRequest(any()))
                .thenReturn(jwt);
        when(jwtTool.extractUserId(jwt))
                .thenReturn(TestConst.USER_ID);

        mockMvc.perform(get(notificationLink).principal(principal))
            .andExpect(status().isOk());
        verify(userNotificationService).getNotificationsFiltered(TestConst.USER_ID, pageable, principal, "en", null, null, null);
    }

    @Test
    void viewNotificationTest() throws Exception {
        mockMvc.perform(post(notificationLink + "/{notificationId}/viewNotification", 1))
            .andExpect(status().isOk());
        verify(userNotificationService).viewNotification(1L);
    }

    @Test
    void unreadNotificationTest() throws Exception {
        mockMvc.perform(post(notificationLink + "/{notificationId}/unreadNotification", 1L))
            .andExpect(status().isOk());
        verify(userNotificationService).unreadNotification(1L);
    }

    @Test
    void deleteNotificationTest() throws Exception {
        String jwt = "jwt";
        when(jwtTool.extractJwtFromNativeWebRequest(any()))
                .thenReturn(jwt);
        when(jwtTool.extractUserId(jwt))
                .thenReturn(TestConst.USER_ID);

        mockMvc.perform(delete(notificationLink + "/{notificationId}", 1L)
            .principal(principal)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(userNotificationService).deleteNotification(TestConst.USER_ID, 1L);
    }

    @Test
    void notificationSocketTest() {
        ActionDto user = getActionDto();
        notificationController.notificationSocket(user);
        verify(userNotificationService).notificationSocket(user);
    }
}
