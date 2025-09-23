package greencity.service;

import com.google.maps.model.AddressComponentType;
import com.google.maps.model.AddressType;
import com.google.maps.model.GeocodingResult;
import greencity.client.UserRemoteClient;
import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.dto.PageInfoDto;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.PageableDetailedDto;
import greencity.dto.location.UserLocationDto;
import greencity.dto.socialnetwork.SocialNetworkVO;
import greencity.dto.user.CreateGreenCityUserDto;
import greencity.dto.user.GreenCityUserProfileDtoResponse;
import greencity.dto.user.UpdateUserCredoDto;
import greencity.dto.user.UserActivationDto;
import greencity.dto.user.UserAddRatingDto;
import greencity.dto.user.UserAddRatingExternalDto;
import greencity.dto.user.UserCityDto;
import greencity.dto.user.GreenCityUserInfoDto;
import greencity.dto.user.UserDeactivationReasonDto;
import greencity.dto.user.UserFilterDto;
import greencity.dto.user.UserManagementVO;
import greencity.dto.user.UserProfileDtoRequest;
import greencity.dto.user.UserRoleDto;
import greencity.dto.user.UserVO;
import greencity.dto.user.UserVOAdvancedDto;
import greencity.entity.User;
import greencity.entity.UserDeactivationReason;
import greencity.entity.UserLocation;
import greencity.enums.EmailPreference;
import greencity.enums.EmailPreferencePeriodicity;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.ForbiddenException;
import greencity.exception.exceptions.InsufficientLocationDataException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserAlreadyExistsException;
import greencity.exception.exceptions.UserStatusUpdateException;
import greencity.exception.exceptions.WrongEmailException;
import greencity.exception.exceptions.WrongIdException;
import greencity.filters.SearchCriteria;
import greencity.filters.UserSpecification;
import greencity.mapping.UserManagementVOMapper;
import greencity.repository.UserDeactivationRepo;
import greencity.repository.UserLocationRepo;
import greencity.repository.UserRepo;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final ModelMapper modelMapper;
    private final UserManagementVOMapper userManagementVOMapper;
    private final UserRemoteClient userRemoteClient;
    private final UserLocationRepo userLocationRepo;
    private final GoogleApiService googleApiService;
    private final UserDeactivationRepo userDeactivationRepo;

    /**
     * {@inheritDoc}
     */
    @Override
    public UserVO findById(Long id) {
        return userRepo.findById(id)
            .map(user -> modelMapper.map(user, UserVO.class))
            .orElseThrow(() -> new WrongIdException(ErrorMessage.USER_NOT_FOUND_BY_ID + id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserVO findNotDeactivatedByEmail(String email) {
        User user = userRepo.findNotDeactivatedByEmail(email)
            .orElseThrow(() -> new WrongEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));
        return modelMapper.map(user, UserVO.class);
    }

    /**
     * Update {@code ROLE} of user.
     *
     * @param id   {@link UserVO} id.
     * @param role {@link Role} for user.
     * @return {@link UserRoleDto} updates like this on User entity should be
     *         handled in GreenCityUser via RestClient.
     */
    @Override
    public UserRoleDto updateRole(Long id, Role role, String email) {
        Map<String, String> body = Map.of("role", role.name());
        return userRemoteClient.updateUserRole(id, body)
            .orElseThrow(() -> new WrongIdException(ErrorMessage.USER_NOT_FOUND_BY_ID + id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkIfTheUserIsOnline(Long userId) {
        if (userId == null) {
            throw new WrongIdException(ErrorMessage.USER_ID_NULL);
        }

        return userRemoteClient.checkIfTheUserIsOnline(userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInitialsById(Long userId) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new WrongIdException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId));
        String name = user.getName();
        String initials = name.contains(" ") ? String.valueOf(name.charAt(0))
            .concat(String.valueOf(name.charAt(name.indexOf(" ") + 1)))
            : String.valueOf(name.charAt(0));
        return initials.toUpperCase();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserVO> getSixFriendsWithTheHighestRating(Long userId) {
        return userRepo.getSixFriendsWithTheHighestRating(userId).stream()
            .map(user -> modelMapper.map(user, UserVO.class))
            .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateEventOrganizerRating(Long eventOrganizerId, Double rate) {
        int updatedRows = userRepo.updateUserEventOrganizerRating(eventOrganizerId, rate);
        if (updatedRows == 0) {
            throw new WrongIdException(ErrorMessage.USER_NOT_FOUND_BY_ID + eventOrganizerId);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDetailedDto<UserManagementVO> getAllUsersByCriteria(UserFilterDto request, Pageable pageable) {
        var userFilterDto = createUserFilterDto(request.getQuery(), request.getStatus());
        pageable = applyDefaultSorting(pageable);

        Page<User> users = userRepo.findAll(buildSpecification(userFilterDto), pageable);
        Page<UserManagementVO> userManagementVOs = userManagementVOMapper.mapAllToPage(users);

        var pageInfo = getPageInfo(pageable, userManagementVOs);
        String sortModel = getSortModel(pageable);

        return new PageableDetailedDto<>(userManagementVOs.getContent(), userManagementVOs.getTotalElements(),
            pageInfo.currentPage(), pageInfo.pageNumbers(), pageInfo.totalPages(), sortModel,
            pageInfo.currentPage() == 0, pageInfo.currentPage() == pageInfo.totalPages() - 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateUserRating(Long userId, Double rating) {
        if (userRepo.findById(userId).isEmpty()) {
            throw new WrongIdException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId);
        }
        userRepo.updateUserRating(userId, rating);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserVO> findByEmails(List<String> emails) {
        return userRemoteClient.findAllByEmailIn(emails);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserVO> getUsersIdByEmailPreferenceAndEmailPeriodicity(EmailPreference emailPreference,
        EmailPreferencePeriodicity periodicity) {
        return userRemoteClient.findAllByEmailPreferenceAndEmailPeriodicity(emailPreference, periodicity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Long> getAllUserFriendsIds(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new WrongIdException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId);
        }
        return userRepo.getAllUserFriendsIds(userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Long> getAllUserFriendsIds(String email) {
        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));
        return userRepo.getAllUserFriendsIds(user.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableAdvancedDto<Long> getAllUserFriendsIds(Long userId, Pageable pageable) {
        if (!userRepo.existsById(userId)) {
            throw new WrongIdException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId);
        }
        Page<Long> page = userRepo.getAllUserFriendsIds(userId, pageable);

        return PageableAdvancedDto.<Long>builder()
            .page(page.getContent())
            .totalElements(page.getTotalElements())
            .currentPage(pageable.getPageNumber())
            .totalPages(page.getTotalPages())
            .number(pageable.getPageNumber())
            .hasPrevious(page.hasPrevious())
            .hasNext(page.hasNext())
            .first(page.isFirst())
            .last(page.isLast())
            .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableAdvancedDto<Long> getAllUserFriendsIds(String email, Pageable pageable) {
        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));
        return getAllUserFriendsIds(user.getId(), pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Long> getSixFriendsIdsWithTheHighestRating(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new WrongIdException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId);
        }
        return userRepo.getSixFriendsIdsWithTheHighestRating(userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Long> getSixFriendsIdsWithTheHighestRating(String email) {
        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));
        return userRepo.getSixFriendsIdsWithTheHighestRating(user.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLocationForUser(Long userId, UserProfileDtoRequest userProfileDtoRequest) {
        User user = findUserById(userId);
        if (shouldSkipLocationUpdate(user, userProfileDtoRequest)) {
            return;
        }

        if (user.getUserLocation() != null && (userProfileDtoRequest.getCoordinates().getLatitude() == null
            || userProfileDtoRequest.getCoordinates().getLongitude() == null)) {
            UserLocation old = user.getUserLocation();
            old.getUsers().remove(user);
            user.setUserLocation(null);
        } else {
            final AddressType[] addressTypes =
                {AddressType.LOCALITY, AddressType.ADMINISTRATIVE_AREA_LEVEL_1, AddressType.COUNTRY};

            GeocodingResult resultsUk = googleApiService.getLocationByCoordinates(
                userProfileDtoRequest.getCoordinates().getLatitude(),
                userProfileDtoRequest.getCoordinates().getLongitude(),
                "uk", addressTypes);
            GeocodingResult resultsEn = googleApiService.getLocationByCoordinates(
                userProfileDtoRequest.getCoordinates().getLatitude(),
                userProfileDtoRequest.getCoordinates().getLongitude(),
                "en", addressTypes);
            UserLocation userLocation = userLocationRepo.getUserLocationByLatitudeAndLongitude(
                userProfileDtoRequest.getCoordinates().getLatitude(),
                userProfileDtoRequest.getCoordinates().getLongitude()).orElse(new UserLocation());

            /*
             * check if user already has a location and if he is the only one assigned to
             * this location. If user do not have a location check if such location is in
             * database, if true then assign it to user, if not - add new location to
             * database and assign it to user. If user has a location and this location
             * belongs only to him, modify this location. If user has a location but there
             * are more users assigned to this location, then create a new location for this
             * user. If user inserted same location get his location and do not change
             * anything.
             */
            if (user.getUserLocation() != null && user.getUserLocation().getUsers().size() == 1) {
                if (userLocation.getId() != null && user.getUserLocation() != userLocation) {
                    UserLocation deleteLocation = user.getUserLocation();
                    user.setUserLocation(userLocation);
                    userLocationRepo.delete(deleteLocation);
                } else {
                    userLocation = user.getUserLocation();
                }
            } else if (user.getUserLocation() != null && user.getUserLocation().getUsers().size() > 1) {
                UserLocation old = user.getUserLocation();
                old.getUsers().remove(user);
            }
            initializeGeoCodingResults(initializeUkrainianGeoCodingResult(userLocation), resultsUk);
            initializeGeoCodingResults(initializeEnglishGeoCodingResult(userLocation), resultsEn);
            userLocation.setLatitude(userProfileDtoRequest.getCoordinates().getLatitude());
            userLocation.setLongitude(userProfileDtoRequest.getCoordinates().getLongitude());
            userLocation = userLocationRepo.save(userLocation);
            user.setUserLocation(userLocation);
            userRepo.save(user);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLocationForUser(String email, UserProfileDtoRequest userProfileDtoRequest) {
        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));
        setLocationForUser(user.getId(), userProfileDtoRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double findUserRating(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new WrongIdException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId);
        }
        return userRepo.findRatingById(userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void increaseUserRating(UserAddRatingDto userAddRatingDto) {
        User user = findUserById(userAddRatingDto.getId());
        user.setRating(user.getRating() + userAddRatingDto.getRating());
        userRepo.save(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void increaseUserRating(UserAddRatingExternalDto userAddRatingDto) {
        String email = userAddRatingDto.getEmail();
        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));
        user.setRating(user.getRating() + userAddRatingDto.getRating());
        userRepo.save(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserCityDto findAllUsersCities(Long userId) {
        return findUserLocation(userId, UserCityDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserCityDto findAllUsersCities(String email) {
        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));
        return findUserLocation(user.getId(), UserCityDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserLocationDto findUserLocationDtoByUserId(Long userId) {
        return findUserLocation(userId, UserLocationDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateUserCredo(UpdateUserCredoDto updateUserCredoDto) {
        userRepo.updateUserCredo(updateUserCredoDto.userId(), updateUserCredoDto.userCredo());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserVOAdvancedDto findByIdAdvanced(Long id) {
        return userRepo.findById(id)
            .map(user -> modelMapper.map(user, UserVOAdvancedDto.class))
            .orElseThrow(() -> new WrongIdException(ErrorMessage.USER_NOT_FOUND_BY_ID + id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSocialNetworkUrlByName(List<SocialNetworkVO> socialNetworks, String socialNetworkName) {
        return socialNetworks.stream()
            .map(SocialNetworkVO::getUrl)
            .filter(url -> url.contains(socialNetworkName))
            .findFirst()
            .orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean createUser(CreateGreenCityUserDto createUserDto) {
        Long newUserId = createUserDto.getId();
        if (userRepo.existsById(newUserId)) {
            throw new UserAlreadyExistsException(HttpStatus.CONFLICT,
                ErrorMessage.USER_ALREADY_REGISTERED_WITH_THIS_ID.formatted(newUserId));
        }
        String newUserEmail = createUserDto.getEmail();
        if (userRepo.existsByEmail(newUserEmail)) {
            throw new UserAlreadyExistsException(HttpStatus.CONFLICT,
                ErrorMessage.USER_ALREADY_REGISTERED_WITH_THIS_EMAIL.formatted(newUserEmail));
        }

        User userToSave = User.builder()
            .id(newUserId)
            .name(createUserDto.getName())
            .email(newUserEmail)
            .profilePicturePath(createUserDto.getProfilePicturePath())
            .rating(AppConstant.DEFAULT_RATING)
            .eventOrganizerRating(AppConstant.DEFAULT_RATING)
            .status(UserStatus.ACTIVATED)
            .build();
        userRepo.save(userToSave);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateUserProfilePicture(String email, String profilePicturePath) {
        int updatedRows = userRepo.updateUserProfilePictureByEmail(email, profilePicturePath);
        if (updatedRows == 0) {
            throw new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateUserName(Long userId, String userName) {
        int updatedRows = userRepo.updateUserName(userId, userName);
        if (updatedRows == 0) {
            throw new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateUserName(String email, String userName) {
        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));
        userRepo.updateUserName(user.getId(), userName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<GreenCityUserProfileDtoResponse> findGreenCityUserProfilesByUserIds(List<Long> userIds) {
        var greenCityProfiles = userRepo.findGreenCityUserProfilesByUserIds(userIds);

        int expectedSize = userIds.size();
        int resultSize = greenCityProfiles.size();

        if (resultSize < expectedSize) {
            List<Long> resultIds = greenCityProfiles.stream()
                .map(GreenCityUserProfileDtoResponse::getUserId)
                .toList();
            List<Long> notFoundIds = userIds.stream()
                .filter(userId -> !resultIds.contains(userId))
                .toList();
            String notFoundIdsStr = notFoundIds.stream().map(String::valueOf).collect(Collectors.joining(", "));
            throw new NotFoundException(ErrorMessage.USERS_NOT_FOUND_BY_IDS + notFoundIdsStr);
        }

        greenCityProfiles.forEach(greenCityProfile -> userLocationRepo.findAllUsersCities(greenCityProfile.getUserId())
            .ifPresent(userLocation -> {
                UserLocationDto userLocationDto = modelMapper.map(userLocation, UserLocationDto.class);
                greenCityProfile.setUserLocationDto(userLocationDto);
            }));
        return greenCityProfiles;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<GreenCityUserProfileDtoResponse> findGreenCityUserProfilesByEmails(List<String> emails) {
        List<Long> userIds = userRepo.getUserIdsByEmails(emails);
        return findGreenCityUserProfilesByUserIds(userIds);
    }

    private <T> T findUserLocation(Long userId, Class<T> clazz) {
        if (!userRepo.existsById(userId)) {
            throw new WrongIdException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId);
        }
        UserLocation userLocation = userLocationRepo.findAllUsersCities(userId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_DID_NOT_SET_ANY_CITY));
        return modelMapper.map(userLocation, clazz);
    }

    @Override
    public void fillGreenCityInfoInUsers(List<? extends UserManagementVO> users) {
        List<String> emails = users.stream()
            .filter(Objects::nonNull)
            .map(UserManagementVO::getEmail)
            .toList();
        Map<String, GreenCityUserInfoDto> usersInfo = userRepo.findGreenCityUserInfoDtosByEmails(emails).stream()
            .collect(Collectors.toMap(
                GreenCityUserInfoDto::userEmail,
                Function.identity()));
        users.stream()
            .filter(Objects::nonNull)
            .forEach(user -> {
                GreenCityUserInfoDto userInfo = usersInfo.get(user.getEmail());
                user.setId(userInfo.userId());
                user.setProfilePicturePath(userInfo.profilePicturePath());
                user.setUserCredo(userInfo.userCredo());
                user.setStatus(userInfo.status());
                user.setRating(userInfo.rating());
            });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserStatus getUserStatusByEmail(String email) {
        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));
        return user.getStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void deleteUserByEmail(String email) {
        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));

        if (user.getStatus() == UserStatus.DEACTIVATED
            || user.getStatus() == UserStatus.BLOCKED) {
            throw new ForbiddenException(ErrorMessage.FORBIDDEN_USER_DELETION);
        }

        user.setStatus(UserStatus.DELETED);
        userRepo.save(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Long> findAllActivatedUserIds(List<Long> ids) {
        if (ids != null) {
            return userRepo.findAllActivatedUserIdsFromList(ids);
        } else {
            return userRepo.findAllActivatedUserIds();
        }
    }

    /**
     * Counts all users by user {@link UserStatus}.
     *
     * @return amount of user with given {@link UserStatus}.
     */
    @Override
    public long countAllByStatus(UserStatus userStatus) {
        return userRepo.countAllByStatus(userStatus);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public List<Long> deactivateAllUsers(List<Long> listId, UserVO currentUser) {
        listId.forEach(id -> {
            String reason = createDeactivationReason(currentUser);
            deactivateUserByIdWithReasons(currentUser, id, List.of(reason));
        });
        return listId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void updateUserStatusById(UserVO currentUser, Long targetUserId, UserStatus status) {
        RequestedAndTargetUsersPair users = isOperationAllowed(currentUser, targetUserId, status);
        UserVO currentUserDto = users.currentUserDto();
        User targetUser = users.targetUser();

        switch (status) {
            case DEACTIVATED -> {
                String reason = createDeactivationReason(currentUserDto);
                saveDeactivationReason(targetUser, reason);
                sendDeactivationNotification(targetUser, reason);
            }
            case ACTIVATED -> {
                String lang = userRemoteClient.findUserLanguageByEmail(targetUser.getEmail());
                UserActivationDto notification = UserActivationDto.builder()
                    .email(targetUser.getEmail())
                    .name(targetUser.getName())
                    .lang(lang)
                    .build();
                userRemoteClient.sendMessageOfActivation(notification);
            }
            default -> { }
        }

        targetUser.setStatus(status);
        userRepo.save(targetUser);
    }

    @Override
    public void deactivateUserByIdWithReasons(UserVO currentUser, Long targetUserId, List<String> reasons) {
        UserStatus status = UserStatus.DEACTIVATED;
        RequestedAndTargetUsersPair users = isOperationAllowed(currentUser, targetUserId, status);
        User targetUser = users.targetUser();

        reasons.forEach(reason -> saveDeactivationReason(targetUser, reason));
        sendDeactivationNotification(targetUser, reasons.getLast());
        targetUser.setStatus(status);
        userRepo.save(targetUser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getDeactivationReasons(Long id, UserVO currentUser) {
        List<UserDeactivationReason> userReasons = userDeactivationRepo.getLastDeactivationReasons(id);
        if (userReasons.isEmpty()) {
            throw new NotFoundException(ErrorMessage.USER_DEACTIVATION_REASON_IS_EMPTY);
        }

        UserDeactivationReason lastReason = userReasons.getFirst();
        String userLang = userRemoteClient.findUserLanguageByEmail(currentUser.getEmail());
        if (userLang.equals("uk")) {
            userLang = "uk";
        }
        return filterReasons(userLang, lastReason.getReason());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getActivatedUsersAmount() {
        return userRepo.countAllByStatus(UserStatus.ACTIVATED);
    }

    private UserFilterDto createUserFilterDto(String criteria, String status) {
        if (status != null) {
            status = status.equals("all") ? null : status;
        }
        return new UserFilterDto(criteria, status);
    }

    private boolean shouldSkipLocationUpdate(User user, UserProfileDtoRequest userProfileDtoRequest) {
        return user.getUserLocation() == null
            && (userProfileDtoRequest.getCoordinates().getLatitude() == null
            || userProfileDtoRequest.getCoordinates().getLongitude() == null);
    }

    private void initializeGeoCodingResults(Map<AddressComponentType, Consumer<String>> initializedMap,
        GeocodingResult geocodingResult) {
        checkGeocodingResultContainsAllInformation(geocodingResult, initializedMap.size());
        initializedMap
            .forEach((key, value) -> Arrays.stream(geocodingResult.addressComponents)
                .forEach(addressComponent -> Arrays.stream(addressComponent.types)
                    .filter(componentType -> componentType.equals(key))
                    .forEach(componentType -> value.accept(addressComponent.longName))));
    }

    private void checkGeocodingResultContainsAllInformation(GeocodingResult geocodingResult, int size) {
        if (geocodingResult.addressComponents.length < size) {
            throw new InsufficientLocationDataException(ErrorMessage.INSUFFICIENT_LOCATION_DATA_FOUND);
        }
    }

    private Map<AddressComponentType, Consumer<String>> initializeEnglishGeoCodingResult(
        UserLocation userLocation) {
        return Map.of(
            AddressComponentType.LOCALITY, userLocation::setCityEn,
            AddressComponentType.COUNTRY, userLocation::setCountryEn,
            AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_1, userLocation::setRegionEn);
    }

    private Map<AddressComponentType, Consumer<String>> initializeUkrainianGeoCodingResult(
        UserLocation userLocation) {
        return Map.of(
            AddressComponentType.LOCALITY, userLocation::setCityUk,
            AddressComponentType.COUNTRY, userLocation::setCountryUk,
            AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_1, userLocation::setRegionUk);
    }

    private User findUserById(Long id) {
        return userRepo.findById(id)
            .orElseThrow(() -> new WrongIdException(ErrorMessage.USER_NOT_FOUND_BY_ID + id));
    }

    private Pageable applyDefaultSorting(Pageable pageable) {
        if (pageable.getSort().isUnsorted()) {
            return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "id"));
        }
        return pageable;
    }

    private String getSortModel(Pageable pageable) {
        return pageable.getSort().stream()
            .map(order -> order.getProperty() + "," + order.getDirection())
            .collect(Collectors.joining(","));
    }

    private PageInfoDto getPageInfo(Pageable pageable, Page<UserManagementVO> userManagementVOs) {
        int currentPage = pageable.getPageNumber();
        int totalPages = userManagementVOs.getTotalPages();
        int startPage = Math.max(0, currentPage - 3);
        int endPage = Math.min(currentPage + 3, totalPages - 1);
        List<Integer> pageNumbers = IntStream.rangeClosed(startPage, endPage).boxed().collect(Collectors.toList());

        return new PageInfoDto(currentPage, totalPages, pageNumbers);
    }

    private UserSpecification buildSpecification(UserFilterDto userFilterDto) {
        List<SearchCriteria> searchCriteriaList = new ArrayList<>();
        setValueIfNotEmpty(searchCriteriaList, "query", userFilterDto.getQuery());
        setValueIfNotEmpty(searchCriteriaList, "status", userFilterDto.getStatus());

        return new UserSpecification(searchCriteriaList);
    }

    private void setValueIfNotEmpty(List<SearchCriteria> searchCriteria, String key, String value) {
        if (StringUtils.hasText(value)) {
            searchCriteria.add(SearchCriteria.builder()
                .key(key)
                .type(key)
                .value(value)
                .build());
        }
    }

    private List<String> filterReasons(String lang, String reasons) {
        List<String> result = null;
        List<String> forAll = List.of(reasons.split("/"));
        if (lang.equals("en")) {
            result = forAll.stream().filter(s -> s.contains("{en}"))
                .map(filterEn -> filterEn.replace("{en}", "").trim()).toList();
        }
        if (lang.equals("uk")) {
            result = forAll.stream().filter(s -> s.contains("{uk}"))
                .map(filterEn -> filterEn.replace("{uk}", "").trim()).toList();
        }
        return result;
    }

    private RequestedAndTargetUsersPair isOperationAllowed(UserVO currentUser, Long targetUserId, UserStatus status) {
        UserVO currentUserDto = userRemoteClient.findByEmail(currentUser.getEmail())
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + currentUser.getEmail()));
        if (currentUserDto.getId().equals(targetUserId)) {
            throw new UserStatusUpdateException(ErrorMessage.USER_CANNOT_DEACTIVATE_YOURSELF);
        }

        User targetUser = userRepo.findById(targetUserId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + targetUserId));
        UserVO targetUserDto = userRemoteClient.findByEmail(targetUser.getEmail())
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + targetUser.getEmail()));
        if (status.equals(UserStatus.DEACTIVATED) && targetUserDto.getRole().equals(Role.ROLE_ADMIN)) {
            throw new UserStatusUpdateException(ErrorMessage.ADMIN_CANNOT_DEACTIVATE_OTHER_ADMIN);
        }

        return new RequestedAndTargetUsersPair(currentUserDto, targetUser, targetUserDto);
    }

    private void saveDeactivationReason(User targetUser, String reason) {
        userDeactivationRepo.save(UserDeactivationReason.builder()
            .dateTimeOfDeactivation(LocalDateTime.now())
            .reason(reason)
            .user(targetUser)
            .build());
    }

    private void sendDeactivationNotification(User targetUser, String reason) {
        String lang = userRemoteClient.findUserLanguageByEmail(targetUser.getEmail());
        UserDeactivationReasonDto notification = UserDeactivationReasonDto.builder()
            .deactivationReason(reason)
            .email(targetUser.getEmail())
            .name(targetUser.getName())
            .lang(lang)
            .build();
        userRemoteClient.sendReasonOfDeactivation(notification);
    }

    private String createDeactivationReason(UserVO currentUserDto) {
        return String.format("Deactivated by %s[%s] admin.", currentUserDto.getName(), currentUserDto.getEmail());
    }

    private record RequestedAndTargetUsersPair(UserVO currentUserDto, User targetUser, UserVO targetUserDto) {}
}