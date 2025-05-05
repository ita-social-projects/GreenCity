package greencity.client;

import greencity.dto.PageableDto;
import greencity.dto.emailpreference.EmailPreferenceDto;
import greencity.dto.socialnetwork.SocialNetworkImageResponseDTO;
import greencity.dto.socialnetwork.SocialNetworkImageRequestDTO;
import greencity.dto.user.UserEmailPreferencesStatisticDto;
import greencity.dto.user.UserNotificationPreferenceVO;
import greencity.dto.user.UserRegistrationStatisticDto;
import greencity.dto.user.UserRoleDto;
import greencity.dto.user.UserRoleStatisticDto;
import greencity.dto.user.UserStatusDto;
import greencity.dto.user.UserStatusStatisticDto;
import greencity.dto.user.UserVO;
import greencity.dto.user.UserVOAdvancedDto;
import greencity.enums.DateGranularity;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserRemoteClient {
    private final WebClient webClient;

    private static final String PAGE_QUERY_PARAM = "page";
    private static final String PAGE_SIZE_QUERY_PARAM = "size";
    private static final String USER_EMAIL_QUERY_PARAM = "email";
    private static final String ID_QUERY_PARAM = "id";

    /**
     * Finds {@link UserVO} that is not 'DEACTIVATED' by {@link UserVO}'s Email.
     *
     * @param email {@link UserVO}'s Email.
     * @return {@link Optional} of {@link UserVO}.
     */
//    public Optional<UserVO> findNotDeactivatedByEmail(String email) {
//        String path = "/user/findNotDeactivatedByEmail";
//        UserVO userVO = webClient.get()
//            .uri(uriBuilder -> uriBuilder.path(path)
//                .queryParam(USER_EMAIL_QUERY_PARAM, email)
//                .build())
//            .retrieve()
//            .bodyToMono(UserVO.class)
//            .block();
//        return Optional.ofNullable(userVO);
//    }

    public Optional<UserVO> findNotDeactivatedByEmail(String email) {
        String path = "/user/findNotDeactivatedByEmailRemote";
        UserVO userVO = webClient.get()
                .uri(uriBuilder -> uriBuilder.path(path)
                        .queryParam(USER_EMAIL_QUERY_PARAM, email)
                        .build())
                .retrieve()
                .bodyToMono(UserVO.class)
                .block();
        return Optional.ofNullable(userVO);
    }

    /**
     * Method that allow you to find not 'DEACTIVATED' {@link UserVO} by id.
     *
     * @param id - {@link UserVO}'s id
     * @return {@link Optional} of found {@link UserVO}.
     */
//    public Optional<UserVO> findNotDeactivatedById(Long id) {
//        String path = "/user/findNotDeactivatedById";
//        UserVO userVO = webClient.get()
//            .uri(uriBuilder -> uriBuilder.path(path)
//                .queryParam(ID_QUERY_PARAM, id)
//                .build())
//            .retrieve()
//            .bodyToMono(UserVO.class)
//            .block();
//        return Optional.ofNullable(userVO);
//    }

    public Optional<UserVO> findNotDeactivatedById(Long id) {
        String path = "/user/findNotDeactivatedByIdRemote";
        UserVO userVO = webClient.get()
                .uri(uriBuilder -> uriBuilder.path(path)
                        .queryParam(ID_QUERY_PARAM, id)
                        .build())
                .retrieve()
                .bodyToMono(UserVO.class)
                .block();
        return Optional.ofNullable(userVO);
    }

    /**
     * Updates user status.
     *
     * @param userStatusDto user status data
     * @return {@link Optional} of updated {@link UserStatusDto}
     */
    public Optional<UserStatusDto> updateUserStatus(UserStatusDto userStatusDto) {
        String path = "/user/status";
        UserStatusDto updatedStatus = webClient.patch()
            .uri(path)
            .bodyValue(userStatusDto)
            .retrieve()
            .bodyToMono(UserStatusDto.class)
            .block();
        return Optional.ofNullable(updatedStatus);
    }

    /**
     * Updates user role.
     *
     * @param id   user id
     * @param body map containing role information
     * @return {@link Optional} of updated {@link UserRoleDto}
     */
    public Optional<UserRoleDto> updateUserRole(Long id, Map<String, String> body) {
        String path = "/user/{id}/role";
        UserRoleDto updatedRole = webClient.patch()
            .uri(uriBuilder -> uriBuilder.path(path).build(id))
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
        String path = "/user/roles-distribution";
        return webClient.get()
            .uri(path)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<UserRoleStatisticDto>>() {
            })
            .block();
    }

    /**
     * Gets user statuses distribution.
     *
     * @return List of {@link UserStatusStatisticDto}
     */
    public List<UserStatusStatisticDto> getUserStatusesDistribution() {
        String path = "/user/statuses-distribution";
        return webClient.get()
            .uri(path)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<UserStatusStatisticDto>>() {
            })
            .block();
    }

    /**
     * Gets user email preferences distribution.
     *
     * @return List of {@link UserEmailPreferencesStatisticDto}
     */
    public List<UserEmailPreferencesStatisticDto> getUserEmailPreferencesDistribution() {
        String path = "/user/email-preferences-distribution";
        return webClient.get()
            .uri(path)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<UserEmailPreferencesStatisticDto>>() {
            })
            .block();
    }

    /**
     * Counts active users.
     *
     * @return count of active users
     */
    public Long countActiveUsers() {
        String path = "/user/count-active-users";
        return webClient.get()
            .uri(path)
            .retrieve()
            .bodyToMono(Long.class)
            .block();
    }

    /**
     * Get user notification preferences by user id.
     *
     * @param id user id
     * @return list of {@link UserNotificationPreferenceVO}
     */
    public List<UserNotificationPreferenceVO> findAllUserNotificationPreferencesByUserId(Long id) {
        String path = "/user-notification-preference";
        return webClient.get()
            .uri(uriBuilder -> uriBuilder.path(path)
                .queryParam(ID_QUERY_PARAM, id)
                .build())
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<UserNotificationPreferenceVO>>() {
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
        String path = "/user-notification-preference/search";
        return webClient.post()
            .uri(path)
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
        String path = "/user/isOnline/{userId}/";
        return webClient.get()
            .uri(uriBuilder -> uriBuilder.path(path).build(userId))
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
        String emailPreference, String periodicity) {
        String path = "/user/email";
        return webClient.get()
            .uri(uriBuilder -> uriBuilder.path(path)
                .queryParam("email-preference", emailPreference)
                .queryParam("email-periodicity", periodicity)
                .build())
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<UserVO>>() {
            })
            .block();
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
        String path = "/user/registration-statistics";
        return webClient.get()
            .uri(uriBuilder -> uriBuilder.path(path)
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
     * Retrieves the list of IDs of users who have the user status set to
     * {@code ACTIVATED}.
     *
     * @param ids a list of user IDs to check; may be {@code null} or empty to
     *            indicate all users
     * @return a list of {@code Long} values representing the IDs of all activated
     *         users
     */
    public List<Long> getActivatedUsersIds(List<Long> ids) {
        String path = "/user/activated-ids";
        return webClient.get()
            .uri(uriBuilder -> {
                uriBuilder = uriBuilder.path(path);
                if (ids != null && !ids.isEmpty()) {
                    uriBuilder = uriBuilder.queryParam("ids", ids);
                }
                return uriBuilder.build();
            })
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<Long>>() {
            })
            .block();
    }

    /**
     * Finds {@link UserVOAdvancedDto} that is not 'DEACTIVATED' by
     * {@link UserVOAdvancedDto}'s id.
     *
     * @param id {@link UserVOAdvancedDto}'s id.
     * @return {@link Optional} of {@link UserVOAdvancedDto}.
     */
    public Optional<UserVOAdvancedDto> findNotDeactivatedByIdAdvanced(Long id) {
        String path = "/user/findNotDeactivatedByIdAdvanced";

        UserVOAdvancedDto userVO = webClient.get()
            .uri(uriBuilder -> uriBuilder.path(path)
                .queryParam(ID_QUERY_PARAM, id)
                .build())
            .retrieve()
            .bodyToMono(UserVOAdvancedDto.class)
            .block();

        return Optional.ofNullable(userVO);
    }

    /**
     * Method that returns page with all {@link SocialNetworkImageResponseDTO}.
     *
     * @param pageable {@link Pageable}.
     * @return {@link PageableDto} of {@link SocialNetworkImageResponseDTO}.
     */
    public PageableDto<SocialNetworkImageResponseDTO> getAllSocialNetworkImagesRemote(Pageable pageable) {
        String path = "/management/socialnetworkimages/get-all-remote";

        return webClient.get()
            .uri(uriBuilder -> uriBuilder.path(path)
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
        String path = "/management/socialnetworkimages/save-remote";

        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("socialNetworkImageRequestDTO",
            socialNetworkImageRequestDTO, MediaType.APPLICATION_JSON);

        if (file != null) {
            bodyBuilder.part("file", file.getResource());
        }
        return webClient.post()
            .uri(path)
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
        String path = "/management/socialnetworkimages/delete";

        return webClient.delete()
            .uri(uriBuilder -> uriBuilder.path(path)
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
        String path = "/management/socialnetworkimages/deleteAll";

        return webClient.method(HttpMethod.DELETE)
            .uri(path)
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
        String path = "/management/socialnetworkimages/find";

        return webClient.get()
            .uri(uriBuilder -> uriBuilder.path(path)
                .queryParam(ID_QUERY_PARAM, id)
                .build())
            .retrieve()
            .bodyToMono(SocialNetworkImageResponseDTO.class)
            .block();
    }

    /**
     * Method which updates SocialNetworkImage.
     *
     * @param socialNetworkImageResponseDTO of
     *                                      {@link SocialNetworkImageResponseDTO}.
     * @param file                          of {@link MultipartFile}.
     */
    /*
     * public void updateSocialImage( SocialNetworkImageResponseDTO
     * socialNetworkImageResponseDTO, MultipartFile file) {
     *
     * String path = "/management/socialnetworkimages/";
     *
     * BodyInserters.MultipartInserter multipartInserter =
     * multipartInserter("socialNetworkImageResponseDTO");
     *
     * MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
     * bodyBuilder.part("socialNetworkImageResponseDTO",
     * socialNetworkImageResponseDTO, MediaType.APPLICATION_JSON);
     *
     * if (file != null) { bodyBuilder.part("file", file.getResource()); }
     *
     * webClient.put() .uri(path) .contentType(MediaType.MULTIPART_FORM_DATA)
     * .body(BodyInserters.fromMultipartData(bodyBuilder.build())) .retrieve()
     * .bodyToMono(Void.class) .block(); }
     */

    private BodyInserters.MultipartInserter multipartInserter(String partName, MultipartFile... multipartFiles) {
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();

        for (MultipartFile multipartFile : multipartFiles) {
            multipartBodyBuilder.part(partName, multipartFile.getResource());
        }

        return BodyInserters.fromMultipartData(multipartBodyBuilder.build());
    }
}
