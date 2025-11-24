package greencity.client;

import greencity.annotations.CheckEmailPreference;
import greencity.constant.AppConstant;
import greencity.dto.econews.InterestingEcoNewsDto;
import greencity.dto.notification.UbsNotificationDto;
import greencity.dto.place.UpdatePlaceStatusWithUserEmailDto;
import greencity.dto.user.UserManagementCreateDto;
import greencity.dto.user.UserManagementDto;
import greencity.dto.user.UserManagementUpdateDto;
import greencity.dto.user.UserManagementVO;
import greencity.dto.user.UserRoleDto;
import greencity.dto.user.UserVO;
import greencity.enums.EmailPreference;
import greencity.enums.Role;
import greencity.properties.RemoteWebClientProperties;
import greencity.service.UserService;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import greencity.message.ScheduledEmailMessage;
import greencity.message.SendHabitNotification;
import greencity.message.SendReportEmailMessage;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import greencity.security.jwt.JwtTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.util.UriComponentsBuilder;
import greencity.constant.RestTemplateLinks;
import greencity.dto.PageableAdvancedDto;
import greencity.enums.EmailNotification;
import static greencity.constant.AppConstant.AUTHORIZATION;

@Component
@Slf4j
public class RestClient {
    private final RestTemplate restTemplate;
    private final UserService userService;
    private final RemoteWebClientProperties remoteWebClientProperties;

    private final HttpServletRequest httpServletRequest;
    private final JwtTool jwtTool;
    private static final String PAGE_QUERY_PARAM = "page";
    private static final String PAGE_SIZE_QUERY_PARAM = "size";
    private static final String USER_EMAIL_QUERY_PARAM = "email";
    private static final String UNABLE_TO_REACH_TO_UBS_WARN_MESSAGE =
        "Exception occurred while trying to reach to UBS: {}";

    /**
     * Constructs a new instance of the RestClient class.
     *
     * @param restTemplate              The RestTemplate to be used for making HTTP
     *                                  requests to GreenCityUser.
     * @param httpServletRequest        The HttpServletRequest object contains data
     *                                  related to the current http request.
     * @param jwtTool                   The JwtTool is used to create JWT tokens for
     *                                  system requests to GreenCityUser.
     * @param remoteWebClientProperties The RemoteWebClient is used to get
     *                                  properties from Environmet such as
     *                                  systemEmail used to creat JWT tokens for
     *                                  system requests to GreenCityUser,
     *                                  greenCityUserServerAddress,
     *                                  greenCityUbsServerAddress
     */
    public RestClient(RestTemplate restTemplate,
        UserService userService,
        HttpServletRequest httpServletRequest,
        JwtTool jwtTool,
        RemoteWebClientProperties remoteWebClientProperties) {
        this.restTemplate = restTemplate;
        this.httpServletRequest = httpServletRequest;
        this.userService = userService;
        this.jwtTool = jwtTool;
        this.remoteWebClientProperties = remoteWebClientProperties;
    }

    public PageableAdvancedDto<UbsNotificationDto> findAllNotificationsForUserFromUbs(Principal principal,
        Pageable pageable) {
        HttpHeaders httpHeaders = setHeader();
        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);
        String userEmail = principal.getName();

        UriComponentsBuilder ubsNotificationsUrlBuilder = UriComponentsBuilder.fromUriString(
            remoteWebClientProperties.getGreencityUbsServerAddress() + RestTemplateLinks.NOTIFICATIONS);

        String url = ubsNotificationsUrlBuilder
            .queryParam(PAGE_QUERY_PARAM, pageable.getPageNumber())
            .queryParam(PAGE_SIZE_QUERY_PARAM, pageable.getPageSize())
            .queryParam(USER_EMAIL_QUERY_PARAM, userEmail)
            .toUriString();

        ResponseEntity<PageableAdvancedDto<UbsNotificationDto>> notifications;
        try {
            notifications = restTemplate.exchange(
                url,
                HttpMethod.GET,
                httpEntity,
                new ParameterizedTypeReference<>() {
                });
        } catch (RestClientException e) {
            log.warn(UNABLE_TO_REACH_TO_UBS_WARN_MESSAGE, e.getMessage());
            return PageableAdvancedDto.<UbsNotificationDto>builder()
                .page(Collections.emptyList())
                .build();
        }

        return notifications.getBody();
    }

    /**
     * Method for getting all users by their {@link EmailNotification}.
     *
     * @param emailNotification enum with {@link EmailNotification} value.
     * @return {@link List} of {@link UserVO}.
     */
    public List<UserVO> findAllByEmailNotification(EmailNotification emailNotification) {
        HttpEntity<String> entity = new HttpEntity<>(new HttpHeaders());
        ResponseEntity<List<UserVO>> exchange = restTemplate.exchange(
            remoteWebClientProperties.getGreencityUserServerAddress()
                + RestTemplateLinks.USER_FIND_ALL_BY_EMAIL_NOTIFICATION
                + RestTemplateLinks.EMAIL_NOTIFICATION + emailNotification,
            HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
            });
        List<UserVO> users = exchange.getBody();
        if (users != null) {
            userService.fillGreenCityInfoInUsers(users);
        }
        return users;
    }

    /**
     * Method that find all users cities.
     *
     * @return {@link List} of cities.
     */
    public List<String> findAllUsersCities() {
        HttpEntity<String> entity = new HttpEntity<>(setHeader());
        ResponseEntity<List<String>> exchange = restTemplate.exchange(
            remoteWebClientProperties.getGreencityUserServerAddress()
                + RestTemplateLinks.FIND_ALL_USERS_CITIES,
            HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
            });
        return exchange.getBody();
    }

    /**
     * Method that find all registration months.
     *
     * @return {@link Map} with months.
     */
    public Map<Integer, Long> findAllRegistrationMonthsMap() {
        HttpEntity<String> entity = new HttpEntity<>(setHeader());
        ResponseEntity<Map<Integer, Long>> exchange = restTemplate.exchange(
            remoteWebClientProperties.getGreencityUserServerAddress()
                + RestTemplateLinks.FIND_ALL_REGISTRATION_MONTHS_MAP,
            HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
            });
        return exchange.getBody();
    }

    /**
     * Method find user by principal.
     *
     * @param email of {@link UserVO}
     */
    public UserVO findByEmail(String email) {
        HttpEntity<String> entity = new HttpEntity<>(setHeader());
        UriComponentsBuilder url =
            UriComponentsBuilder.fromUriString(remoteWebClientProperties.getGreencityUserServerAddress()
                + RestTemplateLinks.USER_FIND_BY_EMAIL).queryParam(USER_EMAIL_QUERY_PARAM, email);
        UserVO user = restTemplate.exchange(url.toUriString(), HttpMethod.GET,
            entity, UserVO.class).getBody();
        if (user != null) {
            userService.fillGreenCityInfoInUsers(List.of(user));
        }
        return user;
    }

    /**
     * Method that allow you to find {@link UserVO} by Id.
     *
     * @param id a value of {@link Long}
     * @return {@link UserVO}
     */
    public UserVO findById(Long id) {
        String email = userService.findById(id).getEmail();
        return findByEmail(email);
    }

    /**
     * Find {@link UserManagementVO} for management by email.
     *
     * @param id a value of {@link Long}
     * @return a dto of {@link UserManagementVO}.
     */
    public UserManagementVO findUserForManagement(Long id) {
        UserVO user = userService.findById(id);
        HttpEntity<String> entity = new HttpEntity<>(setHeader());
        UserManagementVO dto = restTemplate.exchange(remoteWebClientProperties.getGreencityUserServerAddress()
            + RestTemplateLinks.USER_FIND_USER_FOR_MANAGEMENT + RestTemplateLinks.EMAIL + user.getEmail(),
            HttpMethod.GET, entity,
            new ParameterizedTypeReference<UserManagementVO>() {
            }).getBody();
        if (dto != null) {
            userService.fillGreenCityInfoInUsers(List.of(dto));
        }
        return dto;
    }

    /**
     * Method for getting UserVO by search query.
     *
     * @param userDto dto with updated fields.
     */
    public void updateUser(UserManagementDto userDto) {
        UserManagementUpdateDto updateDto = managementDtoToUpdateDto(userDto);
        HttpHeaders headers = setHeader();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserManagementUpdateDto> entity = new HttpEntity<>(updateDto, headers);
        restTemplate.exchange(remoteWebClientProperties.getGreencityUserServerAddress()
            + RestTemplateLinks.USER, HttpMethod.PUT, entity, Object.class);
        log.info("User with id {} has been updated", userDto.getId());
    }

    private UserManagementUpdateDto managementDtoToUpdateDto(UserManagementDto userDto) {
        return UserManagementUpdateDto.builder()
            .name(userDto.getName())
            .email(userDto.getEmail())
            .role(userDto.getRole())
            .status(userDto.getStatus())
            .build();
    }

    /**
     * Method for sending change role request.
     *
     * @param id   of user whose role is being changed
     * @param role new role
     */
    public void updateRole(Long id, Role role) {
        String email = userService.findById(id).getEmail();
        String url = remoteWebClientProperties.getGreencityUserServerAddress()
            + RestTemplateLinks.USER + "/role" + RestTemplateLinks.EMAIL + email;
        HttpHeaders headers = setHeader();
        headers.setContentType(MediaType.APPLICATION_JSON);
        UserRoleDto userRoleDto = new UserRoleDto(role);
        HttpEntity<UserRoleDto> entity = new HttpEntity<>(userRoleDto, headers);
        restTemplate.exchange(url, HttpMethod.PATCH, entity, Object.class);
    }

    /**
     * Method for getting all Users.
     *
     * @return {@link List} of {@link UserVO} instances.
     */
    public List<UserVO> findAll() {
        HttpEntity<String> entity = new HttpEntity<>(setHeader());
        ResponseEntity<UserVO[]> exchange =
            restTemplate.exchange(remoteWebClientProperties.getGreencityUserServerAddress()
                + RestTemplateLinks.USER_FIND_ALL, HttpMethod.GET, entity, UserVO[].class);
        UserVO[] responseDtos = exchange.getBody();
        if (responseDtos != null) {
            List<UserVO> users = Arrays.asList(responseDtos);
            userService.fillGreenCityInfoInUsers(users);
            return users;
        }
        return List.of();
    }

    /**
     * Method for getting all Users.
     *
     * @return {@link List} of {@link UserVO} instances.
     */
    public List<UserManagementVO> findUserFriendsByUserId(Long id) {
        String email = userService.findById(id).getEmail();
        HttpEntity<String> entity = new HttpEntity<>(setHeader());
        ResponseEntity<UserManagementVO[]> exchange = restTemplate.exchange(
            remoteWebClientProperties.getGreencityUserServerAddress()
                + RestTemplateLinks.USER + RestTemplateLinks.FRIENDS + RestTemplateLinks.EMAIL + email,
            HttpMethod.GET,
            entity, UserManagementVO[].class);
        UserManagementVO[] responseDtos = exchange.getBody();
        if (responseDtos != null) {
            List<UserManagementVO> users = Arrays.asList(responseDtos);
            userService.fillGreenCityInfoInUsers(users);
            return users;
        }
        return List.of();
    }

    /**
     * Method for getting {@link String} user language.
     *
     * @param userId of the searched {@link UserVO}.
     * @return current user language {@link String}.
     */
    public String getUserLang(Long userId) {
        String email = userService.findById(userId).getEmail();
        HttpEntity<String> entity = new HttpEntity<>(setHeader());
        String body = restTemplate
            .exchange(remoteWebClientProperties.getGreencityUserServerAddress() + RestTemplateLinks.USER_LANG
                + RestTemplateLinks.EMAIL + email, HttpMethod.GET, entity, String.class)
            .getBody();
        assert body != null;
        return body;
    }

    /**
     * Register new user from admin panel.
     *
     * @param userDto dto with updated fields.
     */
    public void managementRegisterUser(UserManagementCreateDto userDto) {
        HttpHeaders headers = setHeader();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserManagementCreateDto> entity = new HttpEntity<>(userDto, headers);
        restTemplate.exchange(remoteWebClientProperties.getGreencityUserServerAddress()
            + RestTemplateLinks.OWN_SECURITY_REGISTER, HttpMethod.POST, entity, Object.class);
    }

    /**
     * Send InterestingEcoNewsDto to GreenCityUser.
     *
     * @param message with information for sending email about adding new eco news.
     */
    public void sendInterestingEcoNews(InterestingEcoNewsDto message) {
        HttpHeaders headers = setHeader();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<InterestingEcoNewsDto> entity = new HttpEntity<>(message, headers);
        restTemplate.exchange(remoteWebClientProperties.getGreencityUserServerAddress()
            + RestTemplateLinks.SEND_INTERESTING_ECO_NEWS, HttpMethod.POST, entity, Object.class);
    }

    public void sendEmailNotificationChangesPlaceStatus(UpdatePlaceStatusWithUserEmailDto message) {
        HttpHeaders headers = setHeader();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UpdatePlaceStatusWithUserEmailDto> entity = new HttpEntity<>(message, headers);
        restTemplate.exchange(remoteWebClientProperties.getGreencityUserServerAddress()
            + RestTemplateLinks.SEND_NOTIFICATION_STATUS_PLACE, HttpMethod.POST, entity, Object.class);
    }

    /**
     * send SendReportEmailMessage to GreenCityUser.
     *
     * @param reportEmailMessage with information for sending email report about new
     *                           places.
     */
    public void sendReport(SendReportEmailMessage reportEmailMessage) {
        HttpHeaders headers = setHeader();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SendReportEmailMessage> entity = new HttpEntity<>(reportEmailMessage, headers);
        restTemplate.exchange(remoteWebClientProperties.getGreencityUserServerAddress()
            + RestTemplateLinks.SEND_REPORT, HttpMethod.POST, entity, Object.class);
    }

    /**
     * send SendHabitNotification to GreenCityUser.
     *
     * @param sendHabitNotification with information for sending email to each user
     *                              that hasn't marked any habit during some period.
     */
    public void sendHabitNotification(SendHabitNotification sendHabitNotification) {
        HttpHeaders headers = setHeader();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SendHabitNotification> entity = new HttpEntity<>(sendHabitNotification, headers);
        restTemplate.exchange(remoteWebClientProperties.getGreencityUserServerAddress()
            + RestTemplateLinks.SEND_HABIT_NOTIFICATION, HttpMethod.POST, entity, Object.class);
    }

    /**
     * Method makes headers for RestTemplate.
     *
     * @return {@link HttpEntity}
     */
    private HttpHeaders setHeader() {
        String accessToken = null;
        if (RequestContextHolder.getRequestAttributes() != null) {
            Cookie[] cookies = httpServletRequest.getCookies();
            String uri = httpServletRequest.getRequestURI();

            if (cookies != null && uri.startsWith("/management")) {
                accessToken = getTokenFromCookies(cookies);
            }
        }

        if (!StringUtils.hasLength(accessToken)) {
            accessToken = AppConstant.TOKEN_PREFIX + jwtTool
                .createAccessToken(remoteWebClientProperties.getSystemEmailAddress(), Role.ROLE_ADMIN);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        return headers;
    }

    private String getTokenFromCookies(Cookie[] cookies) {
        String token = Arrays.stream(cookies)
            .filter(c -> c.getName().equals("accessToken"))
            .findFirst()
            .map(Cookie::getValue).orElse(null);
        return token == null ? null : AppConstant.TOKEN_PREFIX + token;
    }

    /**
     * Method sends scheduled email notification.
     *
     * @param message {@link ScheduledEmailMessage}.
     */
    public void sendScheduledEmailNotification(ScheduledEmailMessage message) {
        HttpHeaders headers = setHeader();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ScheduledEmailMessage> entity = new HttpEntity<>(message, headers);
        restTemplate.exchange(remoteWebClientProperties.getGreencityUserServerAddress()
            + RestTemplateLinks.SEND_SCHEDULED_NOTIFICATION, HttpMethod.POST, entity, Object.class);
    }

    @CheckEmailPreference(EmailPreference.SYSTEM)
    public void sendEmailNotificationSystem(ScheduledEmailMessage message) {
        sendScheduledEmailNotification(message);
    }

    @CheckEmailPreference(EmailPreference.LIKES)
    public void sendEmailNotificationLikes(ScheduledEmailMessage message) {
        sendScheduledEmailNotification(message);
    }

    @CheckEmailPreference(EmailPreference.COMMENTS)
    public void sendEmailNotificationComments(ScheduledEmailMessage message) {
        sendScheduledEmailNotification(message);
    }

    @CheckEmailPreference(EmailPreference.INVITES)
    public void sendEmailNotificationInvites(ScheduledEmailMessage message) {
        sendScheduledEmailNotification(message);
    }

    @CheckEmailPreference(EmailPreference.PLACES)
    public void sendEmailNotificationPlaces(ScheduledEmailMessage message) {
        sendScheduledEmailNotification(message);
    }
}
