package greencity.client;

import com.google.gson.Gson;
import greencity.constant.RestTemplateLinks;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.achievement.UserVOAchievement;
import greencity.dto.user.UserManagementDto;
import greencity.dto.user.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static greencity.constant.AppConstant.AUTHORIZATION;

@RequiredArgsConstructor
@Component
public class RestClient {
    private final RestTemplate restTemplate;
    @Value("${greencityuser.server.address}")
    private String greenCityUserServerAddress;

    /**
     * Method find user by principal.
     *
     * @param email of {@link UserVO}
     * @author Orest Mamchuk
     */
    public UserVO findByEmail(String email) {
        return restTemplate.getForObject(greenCityUserServerAddress
            + RestTemplateLinks.USER_FIND_BY_EMAIL + RestTemplateLinks.EMAIL + email, UserVO.class);
    }

    /**
     * Method that allow you to find {@link UserVO} by Id.
     *
     * @param id          a value of {@link Long}
     * @param accessToken for authorization
     * @return {@link UserVO}
     * @author Orest Mamchuk
     */
    public UserVO findById(Long id, String accessToken) {
        HttpEntity<String> entity = setHeader(accessToken);
        return restTemplate.exchange(greenCityUserServerAddress
            + RestTemplateLinks.USER_FIND_BY_ID + RestTemplateLinks.ID + id, HttpMethod.GET, entity, UserVO.class)
            .getBody();
    }

    /**
     * Method that allow you to find {@link UserVO} by ID.
     *
     * @param id a value of {@link Long}
     * @return {@link UserVO}
     * @author Orest Mamchuk
     */
    public UserVOAchievement findUserForAchievement(Long id) {
        return restTemplate.getForObject(greenCityUserServerAddress
            + RestTemplateLinks.USER_FIND_BY_ID_FOR_ACHIEVEMENT + RestTemplateLinks.ID + id, UserVOAchievement.class);
    }

    /**
     * Find {@link UserVO} for management by page .
     *
     * @param pageable    a value with pageable configuration.
     * @param accessToken for authorization
     * @return a dto of {@link PageableAdvancedDto}.
     * @author Orest Mamchuk
     */
    public PageableAdvancedDto<UserManagementDto> findUserForManagementByPage(Pageable pageable, String accessToken) {
        HttpEntity<String> entity = setHeader(accessToken);
        return restTemplate.exchange(greenCityUserServerAddress
            + RestTemplateLinks.USER_FIND_USER_FOR_MANAGEMENT + RestTemplateLinks.PAGE + pageable.getPageNumber()
            + RestTemplateLinks.SIZE + pageable.getPageSize(), HttpMethod.GET, entity,
            new ParameterizedTypeReference<PageableAdvancedDto<UserManagementDto>>() {
            }).getBody();
    }

    /**
     * Method for getting UserVO by search query.
     *
     * @param pageable    {@link Pageable}.
     * @param query       query to search
     * @param accessToken for authorization
     * @return {@link PageableAdvancedDto} of {@link UserManagementDto} instances.
     * @author Orest Mamchuk
     */
    public PageableAdvancedDto<UserManagementDto> searchBy(Pageable pageable, String query, String accessToken) {
        HttpEntity<String> entity = setHeader(accessToken);
        return restTemplate.exchange(greenCityUserServerAddress
            + RestTemplateLinks.SEARCH_BY + RestTemplateLinks.PAGE + pageable.getPageNumber()
            + RestTemplateLinks.SIZE + pageable.getPageSize()
            + RestTemplateLinks.QUERY + query, HttpMethod.GET, entity,
            new ParameterizedTypeReference<PageableAdvancedDto<UserManagementDto>>() {
            }).getBody();
    }

    /**
     * Method for getting UserVO by search query.
     *
     * @param userDto     dto with updated fields.
     * @param accessToken for authorization
     * @author Orest Mamchuk
     */
    public void updateUser(UserManagementDto userDto, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<UserManagementDto> entity = new HttpEntity<>(userDto, headers);
        restTemplate.exchange(greenCityUserServerAddress
            + RestTemplateLinks.USER, HttpMethod.PUT, entity, Object.class)
            .getBody();
    }

    /**
     * Method for getting all Users.
     *
     * @param accessToken for authorization
     * @return {@link List} of {@link UserVO} instances.
     * @author Orest Mamchuk
     */
    public List<UserVO> findAll(String accessToken) {
        HttpEntity<String> entity = setHeader(accessToken);
        ResponseEntity<UserVO[]> exchange = restTemplate.exchange(greenCityUserServerAddress
            + RestTemplateLinks.USER_FIND_ALL, HttpMethod.GET, entity, UserVO[].class);
        UserVO[] responseDtos = exchange.getBody();
        assert responseDtos != null;
        return Arrays.asList(responseDtos);
    }

    /**
     * Method for getting all Users.
     *
     * @param accessToken for authorization
     * @return {@link List} of {@link UserVO} instances.
     * @author Orest Mamchuk
     */
    public List<UserManagementDto> findUserFriendsByUserId(Long id, String accessToken) {
        HttpEntity<String> entity = setHeader(accessToken);
        ResponseEntity<UserManagementDto[]> exchange = restTemplate.exchange(greenCityUserServerAddress
            + RestTemplateLinks.USER + "/" + id + RestTemplateLinks.FRIENDS, HttpMethod.GET, entity,
            UserManagementDto[].class);
        UserManagementDto[] responseDtos = exchange.getBody();
        assert responseDtos != null;
        return Arrays.asList(responseDtos);
    }

    /**
     * Method that allow you to find not 'DEACTIVATED' {@link UserVO} by email.
     *
     * @param email - {@link UserVO}'s email
     * @return {@link UserVO}
     * @author Orest Mamchuk
     */
    public Optional<UserVO> findNotDeactivatedByEmail(String email, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        UserVO body = restTemplate.exchange(greenCityUserServerAddress
            + RestTemplateLinks.USER_FIND_NOT_DEACTIVATED_BY_EMAIL + RestTemplateLinks.EMAIL
            + email, HttpMethod.GET, entity, UserVO.class)
            .getBody();
        assert body != null;
        return Optional.of(body);
    }

    /**
     * Method find user id by email.
     *
     * @param email of {@link UserVO}
     * @author Orest Mamchuk
     */
    public Long findIdByEmail(String email, String accessToken) {
        HttpEntity<String> entity = setHeader(accessToken);
        return restTemplate.exchange(greenCityUserServerAddress
            + RestTemplateLinks.USER_FIND_ID_BY_EMAIL
            + RestTemplateLinks.EMAIL + email, HttpMethod.GET, entity, Long.class)
            .getBody();
    }

    /**
     * Updates last activity time for a given user.
     *
     * @param userId               - {@link UserVO}'s id
     * @param userLastActivityTime - new {@link UserVO}'s last activity time
     * @author Orest Mamchuk
     */
    public void updateUserLastActivityTime(Long userId, Date userLastActivityTime, String accessToken) {
        HttpEntity<String> entity = setHeader(accessToken);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        String strDate = dateFormat.format(userLastActivityTime);
        restTemplate.exchange(greenCityUserServerAddress + RestTemplateLinks.USER + "/" + userId
            + RestTemplateLinks.UPDATE_USER_LAST_ACTIVITY_TIME
            + strDate, HttpMethod.PUT, entity, Object.class);
    }

    /**
     * Method for setting {@link UserVO}'s status to DEACTIVATED, so the user will
     * not be able to log in into the system.
     *
     * @param userId      - {@link UserVO}'s id
     * @param accessToken for authorization
     * @author Orest Mamchuk
     */
    public void deactivateUser(Long userId, String accessToken) {
        HttpEntity<String> entity = setHeader(accessToken);
        restTemplate.exchange(greenCityUserServerAddress + RestTemplateLinks.USER_DEACTIVATE
            + RestTemplateLinks.ID + userId, HttpMethod.PUT, entity, Object.class);
    }

    /**
     * Method for setting {@link UserVO}'s status to ACTIVATED.
     *
     * @param userId      - {@link UserVO}'s id
     * @param accessToken for authorization
     * @author Orest Mamchuk
     */
    public void setActivatedStatus(Long userId, String accessToken) {
        HttpEntity<String> entity = setHeader(accessToken);
        restTemplate.exchange(greenCityUserServerAddress + RestTemplateLinks.USER_ACTIVATE
            + RestTemplateLinks.ID + userId, HttpMethod.PUT, entity, Object.class);
    }

    /**
     * Method for setting to a list of {@link UserVO} status DEACTIVATED, so the
     * users will not be able to log in into the system.
     *
     * @param listId      {@link List} populated with ids of {@link UserVO} to be
     *                    deleted.
     * @param accessToken for authorization
     * @author Orest Mamchuk
     */
    public List<Long> deactivateAllUsers(List<Long> listId, String accessToken) {
        Gson gson = new Gson();
        String json = gson.toJson(listId);
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);
        ResponseEntity<Long[]> exchange = restTemplate.exchange(greenCityUserServerAddress
            + RestTemplateLinks.USER_DEACTIVATE
            + RestTemplateLinks.ID + listId, HttpMethod.PUT, entity, Long[].class);
        Long[] responseDtos = exchange.getBody();
        assert responseDtos != null;
        return Arrays.asList(responseDtos);
    }

    /**
     * Register new user from admin panel.
     *
     * @param userDto     dto with updated fields.
     * @param accessToken for authorization
     * @author Orest Mamchuk
     */
    public void managementRegisterUser(UserManagementDto userDto, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<UserManagementDto> entity = new HttpEntity<>(userDto, headers);
        restTemplate.exchange(greenCityUserServerAddress
            + RestTemplateLinks.OWN_SECURITY_REGISTER, HttpMethod.POST, entity, Object.class)
            .getBody();
    }

    /**
     * Method that allow you to save new {@link UserVO}.
     *
     * @param userVO      for save User.
     * @param accessToken for authorization
     * @author Orest Mamchuk
     */
    public void save(UserVO userVO, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        HttpEntity<UserVO> entity = new HttpEntity<>(userVO, headers);
        restTemplate.exchange(greenCityUserServerAddress
            + RestTemplateLinks.USER, HttpMethod.POST, entity, Object.class)
            .getBody();
    }

    /**
     * Method makes headers for RestTemplate.
     *
     * @param accessToken for authorization
     * @return {@link HttpEntity}
     */
    private HttpEntity<String> setHeader(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, accessToken);
        return new HttpEntity<>(headers);
    }
}
