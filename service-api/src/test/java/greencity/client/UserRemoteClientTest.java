package greencity.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import greencity.ModelUtils;
import greencity.dto.PageableDto;
import greencity.dto.emailpreference.EmailPreferenceDto;
import greencity.dto.language.LanguageDTO;
import greencity.dto.socialnetwork.SocialNetworkImageRequestDTO;
import greencity.dto.socialnetwork.SocialNetworkImageResponseDTO;
import greencity.dto.user.UserEmailDto;
import greencity.dto.user.UserEmailPreferencesStatisticDto;
import greencity.dto.user.UserRegistrationStatisticDto;
import greencity.dto.user.UserRoleDto;
import greencity.dto.user.UserRoleStatisticDto;
import greencity.dto.user.UserStatusDto;
import greencity.dto.user.UserStatusStatisticDto;
import greencity.dto.user.UserVO;
import greencity.dto.user.UserVOAdvancedDto;
import greencity.enums.DateGranularity;
import greencity.enums.EmailPreference;
import greencity.enums.EmailPreferencePeriodicity;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.LanguageNotFoundException;
import lombok.SneakyThrows;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class UserRemoteClientTest {
    static MockWebServer mockWebServer;
    UserRemoteClient userRemoteClient;
    ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    String emailQueryParam = "email";
    String idQueryParam = "id";
    String pageQueryParam = "page";
    String pageSizeQueryParam = "size";

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void initialize() {
        String baseUrl = "http://localhost:%s".formatted(mockWebServer.getPort());
        userRemoteClient = new UserRemoteClient(WebClient.builder().baseUrl(baseUrl).build());
    }

    @Test
    @SneakyThrows
    void findNotDeactivatedByEmailTest() {
        String email = "email@email.com";
        UserVO userVO = ModelUtils.getUserVO();
        String userVOJson = toJson(userVO);
        String expectedRequestPath = "/user/findNotDeactivatedByEmail?email=" + email;
        String expectedRequestMethod = HttpMethod.GET.name();

        mockWebServer.enqueue(new MockResponse()
            .setBody(userVOJson)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        Optional<UserVO> actualResult = userRemoteClient.findNotDeactivatedByEmail(email);
        assertTrue(actualResult.isPresent());
        UserVO actualUserVO = actualResult.get();
        assertEquals(userVO, actualUserVO);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
        assertNotNull(recordedRequest.getRequestUrl().queryParameter(emailQueryParam));
        assertEquals(email, recordedRequest.getRequestUrl().queryParameter(emailQueryParam));
    }

    @Test
    @SneakyThrows
    void findNotDeactivatedByIdTest() {
        Long id = 1L;
        UserVO userVO = ModelUtils.getUserVO();
        String userVOJson = toJson(userVO);
        String expectedRequestPath = "/user/findNotDeactivatedById?id=" + id;
        String expectedRequestMethod = HttpMethod.GET.name();

        mockWebServer.enqueue(new MockResponse()
            .setBody(userVOJson)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        Optional<UserVO> actualResult = userRemoteClient.findNotDeactivatedById(id);

        assertTrue(actualResult.isPresent());
        UserVO actualUserVO = actualResult.get();
        assertEquals(userVO, actualUserVO);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
        assertNotNull(recordedRequest.getRequestUrl().queryParameter(idQueryParam));
        assertEquals(id.toString(), recordedRequest.getRequestUrl().queryParameter(idQueryParam));
    }

    @Test
    @SneakyThrows
    void updateUserStatusTest() {
        UserStatusDto userStatusDto = ModelUtils.getUserStatusDto();
        UserStatusDto updatedStatusDto = ModelUtils.getUserStatusDto();
        String userStatusDtoJson = toJson(userStatusDto);
        String updatedStatusDtoJson = toJson(updatedStatusDto);
        String expectedRequestPath = "/user/status";
        String expectedRequestMethod = HttpMethod.PATCH.name();

        mockWebServer.enqueue(new MockResponse()
            .setBody(updatedStatusDtoJson)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        Optional<UserStatusDto> actualResult = userRemoteClient.updateUserStatus(userStatusDto);

        assertTrue(actualResult.isPresent());
        UserStatusDto actualStatusDto = actualResult.get();
        assertEquals(updatedStatusDto, actualStatusDto);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
        assertEquals(userStatusDtoJson, recordedRequest.getBody().readUtf8());
    }

    @Test
    @SneakyThrows
    void updateUserRoleTest() {
        Long id = 1L;
        Map<String, String> body = Map.of("role", "ADMIN");
        UserRoleDto userRoleDto = new UserRoleDto(Role.ROLE_USER);
        String bodyJson = toJson(body);
        String userRoleDtoJson = toJson(userRoleDto);
        String expectedRequestPath = "/user/" + id + "/role";
        String expectedRequestMethod = HttpMethod.PATCH.name();

        mockWebServer.enqueue(new MockResponse()
            .setBody(userRoleDtoJson)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        Optional<UserRoleDto> actualResult = userRemoteClient.updateUserRole(id, body);

        assertTrue(actualResult.isPresent());
        UserRoleDto actualRoleDto = actualResult.get();
        assertEquals(userRoleDto, actualRoleDto);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
        assertEquals(bodyJson, recordedRequest.getBody().readUtf8());
    }

    @Test
    @SneakyThrows
    void getUserRolesDistributionTest() {
        List<UserRoleStatisticDto> roleStats = List.of(
            new UserRoleStatisticDto(Role.ROLE_USER, 10L),
            new UserRoleStatisticDto(Role.ROLE_ADMIN, 20L));
        String roleStatsJson = toJson(roleStats);
        String expectedRequestPath = "/user/roles-distribution";
        String expectedRequestMethod = HttpMethod.GET.name();

        mockWebServer.enqueue(new MockResponse()
            .setBody(roleStatsJson)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        List<UserRoleStatisticDto> actualResult = userRemoteClient.getUserRolesDistribution();

        assertEquals(roleStats, actualResult);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
    }

    @Test
    @SneakyThrows
    void getUserStatusesDistributionTest() {
        List<UserStatusStatisticDto> statusStats = List.of(
            new UserStatusStatisticDto(UserStatus.ACTIVATED, 10L),
            new UserStatusStatisticDto(UserStatus.CREATED, 5L));
        String statusStatsJson = toJson(statusStats);
        String expectedRequestPath = "/user/statuses-distribution";
        String expectedRequestMethod = HttpMethod.GET.name();

        mockWebServer.enqueue(new MockResponse()
            .setBody(statusStatsJson)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        List<UserStatusStatisticDto> actualResult = userRemoteClient.getUserStatusesDistribution();

        assertEquals(statusStats, actualResult);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
    }

    @Test
    @SneakyThrows
    void getUserEmailPreferencesDistributionTest() {
        List<UserEmailPreferencesStatisticDto> emailPrefsStats = List.of(
            new UserEmailPreferencesStatisticDto(EmailPreference.LIKES, EmailPreferencePeriodicity.DAILY, 10L));
        String emailPrefsStatsJson = toJson(emailPrefsStats);
        String expectedRequestPath = "/user/email-preferences-distribution";
        String expectedRequestMethod = HttpMethod.GET.name();

        mockWebServer.enqueue(new MockResponse()
            .setBody(emailPrefsStatsJson)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        List<UserEmailPreferencesStatisticDto> actualResult = userRemoteClient.getUserEmailPreferencesDistribution();

        assertEquals(emailPrefsStats, actualResult);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
    }

    @Test
    @SneakyThrows
    void countActiveUsersTest() {
        Long activeUsersCount = 100L;
        String expectedRequestPath = "/user/count-active-users";
        String expectedRequestMethod = HttpMethod.GET.name();

        mockWebServer.enqueue(new MockResponse()
            .setBody(activeUsersCount.toString())
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        Long actualResult = userRemoteClient.countActiveUsers();

        assertEquals(activeUsersCount, actualResult);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
    }

    @Test
    @SneakyThrows
    void searchUserNotificationPreferenceTest() {
        EmailPreferenceDto emailPreferenceDto =
            new EmailPreferenceDto(10L, EmailPreference.LIKES, EmailPreferencePeriodicity.DAILY);
        Boolean searchResult = true;
        String emailPreferenceDtoJson = toJson(emailPreferenceDto);
        String expectedRequestPath = "/user-notification-preference/search";
        String expectedRequestMethod = HttpMethod.POST.name();

        mockWebServer.enqueue(new MockResponse()
            .setBody(searchResult.toString())
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        Boolean actualResult = userRemoteClient.searchUserNotificationPreference(emailPreferenceDto);

        assertEquals(searchResult, actualResult);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
        assertEquals(emailPreferenceDtoJson, recordedRequest.getBody().readUtf8());
    }

    @Test
    @SneakyThrows
    void checkIfTheUserIsOnlineTest() {
        Long userId = 1L;
        Boolean isOnline = true;
        String expectedRequestPath = "/user/isOnline/" + userId + "/";
        String expectedRequestMethod = HttpMethod.GET.name();

        mockWebServer.enqueue(new MockResponse()
            .setBody(isOnline.toString())
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        Boolean actualResult = userRemoteClient.checkIfTheUserIsOnline(userId);

        assertEquals(isOnline, actualResult);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
    }

    @Test
    @SneakyThrows
    void findAllByEmailPreferenceAndEmailPeriodicityTest() {
        String emailPreference = "DAILY";
        String periodicity = "WEEKLY";
        List<UserVO> users = List.of(ModelUtils.getUserVO());
        String usersJson = toJson(users);
        String expectedRequestPath =
            "/user/email?email-preference=" + emailPreference + "&email-periodicity=" + periodicity;
        String expectedRequestMethod = HttpMethod.GET.name();

        mockWebServer.enqueue(new MockResponse()
            .setBody(usersJson)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        List<UserVO> actualResult =
            userRemoteClient.findAllByEmailPreferenceAndEmailPeriodicity(emailPreference, periodicity);

        assertEquals(users, actualResult);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
        assertEquals(emailPreference, recordedRequest.getRequestUrl().queryParameter("email-preference"));
        assertEquals(periodicity, recordedRequest.getRequestUrl().queryParameter("email-periodicity"));
    }

    @Test
    @SneakyThrows
    void getUserRegistrationsByDateRangeTest() {
        LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 12, 31, 23, 59);
        DateGranularity granularity = DateGranularity.DAY;
        List<UserRegistrationStatisticDto> registrationStats =
            List.of(new UserRegistrationStatisticDto(startDate, 10L));
        String registrationStatsJson = toJson(registrationStats);
        String expectedRequestPath = "/user/registration-statistics?start-date=" + startDate + "&end-date=" + endDate
            + "&granularity=" + granularity.name();
        String expectedRequestMethod = HttpMethod.GET.name();

        mockWebServer.enqueue(new MockResponse()
            .setBody(registrationStatsJson)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        List<UserRegistrationStatisticDto> actualResult =
            userRemoteClient.getUserRegistrationsByDateRange(startDate, endDate, granularity);

        assertEquals(registrationStats, actualResult);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
        assertEquals(startDate.toString(), recordedRequest.getRequestUrl().queryParameter("start-date"));
        assertEquals(endDate.toString(), recordedRequest.getRequestUrl().queryParameter("end-date"));
        assertEquals(granularity.name(), recordedRequest.getRequestUrl().queryParameter("granularity"));
    }

    @Test
    @SneakyThrows
    void getActivatedUsersIdsTest() {
        List<Long> ids = List.of(1L, 2L, 3L);
        List<Long> activatedIds = List.of(1L, 3L);
        String activatedIdsJson = toJson(activatedIds);
        String expectedRequestPath =
            "/user/activated-ids?ids=" + String.join("&ids=", ids.stream().map(String::valueOf).toArray(String[]::new));
        String expectedRequestMethod = HttpMethod.GET.name();

        mockWebServer.enqueue(new MockResponse()
            .setBody(activatedIdsJson)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        List<Long> actualResult = userRemoteClient.getActivatedUsersIds(ids);

        assertEquals(activatedIds, actualResult);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
        assertNotNull(recordedRequest.getRequestUrl().queryParameter("ids"));
    }

    @Test
    @SneakyThrows
    void getActivatedUsersIdsWithNullListTest() {
        List<Long> activatedIds = List.of(1L, 2L, 3L);
        String activatedIdsJson = toJson(activatedIds);
        String expectedRequestPath = "/user/activated-ids";
        String expectedRequestMethod = HttpMethod.GET.name();

        mockWebServer.enqueue(new MockResponse()
            .setBody(activatedIdsJson)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        List<Long> actualResult = userRemoteClient.getActivatedUsersIds(null);

        assertEquals(activatedIds, actualResult);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
    }

    @Test
    @SneakyThrows
    void findNotDeactivatedByIdAdvancedTest() {
        Long id = 1L;
        UserVOAdvancedDto userVOAdvanced = ModelUtils.getUserVOAdvancedDto();
        String userVOAdvancedJson = toJson(userVOAdvanced);
        String expectedRequestPath = "/user/findNotDeactivatedByIdAdvanced?id=" + id;
        String expectedRequestMethod = HttpMethod.GET.name();

        mockWebServer.enqueue(new MockResponse()
            .setBody(userVOAdvancedJson)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        Optional<UserVOAdvancedDto> actualResult = userRemoteClient.findNotDeactivatedByIdAdvanced(id);

        assertTrue(actualResult.isPresent());
        UserVOAdvancedDto actualUserVOAdvanced = actualResult.get();
        assertEquals(userVOAdvanced, actualUserVOAdvanced);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
        assertNotNull(recordedRequest.getRequestUrl().queryParameter(idQueryParam));
        assertEquals(id.toString(), recordedRequest.getRequestUrl().queryParameter(idQueryParam));
    }

    @Test
    @SneakyThrows
    void getAllSocialNetworkImagesRemoteTest() {
        Pageable pageable = PageRequest.of(0, 10);
        PageableDto<SocialNetworkImageResponseDTO> pageableDto = new PageableDto<>(
            List.of(ModelUtils.getSocialNetworkImageResponseDTO()),
            1L,
            0,
            1);
        String pageableDtoJson = toJson(pageableDto);
        String expectedRequestPath = "/management/socialnetworkimages/get-all-remote?page=" + pageable.getPageNumber()
            + "&size=" + pageable.getPageSize();
        String expectedRequestMethod = HttpMethod.GET.name();

        mockWebServer.enqueue(new MockResponse()
            .setBody(pageableDtoJson)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        PageableDto<SocialNetworkImageResponseDTO> actualResult =
            userRemoteClient.getAllSocialNetworkImagesRemote(pageable);

        assertEquals(pageableDto, actualResult);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
        assertEquals(String.valueOf(pageable.getPageNumber()),
            recordedRequest.getRequestUrl().queryParameter(pageQueryParam));
        assertEquals(String.valueOf(pageable.getPageSize()),
            recordedRequest.getRequestUrl().queryParameter(pageSizeQueryParam));
    }

    @Test
    @SneakyThrows
    void saveSocialImageRemoteTest() {
        SocialNetworkImageRequestDTO requestDto = new SocialNetworkImageRequestDTO("image path", "host path");
        SocialNetworkImageResponseDTO responseDto = ModelUtils.getSocialNetworkImageResponseDTO();
        MultipartFile file = new MockMultipartFile("name", "content".getBytes());
        String responseDtoJson = toJson(responseDto);
        String expectedRequestPath = "/management/socialnetworkimages/save-remote";
        String expectedRequestMethod = HttpMethod.POST.name();

        mockWebServer.enqueue(new MockResponse()
            .setBody(responseDtoJson)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        SocialNetworkImageResponseDTO actualResult = userRemoteClient.saveSocialImageRemote(requestDto, file);

        assertEquals(responseDto, actualResult);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
        assertTrue(recordedRequest.getHeader(HttpHeaders.CONTENT_TYPE).startsWith(MediaType.MULTIPART_FORM_DATA_VALUE));
    }

    @Test
    @SneakyThrows
    void deleteSocialImageTest() {
        Long id = 1L;
        String expectedRequestPath = "/management/socialnetworkimages/delete?id=" + id;
        String expectedRequestMethod = HttpMethod.DELETE.name();

        mockWebServer.enqueue(new MockResponse()
            .setBody(id.toString())
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        Long actualResult = userRemoteClient.deleteSocialImage(id);

        assertEquals(id, actualResult);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
        assertNotNull(recordedRequest.getRequestUrl().queryParameter(idQueryParam));
        assertEquals(id.toString(), recordedRequest.getRequestUrl().queryParameter(idQueryParam));
    }

    @Test
    @SneakyThrows
    void deleteAllImagesTest() {
        List<Long> listId = List.of(1L, 2L, 3L);
        String listIdJson = toJson(listId);
        String expectedRequestPath = "/management/socialnetworkimages/deleteAll";
        String expectedRequestMethod = HttpMethod.DELETE.name();

        mockWebServer.enqueue(new MockResponse()
            .setBody(listIdJson)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        List<Long> actualResult = userRemoteClient.deleteAllImages(listId);

        assertEquals(listId, actualResult);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
        assertEquals(listIdJson, recordedRequest.getBody().readUtf8());
    }

    @Test
    @SneakyThrows
    void getEcoNewsByIdTest() {
        Long id = 1L;
        SocialNetworkImageResponseDTO responseDto = ModelUtils.getSocialNetworkImageResponseDTO();
        String responseDtoJson = toJson(responseDto);
        String expectedRequestPath = "/management/socialnetworkimages/find?id=" + id;
        String expectedRequestMethod = HttpMethod.GET.name();

        mockWebServer.enqueue(new MockResponse()
            .setBody(responseDtoJson)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        SocialNetworkImageResponseDTO actualResult = userRemoteClient.getEcoNewsById(id);

        assertEquals(responseDto, actualResult);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
        assertNotNull(recordedRequest.getRequestUrl().queryParameter(idQueryParam));
        assertEquals(id.toString(), recordedRequest.getRequestUrl().queryParameter(idQueryParam));
    }

    @Test
    @SneakyThrows
    void getAllLanguagesTest() {
        List<LanguageDTO> languages = List.of(ModelUtils.getLanguageDTO());
        String languagesJson = toJson(languages);
        String expectedRequestPath = "/lang";
        String expectedRequestMethod = HttpMethod.GET.name();

        mockWebServer.enqueue(new MockResponse()
            .setBody(languagesJson)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        List<LanguageDTO> actualResult = userRemoteClient.getAllLanguages();

        assertEquals(languages, actualResult);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
    }

    @Test
    @SneakyThrows
    void findLanguageByCodeTest() {
        String code = "en";
        LanguageDTO languageDto = ModelUtils.getLanguageDTO();
        String languageDtoJson = toJson(languageDto);
        String expectedRequestPath = "/lang/codes/" + code;
        String expectedRequestMethod = HttpMethod.GET.name();

        mockWebServer.enqueue(new MockResponse()
            .setBody(languageDtoJson)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        LanguageDTO actualResult = userRemoteClient.findLanguageByCode(code);

        assertEquals(languageDto, actualResult);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
    }

    @Test
    @SneakyThrows
    void findLanguageByCodeWhenLanguageNotFoundTest() {
        String code = "en";
        String expectedRequestPath = "/lang/codes/" + code;
        String expectedRequestMethod = HttpMethod.GET.name();

        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        assertThrows(
            LanguageNotFoundException.class,
            () -> userRemoteClient.findLanguageByCode(code));

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
    }

    @Test
    @SneakyThrows
    void findAllLanguageCodesTest() {
        List<String> languageCodes = List.of("en", "uk", "de");
        String languageCodesJson = toJson(languageCodes);
        String expectedRequestPath = "/lang/codes";
        String expectedRequestMethod = HttpMethod.GET.name();

        mockWebServer.enqueue(new MockResponse()
            .setBody(languageCodesJson)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        List<String> actualResult = userRemoteClient.findAllLanguageCodes();

        assertEquals(languageCodes, actualResult);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
    }

    @Test
    @SneakyThrows
    void updateSocialImageTest() {
        SocialNetworkImageResponseDTO responseDto = ModelUtils.getSocialNetworkImageResponseDTO();
        MultipartFile file = new MockMultipartFile("name", "content".getBytes());
        String expectedRequestPath = "/management/socialnetworkimages/";
        String expectedRequestMethod = HttpMethod.PUT.name();

        mockWebServer.enqueue(new MockResponse()
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        userRemoteClient.updateSocialImage(responseDto, file);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
        assertTrue(recordedRequest.getHeader(HttpHeaders.CONTENT_TYPE).startsWith(MediaType.MULTIPART_FORM_DATA_VALUE));
    }

    @Test
    @SneakyThrows
    void findAllByEmailInTest() {
        List<String> emails = List.of("email1@test.com", "email2@test.com");
        List<UserVO> users = List.of(ModelUtils.getUserVO());
        String usersJson = toJson(users);
        String expectedRequestPath = "/user/email/findAll?emails=" + String.join("&emails=", emails);
        System.out.println("expectedRequestPath: " + expectedRequestPath);
        String expectedRequestMethod = HttpMethod.GET.name();

        mockWebServer.enqueue(new MockResponse()
            .setBody(usersJson)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        List<UserVO> actualResult = userRemoteClient.findAllByEmailIn(emails);

        assertEquals(users, actualResult);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
        assertNotNull(recordedRequest.getRequestUrl().queryParameter("emails"));
    }

    @Test
    @SneakyThrows
    void userExistsByEmailTest() {
        String email = "email@email.com";
        String expectedRequestPath = "/user/findNotDeactivatedByEmail?email=" + email;
        UserVO userVO = ModelUtils.getUserVO();
        String userVOJson = toJson(userVO);

        mockWebServer.enqueue(new MockResponse()
            .setBody(userVOJson)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        boolean actualResult = userRemoteClient.userExistsByEmail(email);

        assertTrue(actualResult);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(HttpMethod.GET.name(), recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
        assertEquals(email, recordedRequest.getRequestUrl().queryParameter(emailQueryParam));
    }

    @Test
    @SneakyThrows
    void findUserEmailsByUserIdsTest() {
        List<Long> userIds = List.of(1L, 2L, 3L);
        List<UserEmailDto> userEmails = List.of(
            new UserEmailDto(1L, "email1"),
            new UserEmailDto(2L, "email2"));
        String userEmailsJson = toJson(userEmails);
        String expectedRequestPath = "/user/email/findByIds?userIds="
            + String.join("&userIds=", userIds.stream().map(String::valueOf).toArray(String[]::new));
        String expectedRequestMethod = HttpMethod.GET.name();

        mockWebServer.enqueue(new MockResponse()
            .setBody(userEmailsJson)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        List<UserEmailDto> actualResult = userRemoteClient.findUserEmailsByUserIds(userIds);

        assertEquals(userEmails, actualResult);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(expectedRequestMethod, recordedRequest.getMethod());
        assertEquals(expectedRequestPath, recordedRequest.getPath());
        assertNotNull(recordedRequest.getRequestUrl().queryParameter("userIds"));
    }

    private String toJson(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
