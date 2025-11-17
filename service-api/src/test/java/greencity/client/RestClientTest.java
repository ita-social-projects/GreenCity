package greencity.client;

import static greencity.TestConst.ACCESS_TOKEN;
import static greencity.TestConst.GREEN_CITY_USER_ADDRESS;
import static greencity.TestConst.GREEN_CITY_UBS_ADDRESS;
import static greencity.TestConst.SYSTEM_EMAIL;
import static greencity.TestConst.TOKEN;
import static greencity.constant.AppConstant.AUTHORIZATION;
import greencity.dto.econews.InterestingEcoNewsDto;
import greencity.dto.notification.UbsNotificationDto;
import greencity.dto.place.UpdatePlaceStatusWithUserEmailDto;
import greencity.dto.user.UserManagementCreateDto;
import greencity.dto.user.UserVO;
import greencity.dto.user.UserManagementDto;
import greencity.dto.user.UserManagementUpdateDto;
import greencity.dto.user.UserRoleDto;
import greencity.dto.user.UserManagementVO;
import greencity.ModelUtils;
import greencity.constant.RestTemplateLinks;
import greencity.dto.PageableAdvancedDto;
import greencity.enums.EmailNotification;
import greencity.enums.PlaceStatus;
import greencity.enums.Role;
import greencity.message.ScheduledEmailMessage;
import greencity.message.SendHabitNotification;
import greencity.message.SendReportEmailMessage;
import greencity.properties.RemoteWebClientProperties;
import greencity.service.UserService;
import java.security.Principal;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import greencity.security.jwt.JwtTool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RestClientTest {
    @Mock
    private RestTemplate restTemplate;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private Object object;

    @Mock
    private RequestAttributes requestAttributes;

    @Mock
    private JwtTool jwtTool;

    @Mock
    private UserService userService;

    @Mock
    private RemoteWebClientProperties remoteWebClientProperties;

    private RestClient restClient;

    private static final String USER_EMAIL = "email";

    @BeforeEach
    void init() {
        restClient = new RestClient(restTemplate, userService,
            httpServletRequest, jwtTool, remoteWebClientProperties);
        RequestContextHolder.setRequestAttributes(requestAttributes);
        when(remoteWebClientProperties.getGreencityUbsServerAddress()).thenReturn(GREEN_CITY_UBS_ADDRESS);
        when(remoteWebClientProperties.getGreencityUserServerAddress()).thenReturn(GREEN_CITY_USER_ADDRESS);
        when(remoteWebClientProperties.getSystemEmailAddress()).thenReturn(SYSTEM_EMAIL);
    }

    @Test
    void findAllNotificationsForUserFromUbsTest() {
        Principal principal = Mockito.mock(Principal.class);
        Pageable pageable = Mockito.mock(Pageable.class);
        int pageNumber = 0;
        int pageSize = 10;
        String expectedUrl =
            GREEN_CITY_UBS_ADDRESS + RestTemplateLinks.NOTIFICATIONS + "?page=" + pageNumber + "&size=" + pageSize
                + "&email=" + USER_EMAIL;
        PageableAdvancedDto<UbsNotificationDto> expectedResult = Mockito.mock(PageableAdvancedDto.class);

        when(principal.getName())
            .thenReturn(USER_EMAIL);
        when(pageable.getPageNumber())
            .thenReturn(pageNumber);
        when(pageable.getPageSize())
            .thenReturn(pageSize);
        when(restTemplate.exchange(
            eq(expectedUrl),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class))).thenReturn(ResponseEntity.ok(expectedResult));

        PageableAdvancedDto<UbsNotificationDto> actualResult = restClient.findAllNotificationsForUserFromUbs(
            principal,
            pageable);

        verify(restTemplate).exchange(
            eq(expectedUrl),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void findAllNotificationsForUserFromUbsTestWhenRestTemplateThrowsException() {
        Principal principal = Mockito.mock(Principal.class);
        Pageable pageable = Mockito.mock(Pageable.class);
        int pageNumber = 0;
        int pageSize = 10;
        String expectedUrl =
            GREEN_CITY_UBS_ADDRESS + RestTemplateLinks.NOTIFICATIONS + "?page=" + pageNumber + "&size=" + pageSize
                + "&email=" + USER_EMAIL;
        String exceptionMessage = "exceptionMessage";

        when(principal.getName())
            .thenReturn(USER_EMAIL);
        when(pageable.getPageNumber())
            .thenReturn(pageNumber);
        when(pageable.getPageSize())
            .thenReturn(pageSize);
        when(restTemplate.exchange(
            eq(expectedUrl),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class))).thenThrow(new RestClientException(exceptionMessage));

        PageableAdvancedDto<UbsNotificationDto> actualResult = restClient.findAllNotificationsForUserFromUbs(
            principal,
            pageable);

        verify(restTemplate).exchange(
            eq(expectedUrl),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class));
        assertEquals(Collections.emptyList(), actualResult.getPage());
        assertEquals(0, actualResult.getTotalElements());
        assertEquals(0, actualResult.getCurrentPage());
        assertEquals(0, actualResult.getTotalPages());
        assertEquals(0, actualResult.getNumber());
        assertFalse(actualResult.isHasPrevious());
        assertFalse(actualResult.isHasNext());
        assertFalse(actualResult.isFirst());
        assertFalse(actualResult.isLast());
    }

    @Test
    void findByEmail() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, ACCESS_TOKEN);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        UserVO userVO = ModelUtils.getUserVO();

        when(jwtTool.createAccessToken(anyString(), any(Role.class))).thenReturn(TOKEN);
        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS + RestTemplateLinks.USER_FIND_BY_EMAIL
            + RestTemplateLinks.EMAIL + "taras@gmail.com", HttpMethod.GET,
            entity, UserVO.class)).thenReturn(ResponseEntity.ok(userVO));

        assertEquals(userVO, restClient.findByEmail("taras@gmail.com"));
    }

    @Test
    void findById() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, ACCESS_TOKEN);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        UserVO userVO = ModelUtils.getUserVO();

        when(userService.findById(1L)).thenReturn(userVO);
        when(jwtTool.createAccessToken(anyString(), any(Role.class))).thenReturn(TOKEN);
        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.USER_FIND_BY_EMAIL + RestTemplateLinks.EMAIL + userVO.getEmail(), HttpMethod.GET,
            entity, UserVO.class))
            .thenReturn(ResponseEntity.ok(userVO));

        assertEquals(userVO, restClient.findById(1L));
    }

    @Test
    void updateUser() {
        UserManagementDto userManagementDto = new UserManagementDto();
        UserManagementUpdateDto userManagementUpdateDto = new UserManagementUpdateDto();
        userManagementDto.setId(1L);
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, ACCESS_TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserManagementUpdateDto> entity = new HttpEntity<>(userManagementUpdateDto, headers);

        when(jwtTool.createAccessToken(anyString(), any(Role.class))).thenReturn(TOKEN);
        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.USER, HttpMethod.PUT, entity, Object.class))
            .thenReturn(ResponseEntity.ok(object));

        restClient.updateUser(userManagementDto);

        assertEquals(ResponseEntity.ok(object), restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.USER, HttpMethod.PUT, entity, Object.class));
    }

    @Test
    void updateRole() {
        Role newRole = Role.ROLE_MODERATOR;
        UserRoleDto userRoleDto = new UserRoleDto(newRole);
        UserVO userVO = ModelUtils.getUserVO();
        String url = GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.USER + "/role" + RestTemplateLinks.EMAIL + userVO.getEmail();
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, ACCESS_TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserRoleDto> entity = new HttpEntity<>(userRoleDto, headers);

        when(userService.findById(1L)).thenReturn(userVO);
        when(jwtTool.createAccessToken(anyString(), any(Role.class))).thenReturn(TOKEN);

        restClient.updateRole(1L, newRole);

        verify(restTemplate).exchange(url, HttpMethod.PATCH, entity, Object.class);
    }

    @Test
    void findAll() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, ACCESS_TOKEN);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        UserVO userVO = ModelUtils.getUserVO();
        UserVO[] userVOS = new UserVO[] {userVO};

        when(jwtTool.createAccessToken(anyString(), any(Role.class))).thenReturn(TOKEN);
        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.USER_FIND_ALL, HttpMethod.GET, entity, UserVO[].class))
            .thenReturn(ResponseEntity.of(Optional.of(userVOS)));

        assertEquals(Arrays.asList(userVOS), restClient.findAll());
    }

    @Test
    void findUserFriendsByUserId() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, ACCESS_TOKEN);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        UserManagementVO userManagementDto = new UserManagementVO();
        UserManagementVO[] userManagementDtos = new UserManagementVO[] {userManagementDto};
        UserVO userVO = ModelUtils.getUserVO();

        when(userService.findById(1L)).thenReturn(userVO);
        when(jwtTool.createAccessToken(anyString(), any(Role.class))).thenReturn(TOKEN);
        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.USER + RestTemplateLinks.FRIENDS + RestTemplateLinks.EMAIL + userVO.getEmail(),
            HttpMethod.GET, entity, UserManagementVO[].class))
            .thenReturn(ResponseEntity.ok(userManagementDtos));
        restClient.findUserFriendsByUserId(1L);

        verify(restTemplate).exchange(GREEN_CITY_USER_ADDRESS + RestTemplateLinks.USER + RestTemplateLinks.FRIENDS
            + RestTemplateLinks.EMAIL + userVO.getEmail(), HttpMethod.GET, entity, UserManagementVO[].class);
    }

    @Test
    void getUserLang() {
        String test = "test";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, ACCESS_TOKEN);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        UserVO userVO = ModelUtils.getUserVO();

        when(userService.findById(1L)).thenReturn(userVO);
        when(jwtTool.createAccessToken(anyString(), any(Role.class))).thenReturn(TOKEN);
        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS + RestTemplateLinks.USER_LANG
            + RestTemplateLinks.EMAIL + userVO.getEmail(), HttpMethod.GET, entity, String.class))
            .thenReturn(ResponseEntity.ok(test));

        assertEquals(test, restClient.getUserLang(1L));
    }

    @Test
    void managementRegisterUser() {
        UserManagementCreateDto userManagementDto = new UserManagementCreateDto();
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, ACCESS_TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserManagementCreateDto> entity = new HttpEntity<>(userManagementDto, headers);

        when(jwtTool.createAccessToken(anyString(), any(Role.class))).thenReturn(TOKEN);
        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.OWN_SECURITY_REGISTER, HttpMethod.POST, entity, Object.class))
            .thenReturn(ResponseEntity.ok(object));

        restClient.managementRegisterUser(userManagementDto);

        verify(restTemplate).exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.OWN_SECURITY_REGISTER, HttpMethod.POST, entity, Object.class);
    }

    @Test
    void sendInterestingEcoNews() {
        InterestingEcoNewsDto message = ModelUtils.getInterestingEcoNewsDto();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set(AUTHORIZATION, ACCESS_TOKEN);
        HttpEntity<InterestingEcoNewsDto> entity = new HttpEntity<>(message, httpHeaders);

        when(jwtTool.createAccessToken(anyString(), any(Role.class))).thenReturn(TOKEN);
        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.SEND_INTERESTING_ECO_NEWS, HttpMethod.POST, entity, Object.class))
            .thenReturn(ResponseEntity.ok(object));

        restClient.sendInterestingEcoNews(message);

        verify(restTemplate).exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.SEND_INTERESTING_ECO_NEWS, HttpMethod.POST, entity, Object.class);
    }

    @Test
    void sendReport() {
        SendReportEmailMessage message = ModelUtils.getSendReportEmailMessage();
        HttpEntity<SendReportEmailMessage> entity = new HttpEntity<>(message, ModelUtils.getHeaders());
        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.SEND_REPORT, HttpMethod.POST, entity, Object.class))
            .thenReturn(ResponseEntity.ok(object));
        when(jwtTool.createAccessToken(SYSTEM_EMAIL, Role.ROLE_ADMIN)).thenReturn("accessToken");
        restClient.sendReport(message);

        verify(jwtTool).createAccessToken(SYSTEM_EMAIL, Role.ROLE_ADMIN);
        verify(restTemplate).exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.SEND_REPORT, HttpMethod.POST, entity, Object.class);
    }

    @Test
    void sendHabitNotification() {
        SendHabitNotification notification = ModelUtils.getSendHabitNotification();
        HttpEntity<SendHabitNotification> entity = new HttpEntity<>(notification, ModelUtils.getHeaders());

        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.SEND_HABIT_NOTIFICATION, HttpMethod.POST, entity, Object.class))
            .thenReturn(ResponseEntity.ok(object));
        when(jwtTool.createAccessToken(SYSTEM_EMAIL, Role.ROLE_ADMIN)).thenReturn("accessToken");

        restClient.sendHabitNotification(notification);

        verify(jwtTool).createAccessToken(SYSTEM_EMAIL, Role.ROLE_ADMIN);
        verify(restTemplate).exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.SEND_HABIT_NOTIFICATION, HttpMethod.POST, entity, Object.class);
    }

    @Test
    void findUserForManagement() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, ACCESS_TOKEN);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        UserVO userVO = ModelUtils.getUserVO();
        UserManagementVO user = new UserManagementVO();

        when(jwtTool.createAccessToken(anyString(), any(Role.class))).thenReturn(TOKEN);
        when(userService.findById(userVO.getId())).thenReturn(userVO);
        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.USER_FIND_USER_FOR_MANAGEMENT + RestTemplateLinks.EMAIL + userVO.getEmail(),
            HttpMethod.GET, entity, new ParameterizedTypeReference<UserManagementVO>() {
            }))
            .thenReturn(ResponseEntity.ok(user));

        assertEquals(user, restClient.findUserForManagement(userVO.getId()));
    }

    @Test
    void findAllByEmailNotification() {
        HttpEntity<String> entity = new HttpEntity<>(new HttpHeaders());
        List<UserVO> userVOS = Collections.singletonList(ModelUtils.getUserVO());

        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.USER_FIND_ALL_BY_EMAIL_NOTIFICATION
            + RestTemplateLinks.EMAIL_NOTIFICATION + EmailNotification.IMMEDIATELY,
            HttpMethod.GET, entity, new ParameterizedTypeReference<List<UserVO>>() {
            }))
            .thenReturn(ResponseEntity.status(HttpStatus.OK).body(userVOS));

        assertEquals(userVOS, restClient.findAllByEmailNotification(EmailNotification.IMMEDIATELY));
    }

    @Test
    void findAllRegistrationMonthsMap() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, ACCESS_TOKEN);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        Map<Integer, Long> expected = Collections.singletonMap(1, 1L);

        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.FIND_ALL_REGISTRATION_MONTHS_MAP,
            HttpMethod.GET, entity, new ParameterizedTypeReference<Map<Integer, Long>>() {
            })).thenReturn(ResponseEntity.status(HttpStatus.OK).body(expected));
        when(jwtTool.createAccessToken(anyString(), any(Role.class))).thenReturn(TOKEN);

        assertEquals(expected, restClient.findAllRegistrationMonthsMap());
    }

    @Test
    void findAllUsersCities() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, ACCESS_TOKEN);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        List<String> expected = Collections.singletonList("text");

        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS + RestTemplateLinks.FIND_ALL_USERS_CITIES,
            HttpMethod.GET, entity, new ParameterizedTypeReference<List<String>>() {
            }))
            .thenReturn(ResponseEntity.status(HttpStatus.OK).body(expected));
        when(jwtTool.createAccessToken(anyString(), any(Role.class))).thenReturn(TOKEN);

        assertEquals(expected, restClient.findAllUsersCities());
    }

    @Test
    void sendScheduledNotificationTest() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, ACCESS_TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);
        ScheduledEmailMessage message = ModelUtils.getScheduledEmailMessage();

        HttpEntity<ScheduledEmailMessage> entity = new HttpEntity<>(message, headers);

        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.SEND_SCHEDULED_NOTIFICATION, HttpMethod.POST, entity, Object.class))
            .thenReturn(ResponseEntity.ok(object));
        when(jwtTool.createAccessToken(anyString(), any(Role.class))).thenReturn(TOKEN);

        restClient.sendScheduledEmailNotification(message);

        verify(restTemplate).exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.SEND_SCHEDULED_NOTIFICATION, HttpMethod.POST, entity, Object.class);
    }

    @Test
    void sendEmailNotificationSystemTest() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, ACCESS_TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);
        ScheduledEmailMessage message = ModelUtils.getScheduledEmailMessage();

        HttpEntity<ScheduledEmailMessage> entity = new HttpEntity<>(message, headers);

        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.SEND_SCHEDULED_NOTIFICATION, HttpMethod.POST, entity, Object.class))
            .thenReturn(ResponseEntity.ok(object));
        when(jwtTool.createAccessToken(anyString(), any(Role.class))).thenReturn(TOKEN);

        restClient.sendEmailNotificationSystem(message);

        verify(restTemplate).exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.SEND_SCHEDULED_NOTIFICATION, HttpMethod.POST, entity, Object.class);
    }

    @Test
    void sendEmailNotificationLikesTest() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, ACCESS_TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);
        ScheduledEmailMessage message = ModelUtils.getScheduledEmailMessage();

        HttpEntity<ScheduledEmailMessage> entity = new HttpEntity<>(message, headers);

        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.SEND_SCHEDULED_NOTIFICATION, HttpMethod.POST, entity, Object.class))
            .thenReturn(ResponseEntity.ok(object));
        when(jwtTool.createAccessToken(anyString(), any(Role.class))).thenReturn(TOKEN);

        restClient.sendEmailNotificationLikes(message);

        verify(restTemplate).exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.SEND_SCHEDULED_NOTIFICATION, HttpMethod.POST, entity, Object.class);
    }

    @Test
    void sendEmailNotificationCommentsTest() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, ACCESS_TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);
        ScheduledEmailMessage message = ModelUtils.getScheduledEmailMessage();

        HttpEntity<ScheduledEmailMessage> entity = new HttpEntity<>(message, headers);

        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.SEND_SCHEDULED_NOTIFICATION, HttpMethod.POST, entity, Object.class))
            .thenReturn(ResponseEntity.ok(object));
        when(jwtTool.createAccessToken(anyString(), any(Role.class))).thenReturn(TOKEN);

        restClient.sendEmailNotificationComments(message);

        verify(restTemplate).exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.SEND_SCHEDULED_NOTIFICATION, HttpMethod.POST, entity, Object.class);
    }

    @Test
    void sendEmailNotificationInvitesTest() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, ACCESS_TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);
        ScheduledEmailMessage message = ModelUtils.getScheduledEmailMessage();

        HttpEntity<ScheduledEmailMessage> entity = new HttpEntity<>(message, headers);

        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.SEND_SCHEDULED_NOTIFICATION, HttpMethod.POST, entity, Object.class))
            .thenReturn(ResponseEntity.ok(object));
        when(jwtTool.createAccessToken(anyString(), any(Role.class))).thenReturn(TOKEN);

        restClient.sendEmailNotificationInvites(message);

        verify(restTemplate).exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.SEND_SCHEDULED_NOTIFICATION, HttpMethod.POST, entity, Object.class);
    }

    @Test
    void sendEmailNotificationPlacesTest() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, ACCESS_TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);
        ScheduledEmailMessage message = ModelUtils.getScheduledEmailMessage();

        HttpEntity<ScheduledEmailMessage> entity = new HttpEntity<>(message, headers);

        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.SEND_SCHEDULED_NOTIFICATION, HttpMethod.POST, entity, Object.class))
            .thenReturn(ResponseEntity.ok(object));
        when(jwtTool.createAccessToken(anyString(), any(Role.class))).thenReturn(TOKEN);

        restClient.sendEmailNotificationPlaces(message);

        verify(restTemplate).exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.SEND_SCHEDULED_NOTIFICATION, HttpMethod.POST, entity, Object.class);
    }

    @Test
    void sendEmailNotificationChangesPlaceStatusTest() {
        UpdatePlaceStatusWithUserEmailDto message = new UpdatePlaceStatusWithUserEmailDto();
        message.setPlaceName("TestPlace");
        message.setNewStatus(PlaceStatus.APPROVED);
        message.setUserName("TestUser");
        message.setEmail("test@example.com");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String expectedUrl = GREEN_CITY_USER_ADDRESS + RestTemplateLinks.SEND_NOTIFICATION_STATUS_PLACE;
        when(restTemplate.exchange(eq(expectedUrl), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
            .thenReturn(ResponseEntity.ok().build());
        restClient.sendEmailNotificationChangesPlaceStatus(message);
        verify(restTemplate, times(1)).exchange(eq(expectedUrl), eq(HttpMethod.POST), any(HttpEntity.class),
            eq(Object.class));
    }
}
