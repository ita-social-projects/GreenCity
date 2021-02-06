package greencity.client;

import static greencity.constant.AppConstant.AUTHORIZATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gson.Gson;
import greencity.ModelUtils;
import greencity.constant.RestTemplateLinks;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.achievement.UserVOAchievement;
import greencity.dto.user.UserManagementDto;
import greencity.dto.user.UserManagementVO;
import greencity.dto.user.UserManagementViewDto;
import greencity.dto.user.UserVO;
import greencity.message.AddEcoNewsMessage;
import greencity.message.SendChangePlaceStatusEmailMessage;
import greencity.message.SendHabitNotification;
import greencity.message.SendReportEmailMessage;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class RestClientTest {
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private java.lang.Object Object;
    @Value("${greencityuser.server.address}")
    private String greenCityUserServerAddress;
    @InjectMocks
    private RestClient restClient;

    @Test
    void findByEmail() {
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        UserVO userVO = ModelUtils.getUserVO();
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(greenCityUserServerAddress + RestTemplateLinks.USER_FIND_BY_EMAIL
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
        when(restTemplate.exchange(greenCityUserServerAddress
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
        when(restTemplate.exchange(greenCityUserServerAddress
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
        when(restTemplate.exchange(greenCityUserServerAddress
            + RestTemplateLinks.SEARCH_BY + RestTemplateLinks.PAGE + pageable.getPageNumber()
            + RestTemplateLinks.SIZE + pageable.getPageSize()
            + RestTemplateLinks.QUERY + query, HttpMethod.GET, entity,
            new ParameterizedTypeReference<PageableAdvancedDto<UserManagementDto>>() {
            })).thenReturn(ResponseEntity.ok(pageableAdvancedDto));
        assertEquals(pageableAdvancedDto, restClient.searchBy(pageable, query));
    }

    @Test
    void updateUser() {
        UserManagementDto userManagementDto = new UserManagementDto();
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<UserManagementDto> entity = new HttpEntity<>(userManagementDto, headers);
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(greenCityUserServerAddress
            + RestTemplateLinks.USER, HttpMethod.PUT, entity, Object.class)).thenReturn(ResponseEntity.ok(Object));
        restClient.updateUser(userManagementDto);
        verify(restTemplate).exchange(greenCityUserServerAddress
            + RestTemplateLinks.USER, HttpMethod.PUT, entity, Object.class);
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
        when(restTemplate.exchange(greenCityUserServerAddress
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
        when(restTemplate.exchange(greenCityUserServerAddress
            + RestTemplateLinks.USER + "/" + 1L + RestTemplateLinks.FRIENDS, HttpMethod.GET, entity,
            UserManagementDto[].class)).thenReturn(ResponseEntity.ok(userManagementDtos));
        restClient.findUserFriendsByUserId(1L);

        verify(restTemplate).exchange(greenCityUserServerAddress + RestTemplateLinks.USER + "/"
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
        when(restTemplate.exchange(greenCityUserServerAddress
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
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(null);
        when(httpServletRequest.getCookies()).thenReturn(cookies);
        when(httpServletRequest.getRequestURI()).thenReturn("/management");
        when(restTemplate.exchange(greenCityUserServerAddress
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
        when(restTemplate.exchange(greenCityUserServerAddress
            + RestTemplateLinks.USER_FIND_ID_BY_EMAIL
            + RestTemplateLinks.EMAIL + email, HttpMethod.GET, entity, Long.class))
                .thenReturn(ResponseEntity.ok(1L));

        assertEquals(1L, restClient.findIdByEmail(email));
    }

    @Test
    void updateUserLastActivityTime() {
        Date date = new Date();
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        String strDate = dateFormat.format(date);
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(greenCityUserServerAddress + RestTemplateLinks.USER + "/" + 1L
            + RestTemplateLinks.UPDATE_USER_LAST_ACTIVITY_TIME
            + strDate, HttpMethod.PUT, entity, Object.class)).thenReturn(ResponseEntity.ok(Object));

        restClient.updateUserLastActivityTime(1L, date);
        verify(restTemplate).exchange(greenCityUserServerAddress + RestTemplateLinks.USER + "/" + 1L
            + RestTemplateLinks.UPDATE_USER_LAST_ACTIVITY_TIME
            + strDate, HttpMethod.PUT, entity, Object.class);
    }

    @Test
    void deactivateUser() {
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(greenCityUserServerAddress + RestTemplateLinks.USER_DEACTIVATE
            + RestTemplateLinks.ID + 1L, HttpMethod.PUT, entity, Object.class))
                .thenReturn(ResponseEntity.ok(Object));
        restClient.deactivateUser(1L);
        verify(restTemplate).exchange(greenCityUserServerAddress + RestTemplateLinks.USER_DEACTIVATE
            + RestTemplateLinks.ID + 1L, HttpMethod.PUT, entity, Object.class);
    }

    @Test
    void setActivatedStatus() {
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(greenCityUserServerAddress + RestTemplateLinks.USER_ACTIVATE
            + RestTemplateLinks.ID + 1L, HttpMethod.PUT, entity, Object.class))
                .thenReturn(ResponseEntity.ok(Object));
        restClient.setActivatedStatus(1L);
        verify(restTemplate).exchange(greenCityUserServerAddress + RestTemplateLinks.USER_ACTIVATE
            + RestTemplateLinks.ID + 1L, HttpMethod.PUT, entity, Object.class);
    }

    @Test
    void deactivateAllUsers() {
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        Long[] longs = new Long[] {1L, 2L};
        List<Long> listId = Arrays.asList(longs);
        Gson gson = new Gson();
        String json = gson.toJson(listId);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(greenCityUserServerAddress
            + RestTemplateLinks.USER_DEACTIVATE
            + RestTemplateLinks.ID + listId, HttpMethod.PUT, entity, Long[].class))
                .thenReturn(ResponseEntity.ok(longs));
        assertEquals(listId, restClient.deactivateAllUsers(listId));
    }

    @Test
    void managementRegisterUser() {
        UserManagementDto userManagementDto = new UserManagementDto();
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<UserManagementDto> entity = new HttpEntity<>(userManagementDto, headers);
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(greenCityUserServerAddress
            + RestTemplateLinks.OWN_SECURITY_REGISTER, HttpMethod.POST, entity, Object.class))
                .thenReturn(ResponseEntity.ok(Object));
        restClient.managementRegisterUser(userManagementDto);

        verify(restTemplate).exchange(greenCityUserServerAddress
            + RestTemplateLinks.OWN_SECURITY_REGISTER, HttpMethod.POST, entity, Object.class);
    }

    @Test
    void addEcoNews() {
        AddEcoNewsMessage message = ModelUtils.getAddEcoNewsMessage();
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<AddEcoNewsMessage> entity = new HttpEntity<>(message, headers);
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(greenCityUserServerAddress
            + RestTemplateLinks.ADD_ECO_NEWS, HttpMethod.POST, entity, Object.class))
                .thenReturn(ResponseEntity.ok(Object));
        restClient.addEcoNews(message);

        verify(restTemplate).exchange(greenCityUserServerAddress
            + RestTemplateLinks.ADD_ECO_NEWS, HttpMethod.POST, entity, Object.class);
    }

    @Test
    void sendReport() {
        SendReportEmailMessage message = ModelUtils.getSendReportEmailMessage();
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<SendReportEmailMessage> entity = new HttpEntity<>(message, headers);
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(greenCityUserServerAddress
            + RestTemplateLinks.SEND_REPORT, HttpMethod.POST, entity, Object.class))
                .thenReturn(ResponseEntity.ok(Object));
        restClient.sendReport(message);

        verify(restTemplate).exchange(greenCityUserServerAddress
            + RestTemplateLinks.SEND_REPORT, HttpMethod.POST, entity, Object.class);
    }

    @Test
    void changePlaceStatus() {
        SendChangePlaceStatusEmailMessage message = ModelUtils.getSendChangePlaceStatusEmailMessage();
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<SendChangePlaceStatusEmailMessage> entity = new HttpEntity<>(message, headers);
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(restTemplate.exchange(greenCityUserServerAddress
            + RestTemplateLinks.CHANGE_PLACE_STATUS, HttpMethod.POST, entity, Object.class))
                .thenReturn(ResponseEntity.ok(Object));
        restClient.changePlaceStatus(message);

        verify(restTemplate).exchange(greenCityUserServerAddress
            + RestTemplateLinks.CHANGE_PLACE_STATUS, HttpMethod.POST, entity, Object.class);
    }

    @Test
    void sendHabitNotification() {
        SendHabitNotification notification = ModelUtils.getSendHabitNotification();
        HttpEntity<SendHabitNotification> entity = new HttpEntity<>(notification, new HttpHeaders());
        when(restTemplate.exchange(greenCityUserServerAddress
            + RestTemplateLinks.SEND_HABIT_NOTIFICATION, HttpMethod.POST, entity, Object.class))
                .thenReturn(ResponseEntity.ok(Object));
        restClient.sendHabitNotification(notification);

        verify(restTemplate).exchange(greenCityUserServerAddress
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
        when(restTemplate.exchange(greenCityUserServerAddress
            + RestTemplateLinks.USER, HttpMethod.POST, entity, Object.class)).thenReturn(ResponseEntity.ok(Object));
        restClient.save(userVO);

        verify(restTemplate).exchange(greenCityUserServerAddress
            + RestTemplateLinks.USER, HttpMethod.POST, entity, Object.class);
    }

    @Test
    void saveTest() {
        UserVO userVO = ModelUtils.getUserVO();
        String accessToken = "accessToken";
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<UserVO> entity = new HttpEntity<>(userVO, headers);
        when(restTemplate.exchange(greenCityUserServerAddress
            + RestTemplateLinks.USER, HttpMethod.POST, entity, Object.class)).thenReturn(ResponseEntity.ok(Object));
        restClient.save(userVO, accessToken);

        verify(restTemplate).exchange(greenCityUserServerAddress
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
        when(restTemplate.exchange(greenCityUserServerAddress
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
        when(restTemplate.exchange(greenCityUserServerAddress
            + RestTemplateLinks.USER_SEARCH + RestTemplateLinks.PAGE + pageable.getPageNumber()
            + RestTemplateLinks.SIZE + pageable.getPageSize(), HttpMethod.POST, entity,
            new ParameterizedTypeReference<PageableAdvancedDto<UserManagementVO>>() {
            })).thenReturn(ResponseEntity.ok(userAdvancedDto));
        assertEquals(userAdvancedDto, restClient.search(pageable, userViewDto));
    }
}
