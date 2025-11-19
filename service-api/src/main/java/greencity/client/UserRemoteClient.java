package greencity.client;

import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.emailpreference.EmailPreferenceDto;
import greencity.dto.language.LanguageDTO;
import greencity.dto.socialnetwork.SocialNetworkImageResponseDTO;
import greencity.dto.socialnetwork.SocialNetworkImageRequestDTO;
import greencity.dto.user.UserActivationDto;
import greencity.dto.user.UserDeactivationReasonDto;
import greencity.dto.user.UserEmailPreferencesStatisticDto;
import greencity.dto.user.UserRegistrationStatisticDto;
import greencity.dto.user.UserRoleDto;
import greencity.dto.user.UserRoleStatisticDto;
import greencity.dto.user.UserVO;
import greencity.dto.user.UserVOAdvancedDto;
import greencity.enums.DateGranularity;
import greencity.enums.EmailPreference;
import greencity.enums.EmailPreferencePeriodicity;
import greencity.exception.exceptions.LanguageNotFoundException;
import greencity.exception.exceptions.NotFoundException;
import greencity.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserRemoteClient {
    private final WebClient webClient;
    private final UserService userService;

    private static final String PAGE_QUERY_PARAM = "page";
    private static final String PAGE_SIZE_QUERY_PARAM = "size";
    private static final String USER_EMAIL_QUERY_PARAM = "email";
    private static final String ID_QUERY_PARAM = "id";
    private static final String EMAIL_QUERY_PARAM = "email";

    public UserRemoteClient(WebClient webClient, @Lazy UserService userService) {
        this.webClient = webClient;
        this.userService = userService;
    }

    /**
     * Method for uploading files.
     *
     * @param files files to save.
     * @return urls of the saved files.
     */
    public List<String> uploadAllFiles(List<MultipartFile> files) {
        MultipartFile[] multipartFiles = files.toArray(new MultipartFile[0]);

        return webClient.post()
            .uri("/files")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(multipartInserter(multipartFiles))
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<String>>() {
            })
            .block();
    }

    /**
     * Method for uploading a file.
     *
     * @param file file to save.
     * @return url of the saved file.
     */
    public String uploadFile(MultipartFile file) {
        return webClient.post()
            .uri("/files/single")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(multipartInserter(file))
            .retrieve()
            .bodyToMono(String.class)
            .block();
    }

    /**
     * Method for deleting files.
     *
     * @param paths urls of files to delete.
     */
    public void deleteAllFiles(List<String> paths) {
        webClient.method(HttpMethod.DELETE)
            .uri("/files")
            .bodyValue(paths)
            .retrieve()
            .bodyToMono(Void.class)
            .block();
    }

    /**
     * Method for deleting files.
     *
     * @param path urls of files to delete.
     */
    public void deleteFile(String path) {
        webClient.method(HttpMethod.DELETE)
            .uri(uriBuilder -> uriBuilder
                .path("/files/single")
                .queryParam("path", path)
                .build())
            .retrieve()
            .bodyToMono(Void.class)
            .block();
    }

    /**
     * Finds {@link UserVO} by {@link UserVO}'s Email.
     *
     * @param email {@link UserVO}'s Email.
     * @return {@link Optional} of {@link UserVO}.
     */
    public Optional<UserVO> findByEmail(String email) {
        UserVO userVO = webClient.get()
            .uri(uriBuilder -> uriBuilder.path("/user/findByEmail")
                .queryParam(USER_EMAIL_QUERY_PARAM, email)
                .build())
            .retrieve()
            .bodyToMono(UserVO.class)
            .block();
        if (userVO != null) {
            userService.fillGreenCityInfoInUsers(List.of(userVO));
        }
        return Optional.ofNullable(userVO);
    }

    /**
     * Updates user role.
     *
     * @param id   user id
     * @param body map containing role information
     * @return {@link Optional} of updated {@link UserRoleDto}
     */
    public Optional<UserRoleDto> updateUserRole(Long id, Map<String, String> body) {
        String email = userService.findById(id).getEmail();
        UserRoleDto updatedRole = webClient.patch()
            .uri(uriBuilder -> uriBuilder.path("/user/role")
                .queryParam(EMAIL_QUERY_PARAM, email)
                .build())
            .bodyValue(body)
            .retrieve()
            .bodyToMono(UserRoleDto.class)
            .block();
        return Optional.ofNullable(updatedRole);
    }

    /**
     * Gets user roles distribution.
     *
     * @return List of {@link UserRoleStatisticDto}
     */
    public List<UserRoleStatisticDto> getUserRolesDistribution() {
        return webClient.get()
            .uri("/user/roles-distribution")
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<UserRoleStatisticDto>>() {
            })
            .block();
    }

    /**
     * Gets user email preferences distribution.
     *
     * @return List of {@link UserEmailPreferencesStatisticDto}
     */
    public List<UserEmailPreferencesStatisticDto> getUserEmailPreferencesDistribution() {
        return webClient.get()
            .uri("/user/email-preferences-distribution")
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<UserEmailPreferencesStatisticDto>>() {
            })
            .block();
    }

    /**
     * Check is user notification preference exists by params in EmailPreferenceDto.
     *
     * @param emailPreferenceDto email preference data
     * @return boolean of whether UserNotificationPreference exists
     */
    public Boolean searchUserNotificationPreference(EmailPreferenceDto emailPreferenceDto) {
        return webClient.post()
            .uri("/user-notification-preference/search")
            .bodyValue(emailPreferenceDto)
            .retrieve()
            .bodyToMono(Boolean.class)
            .block();
    }

    /**
     * The method checks by id if a {@link UserVO} is online.
     *
     * @param userId id of the user
     * @return boolean of whether user by that id is online.
     */
    public Boolean checkIfTheUserIsOnline(Long userId) {
        String email = userService.findById(userId).getEmail();
        return webClient.get()
            .uri(uriBuilder -> uriBuilder.path("/user/isOnline")
                .queryParam(EMAIL_QUERY_PARAM, email)
                .build())
            .retrieve()
            .bodyToMono(Boolean.class)
            .block();
    }

    /**
     * Find users by email preference and email periodicity.
     *
     * @param emailPreference user's email preference.
     * @param periodicity     email periodicity.
     * @return list of {@link UserVO}
     */
    public List<UserVO> findAllByEmailPreferenceAndEmailPeriodicity(
        EmailPreference emailPreference, EmailPreferencePeriodicity periodicity) {
        List<UserVO> users = webClient.get()
            .uri(uriBuilder -> uriBuilder.path("/user/email")
                .queryParam("email-preference", emailPreference.name())
                .queryParam("email-periodicity", periodicity.name())
                .build())
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<UserVO>>() {
            })
            .block();
        if (users != null) {
            userService.fillGreenCityInfoInUsers(users);
        }
        return users;
    }

    /**
     * Method to get list of dates and counts of registered users.
     *
     * @param startDate   {@code LocalDateTime} startDate.
     * @param endDate     {@code LocalDateTime} endDate.
     * @param granularity {@link DateGranularity} (eg. day, week, month, year).
     * @return {@link List} of {@link UserRegistrationStatisticDto}.
     */
    public List<UserRegistrationStatisticDto> getUserRegistrationsByDateRange(
        LocalDateTime startDate, LocalDateTime endDate, DateGranularity granularity) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder.path("/user/registration-statistics")
                .queryParam("start-date", startDate)
                .queryParam("end-date", endDate)
                .queryParam("granularity", granularity.name())
                .build())
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<UserRegistrationStatisticDto>>() {
            })
            .block();
    }

    /**
     * Finds {@link UserVOAdvancedDto} by {@link UserVOAdvancedDto}'s email.
     *
     * @param email {@link UserVOAdvancedDto}'s email.
     * @return {@link Optional} of {@link UserVOAdvancedDto}.
     */
    public Optional<UserVOAdvancedDto> findByEmailAdvanced(String email) {
        UserVOAdvancedDto userVO = webClient.get()
            .uri(uriBuilder -> uriBuilder.path("/user/findByEmailAdvanced")
                .queryParam(USER_EMAIL_QUERY_PARAM, email)
                .build())
            .retrieve()
            .bodyToMono(UserVOAdvancedDto.class)
            .block();
        if (userVO != null) {
            userService.fillGreenCityInfoInUsers(List.of(userVO));
        }
        return Optional.ofNullable(userVO);
    }

    /**
     * Method that returns page with all {@link SocialNetworkImageResponseDTO}.
     *
     * @param pageable {@link Pageable}.
     * @return {@link PageableDto} of {@link SocialNetworkImageResponseDTO}.
     */
    public PageableDto<SocialNetworkImageResponseDTO> getAllSocialNetworkImagesRemote(Pageable pageable) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder.path("/management/socialnetworkimages/get-all-remote")
                .queryParam(PAGE_QUERY_PARAM, pageable.getPageNumber())
                .queryParam(PAGE_SIZE_QUERY_PARAM, pageable.getPageSize())
                .build())
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<PageableDto<SocialNetworkImageResponseDTO>>() {
            })
            .block();
    }

    /**
     * Method for creating SocialNetworkImage.
     *
     * @param socialNetworkImageRequestDTO dto for creating a SocialNetworkImage
     *                                     entity.
     * @param file                         of {@link MultipartFile}
     * @return {@link SocialNetworkImageResponseDTO}
     */

    public SocialNetworkImageResponseDTO saveSocialImageRemote(
        SocialNetworkImageRequestDTO socialNetworkImageRequestDTO, MultipartFile file) {
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("socialNetworkImageRequestDTO",
            socialNetworkImageRequestDTO, MediaType.APPLICATION_JSON);

        if (file != null) {
            bodyBuilder.part("file", file.getResource());
        }
        return webClient.post()
            .uri("/management/socialnetworkimages/save-remote")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
            .retrieve()
            .bodyToMono(SocialNetworkImageResponseDTO.class)
            .block();
    }

    /**
     * Method which deletes SocialNetworkImageVO by given id.
     *
     * @param id of Social Network Images
     * @return {@link Long} id og the deleted image
     */
    public Long deleteSocialImage(Long id) {
        return webClient.delete()
            .uri(uriBuilder -> uriBuilder.path("/management/socialnetworkimages/delete")
                .queryParam(ID_QUERY_PARAM, id)
                .build())
            .retrieve()
            .bodyToMono(Long.class)
            .block();
    }

    /**
     * Method for deleting SocialNetworkImageVO by given id.
     *
     * @param listId list of IDs.
     * @return {@link List} of the deleted image ids
     */
    public List<Long> deleteAllImages(List<Long> listId) {
        return webClient.method(HttpMethod.DELETE)
            .uri("/management/socialnetworkimages/deleteAll")
            .bodyValue(listId)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<Long>>() {
            })
            .block();
    }

    /**
     * Method for getting socialnetworkimages by id.
     *
     * @param id of Eco New
     * @return {@link SocialNetworkImageResponseDTO} instance.
     */
    public SocialNetworkImageResponseDTO getEcoNewsById(Long id) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder.path("/management/socialnetworkimages/find")
                .queryParam(ID_QUERY_PARAM, id)
                .build())
            .retrieve()
            .bodyToMono(SocialNetworkImageResponseDTO.class)
            .block();
    }

    /**
     * Method to get all languages as {@link LanguageDTO}.
     *
     * @return {@link List} of {@link LanguageDTO}
     */
    public List<LanguageDTO> getAllLanguages() {
        return webClient.get()
            .uri("/lang")
            .retrieve()
            .bodyToFlux(LanguageDTO.class)
            .toStream()
            .toList();
    }

    /**
     * Find language {@link LanguageDTO} by code.
     *
     * @param code language code
     * @return language {@link LanguageDTO}
     */
    public LanguageDTO findLanguageByCode(String code) throws NotFoundException {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder.path("/lang/codes/{code}").build(code))
            .retrieve()
            .onStatus(httpStatusCode -> httpStatusCode.isSameCodeAs(HttpStatus.NOT_FOUND),
                clientResponse -> Mono
                    .error(new LanguageNotFoundException(ErrorMessage.LANGUAGE_NOT_FOUND_BY_CODE + code)))
            .bodyToMono(LanguageDTO.class)
            .block();
    }

    /**
     * Method to get all language codes.
     *
     * @return {@link List} of {@link String} language codes
     */
    public List<String> findAllLanguageCodes() {
        return webClient.get()
            .uri("/lang/codes")
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<String>>() {
            })
            .block();
    }

    /**
     * Method which updates SocialNetworkImage.
     *
     * @param socialNetworkImageResponseDTO of
     *                                      {@link SocialNetworkImageResponseDTO}.
     * @param file                          of {@link MultipartFile}.
     */
    public void updateSocialImage(SocialNetworkImageResponseDTO socialNetworkImageResponseDTO, MultipartFile file) {
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("socialNetworkImageResponseDTO",
            socialNetworkImageResponseDTO, MediaType.APPLICATION_JSON);

        if (file != null) {
            bodyBuilder.part("file", file.getResource());
        }

        webClient.put().uri("/management/socialnetworkimages/").contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(bodyBuilder.build())).retrieve()
            .bodyToMono(Void.class).block();
    }

    /**
     * Find list of {@link UserVO}'s by emails.
     *
     * @param emails user emails.
     * @return list of {@link UserVO}.
     */
    public List<UserVO> findAllByEmailIn(List<String> emails) {
        String emailsListQueryParam = "emails";
        List<UserVO> users = webClient.get()
            .uri(uriBuilder -> uriBuilder.path("/user/email/findAll")
                .queryParam(emailsListQueryParam, emails)
                .build())
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<UserVO>>() {
            })
            .block();
        if (users != null) {
            userService.fillGreenCityInfoInUsers(users);
        }
        return users;
    }

    /**
     * Method to check whether user exists by email.
     *
     * @param email user's email
     * @return boolean of whether user by that email exists
     */
    public boolean userExistsByEmail(String email) {
        Optional<UserVO> userVOOptional = findByEmail(email);
        return userVOOptional.isPresent();
    }

    /**
     * Method to get user language by email.
     *
     * @param email user's email
     * @return user's language
     */
    public String findUserLanguageByEmail(String email) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder.path("/user/greencity/lang")
                .queryParam(EMAIL_QUERY_PARAM, email)
                .build())
            .retrieve()
            .bodyToMono(String.class)
            .block();
    }

    /**
     * Sends an email about reason of deactivation.
     *
     * @param notification {@link UserDeactivationReasonDto} - notification details
     */
    public void sendReasonOfDeactivation(UserDeactivationReasonDto notification) {
        webClient.post()
            .uri("/email/sendReasonOfDeactivation")
            .bodyValue(notification)
            .retrieve()
            .bodyToMono(Void.class)
            .block();
    }

    /**
     * Sends an email.
     *
     * @param notification {@link UserActivationDto} - notification details
     */
    public void sendMessageOfActivation(UserActivationDto notification) {
        webClient.post()
            .uri("/email/sendMessageOfActivation")
            .bodyValue(notification)
            .retrieve()
            .bodyToMono(Void.class)
            .block();
    }

    private BodyInserters.MultipartInserter multipartInserter(MultipartFile... multipartFiles) {
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();

        for (MultipartFile multipartFile : multipartFiles) {
            multipartBodyBuilder.part("file", multipartFile.getResource());
        }

        return BodyInserters.fromMultipartData(multipartBodyBuilder.build());
    }
}
