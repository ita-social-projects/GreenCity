package greencity.client;

import static greencity.constant.AppConstant.AUTHORIZATION;
import greencity.dto.user.UserVO;
import greencity.dto.user.UserManagementDto;
import greencity.dto.user.UserManagementUpdateDto;
import greencity.dto.user.UserRoleDto;
import greencity.dto.user.UserManagementViewDto;
import greencity.dto.user.UserManagementVO;
import com.google.gson.Gson;
import greencity.ModelUtils;
import greencity.constant.RestTemplateLinks;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.achievement.UserVOAchievement;
import greencity.dto.econews.EcoNewsForSendEmailDto;
import greencity.dto.eventcomment.EventCommentForSendEmailDto;
import greencity.enums.EmailNotification;
import greencity.enums.Role;
import greencity.message.SendChangePlaceStatusEmailMessage;
import greencity.message.SendEventCreationNotification;
import greencity.message.SendHabitNotification;
import greencity.message.SendReportEmailMessage;

import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import greencity.security.jwt.JwtTool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestClientTest {
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private java.lang.Object Object;
    private static final String GREEN_CITY_USER_ADDRESS = "https://www.greencity.com.ua";
    private static final String SYSTEM_EMAIL = "test-service-mail@greencity.ua";
    private RestClient restClient;
    @Mock
    private JwtTool jwtTool;

    @BeforeEach
    void init() {
        restClient = new RestClient(restTemplate, GREEN_CITY_USER_ADDRESS, httpServletRequest, jwtTool, SYSTEM_EMAIL);
    }

    @Test
    void findByEmail() {
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        UserVO userVO = ModelUtils.getUserVO();
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS + RestTemplateLinks.USER_FIND_BY_EMAIL
            + RestTemplateLinks.EMAIL + "taras@gmail.com", HttpMethod.GET,
            entity, UserVO.class)).thenReturn(ResponseEntity.ok(userVO));

        assertEquals(userVO, restClient.findByEmail("taras@gmail.com"));
    }

    @Test
    void findById() {
        UserVO userVO = ModelUtils.getUserVO();
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.USER_FIND_BY_ID + RestTemplateLinks.ID + 1L, HttpMethod.GET, entity, UserVO.class))
                .thenReturn(ResponseEntity.ok(userVO));
        assertEquals(userVO, restClient.findById(1L));
    }

    @Test
    void findUserForAchievement() {
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        UserVOAchievement userVOAchievement = new UserVOAchievement();
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.USER_FIND_BY_ID_FOR_ACHIEVEMENT + RestTemplateLinks.ID + 1L,
            HttpMethod.GET, entity, UserVOAchievement.class)).thenReturn(ResponseEntity.ok(userVOAchievement));
        assertEquals(userVOAchievement, restClient.findUserForAchievement(1L));
    }

    @Test
    void searchBy() {
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String query = "Search";
        Pageable pageable = PageRequest.of(0, 10);
        List<UserManagementDto> ecoNewsDtos = Collections.singletonList(new UserManagementDto());
        PageableAdvancedDto<UserManagementDto> pageableAdvancedDto =
            new PageableAdvancedDto<>(ecoNewsDtos, 2, 0, 3, 0, true, true, true, true);
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.SEARCH_BY + RestTemplateLinks.PAGE + pageable.getPageNumber()
            + RestTemplateLinks.SIZE + pageable.getPageSize()
            + RestTemplateLinks.QUERY + query, HttpMethod.GET, entity,
            new ParameterizedTypeReference<PageableAdvancedDto<UserManagementDto>>() {
            })).thenReturn(ResponseEntity.ok(pageableAdvancedDto));
        assertEquals(pageableAdvancedDto, restClient.searchBy(pageable, query));
    }

    @Test
    void updateUser() {
        // given
        UserManagementDto userManagementDto = new UserManagementDto();
        UserManagementUpdateDto userManagementUpdateDto = new UserManagementUpdateDto();
        userManagementDto.setId(1L);
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserManagementUpdateDto> entity = new HttpEntity<>(userManagementUpdateDto, headers);
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.USER + "/1", HttpMethod.PUT, entity, Object.class))
                .thenReturn(ResponseEntity.ok(Object));
        // when
        restClient.updateUser(userManagementDto);
        // then
        assertEquals(ResponseEntity.ok(Object), restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.USER + "/1", HttpMethod.PUT, entity, Object.class));
    }

    @Test
    void updateRole() {
        // given
        Role newRole = Role.ROLE_MODERATOR;
        UserRoleDto userRoleDto = new UserRoleDto(newRole);
        String url = GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.USER + "/1/role";
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserRoleDto> entity = new HttpEntity<>(userRoleDto, headers);
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);

        // when
        restClient.updateRole(1L, newRole);

        // then
        verify(restTemplate).exchange(url, HttpMethod.PATCH, entity, Object.class);
    }

    @Test
    void findAll() {
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        UserVO userVO = ModelUtils.getUserVO();
        UserVO[] userVOS = new UserVO[] {userVO};
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.USER_FIND_ALL, HttpMethod.GET, entity, UserVO[].class))
                .thenReturn(ResponseEntity.of(Optional.of(userVOS)));

        assertEquals(Arrays.asList(userVOS), restClient.findAll());
    }

    @Test
    void findUserFriendsByUserId() {
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        UserManagementDto userManagementDto = new UserManagementDto();
        UserManagementDto[] userManagementDtos = new UserManagementDto[] {userManagementDto};
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.USER + "/" + 1L + RestTemplateLinks.FRIENDS, HttpMethod.GET, entity,
            UserManagementDto[].class)).thenReturn(ResponseEntity.ok(userManagementDtos));
        restClient.findUserFriendsByUserId(1L);

        verify(restTemplate).exchange(GREEN_CITY_USER_ADDRESS + RestTemplateLinks.USER + "/"
            + 1L + RestTemplateLinks.FRIENDS, HttpMethod.GET, entity, UserManagementDto[].class);
    }

    @Test
    void findNotDeactivatedByEmail() {
        String email = "test@gmail.com";
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        UserVO userVO = ModelUtils.getUserVO();
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.USER_FIND_NOT_DEACTIVATED_BY_EMAIL + RestTemplateLinks.EMAIL
            + email, HttpMethod.GET, entity, UserVO.class)).thenReturn(ResponseEntity.ok(userVO));

        assertEquals(Optional.of(userVO), restClient.findNotDeactivatedByEmail(email));
    }

    @Test
    void findNotDeactivatedByEmailFromCookies() {
        String email = "test@gmail.com";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, "Bearer testToken");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        UserVO userVO = ModelUtils.getUserVO();
        Cookie[] cookies = new Cookie[] {new Cookie("accessToken", "testToken")};
        when(httpServletRequest.getCookies()).thenReturn(cookies);
        when(httpServletRequest.getRequestURI()).thenReturn("/management");
        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.USER_FIND_NOT_DEACTIVATED_BY_EMAIL + RestTemplateLinks.EMAIL
            + email, HttpMethod.GET, entity, UserVO.class)).thenReturn(ResponseEntity.ok(userVO));

        assertEquals(Optional.of(userVO), restClient.findNotDeactivatedByEmail(email));
    }

    @Test
    void findIdByEmail() {
        String email = "test@gmail.com";
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.USER_FIND_ID_BY_EMAIL
            + RestTemplateLinks.EMAIL + email, HttpMethod.GET, entity, Long.class))
                .thenReturn(ResponseEntity.ok(1L));

        assertEquals(1L, restClient.findIdByEmail(email));
    }

    @Test
    void getDeactivationReason() {
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        String[] test = new String[] {"test", "test"};
        List<String> listString = Arrays.asList(test);
        Gson gson = new Gson();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS + RestTemplateLinks.USER_REASONS
            + RestTemplateLinks.ID + 1L
            + RestTemplateLinks.ADMIN_LANG + "en", HttpMethod.GET, entity, String[].class))
                .thenReturn(ResponseEntity.ok(test));
        assertEquals(listString, restClient.getDeactivationReason(1L, "en"));
    }

    @Test
    void getUserLang() {
        String test = "test";
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS + RestTemplateLinks.USER_LANG
            + RestTemplateLinks.ID + 1L, HttpMethod.GET, entity, String.class))
                .thenReturn(ResponseEntity.ok(test));
        assertEquals(test, restClient.getUserLang(1L));
    }

    @Test
    void deactivateUser() {
        String accessToken = "accessToken";
        List<String> test = List.of("test", "test");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<List<String>> entity = new HttpEntity<>(test, headers);
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS + RestTemplateLinks.USER_DEACTIVATE
            + RestTemplateLinks.ID + 1L, HttpMethod.PUT, entity, Object.class))
                .thenReturn(ResponseEntity.ok(Object));
        restClient.deactivateUser(1L, test);
        verify(restTemplate).exchange(GREEN_CITY_USER_ADDRESS + RestTemplateLinks.USER_DEACTIVATE
            + RestTemplateLinks.ID + 1L, HttpMethod.PUT, entity, Object.class);
    }

    @Test
    void setActivatedStatus() {
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS + RestTemplateLinks.USER_ACTIVATE
            + RestTemplateLinks.ID + 1L, HttpMethod.PUT, entity, Object.class))
                .thenReturn(ResponseEntity.ok(Object));
        restClient.setActivatedStatus(1L);
        verify(restTemplate).exchange(GREEN_CITY_USER_ADDRESS + RestTemplateLinks.USER_ACTIVATE
            + RestTemplateLinks.ID + 1L, HttpMethod.PUT, entity, Object.class);
    }

    @Test
    void deactivateAllUsers() {
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(AUTHORIZATION, accessToken);
        Long[] longs = new Long[] {1L, 2L};
        List<Long> listId = Arrays.asList(longs);
        Gson gson = new Gson();
        String json = gson.toJson(listId);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        restClient.deactivateAllUsers(listId);

        verify(restTemplate).exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.USER_DEACTIVATE
            + RestTemplateLinks.ID + listId, HttpMethod.PUT, entity, Long[].class);

    }

    @Test
    void managementRegisterUser() {
        // given
        UserManagementDto userManagementDto = new UserManagementDto();
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserManagementDto> entity = new HttpEntity<>(userManagementDto, headers);
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.OWN_SECURITY_REGISTER, HttpMethod.POST, entity, Object.class))
                .thenReturn(ResponseEntity.ok(Object));

        // when
        restClient.managementRegisterUser(userManagementDto);

        // then
        verify(restTemplate).exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.OWN_SECURITY_REGISTER, HttpMethod.POST, entity, Object.class);
    }

    @Test
    void addEcoNews() {
        EcoNewsForSendEmailDto message = ModelUtils.getEcoNewsForSendEmailDto();
        HttpHeaders httpHeaders = new HttpHeaders();
        String accessToken = "accessToken";
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set(AUTHORIZATION, accessToken);
        HttpEntity<EcoNewsForSendEmailDto> entity = new HttpEntity<>(message, httpHeaders);
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.ADD_ECO_NEWS, HttpMethod.POST, entity, Object.class))
                .thenReturn(ResponseEntity.ok(Object));
        restClient.addEcoNews(message);

        verify(restTemplate).exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.ADD_ECO_NEWS, HttpMethod.POST, entity, Object.class);
    }

    @Test
    void sendNewEventComment() {
        EventCommentForSendEmailDto message = ModelUtils.getEventCommentForSendEmailDto();
        HttpHeaders httpHeaders = new HttpHeaders();
        String accessToken = "accessToken";
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set(AUTHORIZATION, accessToken);
        HttpEntity<EventCommentForSendEmailDto> entity = new HttpEntity<>(message, httpHeaders);
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.ADD_EVENT_COMMENT, HttpMethod.POST, entity, Object.class))
                .thenReturn(ResponseEntity.ok(Object));
        restClient.sendNewEventComment(message);

        verify(restTemplate).exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.ADD_EVENT_COMMENT, HttpMethod.POST, entity, Object.class);
    }

    @Test
    void sendReport() {
        SendReportEmailMessage message = ModelUtils.getSendReportEmailMessage();
        HttpEntity<SendReportEmailMessage> entity = new HttpEntity<>(message, ModelUtils.getHeaders());
        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.SEND_REPORT, HttpMethod.POST, entity, Object.class))
                .thenReturn(ResponseEntity.ok(Object));
        when(jwtTool.createAccessToken(SYSTEM_EMAIL, Role.ROLE_ADMIN)).thenReturn("accessToken");
        restClient.sendReport(message);

        verify(jwtTool).createAccessToken(SYSTEM_EMAIL, Role.ROLE_ADMIN);
        verify(restTemplate).exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.SEND_REPORT, HttpMethod.POST, entity, Object.class);
    }

    @Test
    void changePlaceStatus() {
        SendChangePlaceStatusEmailMessage message = ModelUtils.getSendChangePlaceStatusEmailMessage();
        HttpHeaders httpHeaders = new HttpHeaders();
        String accessToken = "accessToken";
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set(AUTHORIZATION, accessToken);

        HttpEntity<SendChangePlaceStatusEmailMessage> entity = new HttpEntity<>(message, httpHeaders);
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.CHANGE_PLACE_STATUS, HttpMethod.POST, entity, Object.class))
                .thenReturn(ResponseEntity.ok(Object));
        restClient.changePlaceStatus(message);

        verify(restTemplate).exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.CHANGE_PLACE_STATUS, HttpMethod.POST, entity, Object.class);
    }

    @Test
    void sendHabitNotification() {
        SendHabitNotification notification = ModelUtils.getSendHabitNotification();
        HttpEntity<SendHabitNotification> entity = new HttpEntity<>(notification, ModelUtils.getHeaders());
        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.SEND_HABIT_NOTIFICATION, HttpMethod.POST, entity, Object.class))
                .thenReturn(ResponseEntity.ok(Object));
        when(jwtTool.createAccessToken(SYSTEM_EMAIL, Role.ROLE_ADMIN)).thenReturn("accessToken");
        restClient.sendHabitNotification(notification);

        verify(jwtTool).createAccessToken(SYSTEM_EMAIL, Role.ROLE_ADMIN);
        verify(restTemplate).exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.SEND_HABIT_NOTIFICATION, HttpMethod.POST, entity, Object.class);
    }

    @Test
    void save() {
        UserVO userVO = ModelUtils.getUserVO();
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<UserVO> entity = new HttpEntity<>(userVO, headers);
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.USER, HttpMethod.POST, entity, Object.class)).thenReturn(ResponseEntity.ok(Object));
        restClient.save(userVO);

        verify(restTemplate).exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.USER, HttpMethod.POST, entity, Object.class);
    }

    @Test
    void saveTest() {
        UserVO userVO = ModelUtils.getUserVO();
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<UserVO> entity = new HttpEntity<>(userVO, headers);
        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.USER, HttpMethod.POST, entity, Object.class)).thenReturn(ResponseEntity.ok(Object));
        restClient.save(userVO, accessToken);

        verify(restTemplate).exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.USER, HttpMethod.POST, entity, Object.class);

    }

    @Test
    void findUserForManagementByPage() {
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        List<UserManagementDto> ecoNewsDtos = Collections.singletonList(new UserManagementDto());
        PageableAdvancedDto<UserManagementDto> pageableAdvancedDto =
            new PageableAdvancedDto<>(ecoNewsDtos, 2, 0, 3, 0, true, true, true, true);
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.USER_FIND_USER_FOR_MANAGEMENT + RestTemplateLinks.PAGE + pageable.getPageNumber()
            + RestTemplateLinks.SIZE + pageable.getPageSize() + "&sort=id,ASC", HttpMethod.GET, entity,
            new ParameterizedTypeReference<PageableAdvancedDto<UserManagementDto>>() {
            })).thenReturn(ResponseEntity.ok(pageableAdvancedDto));
        assertEquals(pageableAdvancedDto, restClient.findUserForManagementByPage(pageable));
    }

    @Test
    void searchTest() {
        Pageable pageable = PageRequest.of(0, 20, Sort.unsorted());
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(AUTHORIZATION, accessToken);
        UserManagementViewDto userViewDto =
            UserManagementViewDto.builder()
                .id("1L")
                .name("vivo")
                .email("test@ukr.net")
                .userCredo("Hello")
                .role("1")
                .userStatus("1")
                .build();
        List<UserManagementVO> userManagementVOS = Collections.singletonList(new UserManagementVO());
        PageableAdvancedDto<UserManagementVO> userAdvancedDto =
            new PageableAdvancedDto<>(userManagementVOS, 20, 0, 0, 0,
                true, true, true, true);
        HttpEntity<UserManagementViewDto> entity = new HttpEntity<>(userViewDto, headers);
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.USER_SEARCH + RestTemplateLinks.PAGE + pageable.getPageNumber()
            + RestTemplateLinks.SIZE + pageable.getPageSize()
            + RestTemplateLinks.SORT, HttpMethod.POST, entity,
            new ParameterizedTypeReference<PageableAdvancedDto<UserManagementVO>>() {
            })).thenReturn(ResponseEntity.ok(userAdvancedDto));
        assertEquals(userAdvancedDto, restClient.search(pageable, userViewDto));
    }

    @Test
    void scheduleDeleteDeactivatedUsers() {
        HttpEntity<String> entity = new HttpEntity<>(new HttpHeaders());
        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS + RestTemplateLinks.DELETE_DEACTIVATED_USERS,
            HttpMethod.POST, entity, Object.class))
                .thenReturn(ResponseEntity.ok(Object));
        restClient.scheduleDeleteDeactivatedUsers();
        verify(restTemplate, times(1)).exchange(GREEN_CITY_USER_ADDRESS + RestTemplateLinks.DELETE_DEACTIVATED_USERS,
            HttpMethod.POST, entity, Object.class);
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
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        Map<Integer, Long> expected = Collections.singletonMap(1, 1L);
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.FIND_ALL_REGISTRATION_MONTHS_MAP,
            HttpMethod.GET, entity, new ParameterizedTypeReference<Map<Integer, Long>>() {
            })).thenReturn(ResponseEntity.status(HttpStatus.OK).body(expected));

        assertEquals(expected, restClient.findAllRegistrationMonthsMap());
    }

    @Test
    void findAllUsersCities() {
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        List<String> expected = Collections.singletonList("text");
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.FIND_ALL_USERS_CITIES,
            HttpMethod.GET, entity, new ParameterizedTypeReference<List<String>>() {
            }))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body(expected));
        assertEquals(expected, restClient.findAllUsersCities());
    }

    @Test
    void sendEventCreationNotificationTest() {
        String accessToken = "Bearer null";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        SendEventCreationNotification notification = ModelUtils.getSendEventCreationNotification();
        HttpEntity<SendEventCreationNotification> entity = new HttpEntity<>(notification, headers);
        when(restTemplate.exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.SEND_EVENT_CREATION_NOTIFICATION, HttpMethod.POST, entity, Object.class))
                .thenReturn(ResponseEntity.ok(Object));
        restClient.sendEventCreationNotification(notification);

        verify(restTemplate).exchange(GREEN_CITY_USER_ADDRESS
            + RestTemplateLinks.SEND_EVENT_CREATION_NOTIFICATION, HttpMethod.POST, entity, Object.class);
    }
}
