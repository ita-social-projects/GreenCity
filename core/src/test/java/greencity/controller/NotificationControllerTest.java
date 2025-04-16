package greencity.controller;

import greencity.ModelUtils;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.achievement.ActionDto;
import greencity.dto.notification.NotificationDto;
import greencity.enums.ProjectName;
import greencity.service.UserNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import java.security.Principal;
import java.util.Locale;

import static greencity.ModelUtils.getActionDto;
import static greencity.ModelUtils.getPrincipal;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.anyString;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(notificationController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setValidator(mockValidator)
            .build();
    }

    @Test
    void getNotificationsFilteredTest() throws Exception {
        var pageable = PageRequest.of(0, 20);

        mockMvc.perform(get(notificationLink).principal(principal))
            .andExpect(status().isOk());
        verify(userNotificationService).getNotificationsFiltered(pageable, principal, "en", null, null, null);
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
        mockMvc.perform(delete(notificationLink + "/{notificationId}", 1L)
            .principal(principal)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(userNotificationService).deleteNotification(principal, 1L);
    }

    @Test
    void notificationSocketTest() {
        ActionDto user = getActionDto();
        notificationController.notificationSocket(user);
        verify(userNotificationService).notificationSocket(user);
    }

    @Test
    void getNotificationsBySearchRequestFilteredOkTest() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        String language = "en";
        Locale locale = Locale.of(language);
        ProjectName projectName = ProjectName.GREENCITY;
        String searchRequest = "paid";

        PageableAdvancedDto<NotificationDto> result = ModelUtils
                .getPageableAdvanceDtoOfNotificationDtos(ModelUtils.getNotificationDtos(), pageable);

        when(userNotificationService.getAllNotificationsForUserBySearchRequest(pageable, principal, locale, projectName, searchRequest))
                .thenReturn(result);

        mockMvc.perform(get(notificationLink + "/search" + "?search-request=" + searchRequest
                        + "&locale=" + language + "&project-name=" + projectName)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(userNotificationService).getAllNotificationsForUserBySearchRequest(pageable, principal, locale, projectName, searchRequest);
        assertEquals(2, result.getTotalElements());
        assertEquals(ModelUtils.getNotificationDtos().getFirst(), result.getPage().getFirst());
    }

    @Test
    void getNotificationsBySearchRequestFiltered400Test() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        String language = "en";
        Locale locale = Locale.of(language);
        ProjectName projectName = ProjectName.GREENCITY;

        mockMvc.perform(get(notificationLink + "/search"
                        + "?locale=" + language + "&project-name=" + projectName)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        verify(userNotificationService, never()).getAllNotificationsForUserBySearchRequest(eq(pageable), eq(principal), eq(locale), eq(projectName), anyString());
    }
}
