package greencity.service;

import com.google.maps.model.AddressComponentType;
import com.google.maps.model.AddressType;
import com.google.maps.model.GeocodingResult;
import greencity.client.UserRemoteClient;
import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.constant.LogMessage;
import greencity.dto.PageInfoDto;
import greencity.dto.PageableDetailedDto;
import greencity.dto.location.UserLocationDto;
import greencity.dto.user.UpdateUserCredoDto;
import greencity.dto.user.UpdateUserDto;
import greencity.dto.socialnetwork.SocialNetworkVO;
import greencity.dto.user.UserAddRatingDto;
import greencity.dto.user.UserCityDto;
import greencity.dto.user.UserFilterDto;
import greencity.dto.user.UserManagementVO;
import greencity.dto.user.UserProfileDtoRequest;
import greencity.dto.user.UserRoleDto;
import greencity.dto.user.UserStatusDto;
import greencity.dto.user.UserVO;
import greencity.dto.user.UserVOAdvancedDto;
import greencity.dto.user.CreateGreenCityUserDto;
import greencity.entity.User;
import greencity.entity.UserLocation;
import greencity.enums.EmailPreference;
import greencity.enums.EmailPreferencePeriodicity;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import greencity.enums.UserUpdateType;
import greencity.exception.exceptions.BadUpdateRequestException;
import greencity.exception.exceptions.InsufficientLocationDataException;
import greencity.exception.exceptions.LowRoleLevelException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.WrongEmailException;
import greencity.exception.exceptions.WrongIdException;
import greencity.exception.exceptions.UserAlreadyExistsException;
import greencity.mapping.UpdateUserDtoUserMapper;
import greencity.mapping.UserManagementVOMapper;
import greencity.repository.UserLocationRepo;
import greencity.repository.UserRepo;
import greencity.repository.options.UserFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    private final UpdateUserDtoUserMapper updateUserDtoUserMapper;

    @Value("300000")
    private long timeAfterLastActivity;

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(UserVO userVO) {
        userRepo.save(modelMapper.map(userVO, User.class));
    }

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
    public UserVO findByEmail(String email) {
        return userRepo.findByEmail(email)
            .map(user -> modelMapper.map(user, UserVO.class))
            .orElseThrow(() -> new WrongEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<UserVO> findNotDeactivatedByEmail(String email) {
        UserVO user = userRemoteClient.findNotDeactivatedByEmail(email)
            .orElseThrow(() -> new WrongEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));
        return Optional.of(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long findIdByEmail(String email) {
        log.info(LogMessage.IN_FIND_ID_BY_EMAIL, email);
        return userRepo.findIdByEmail(email)
            .orElseThrow(() -> new WrongEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserStatusDto updateStatus(Long id, UserStatus userStatus, String email) {
        checkUpdatableUser(id, email);
        accessForUpdateUserStatus(id, email);
        UserVO userVO = findById(id);
        userVO.setUserStatus(userStatus);

        UserStatusDto userStatusDto = UserStatusDto.builder()
            .id(id)
            .userStatus(userStatus)
            .build();

        userRemoteClient.updateUserStatus(userStatusDto);
        return modelMapper.map(userVO, UserStatusDto.class);
    }

    /**
     * Update {@code ROLE} of user.
     *
     * @param id   {@link UserVO} id.
     * @param role {@link Role} for user.
     * @return {@link UserRoleDto}
     * @deprecated updates like this on User entity should be handled in
     *             GreenCityUser via RestClient.
     */
    @Deprecated
    @Override
    public UserRoleDto updateRole(Long id, Role role, String email) {
        Map<String, String> body = Map.of("role", role.name());
        return userRemoteClient.updateUserRole(id, body)
            .orElseThrow(() -> new WrongIdException(ErrorMessage.USER_NOT_FOUND_BY_ID + id));
    }

    /**
     * Method which check that, if admin/moderator update role/status of himself,
     * then throw exception.
     *
     * @param id    id of updatable user.
     * @param email email of admin/moderator.
     */
    protected void checkUpdatableUser(Long id, String email) {
        UserVO user = findByEmail(email);
        if (id.equals(user.getId())) {
            throw new BadUpdateRequestException(ErrorMessage.USER_CANT_UPDATE_HIMSELF);
        }
    }

    /**
     * Method which check that, if moderator trying update status of admins or
     * moderators, then throw exception.
     *
     * @param id    id of updatable user.
     * @param email email of admin/moderator.
     */
    private void accessForUpdateUserStatus(Long id, String email) {
        UserVO user = findByEmail(email);
        if (user.getRole() == Role.ROLE_MODERATOR) {
            Role role = findById(id).getRole();
            if ((role == Role.ROLE_MODERATOR) || (role == Role.ROLE_ADMIN)) {
                throw new LowRoleLevelException(ErrorMessage.IMPOSSIBLE_UPDATE_USER_STATUS);
            }
        }
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
        userRepo.updateUserEventOrganizerRating(eventOrganizerId, rate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDetailedDto<UserManagementVO> getAllUsersByCriteria(UserFilterDto request, Pageable pageable) {
        var userFilterDto = createUserFilterDto(request.getQuery(), request.getRole(), request.getStatus());
        pageable = applyDefaultSorting(pageable);

        Page<User> users = userRepo.findAll(new UserFilter(userFilterDto), pageable);
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
            throw new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId);
        }
        userRepo.updateUserRating(userId, rating);
    }

    private UserFilterDto createUserFilterDto(String criteria, String role, String status) {
        if (status != null) {
            status = status.equals("all") ? null : status;
        }
        if (role != null) {
            role = role.equals("all") ? null : role;
        }
        return new UserFilterDto(criteria, role, status);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserVO> findByEmails(List<String> emails) {
        return userRepo.findAllByEmailIn(emails).stream()
            .map(u -> modelMapper.map(u, UserVO.class))
            .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserVO> getUsersIdByEmailPreferenceAndEmailPeriodicity(EmailPreference emailPreference,
        EmailPreferencePeriodicity periodicity) {
        return userRemoteClient.findAllByEmailPreferenceAndEmailPeriodicity(emailPreference.name(), periodicity.name());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Long> getAllUserFriendsIds(Long userId) {
        return userRepo.getAllUserFriendsIds(userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Long> getAllUserFriendsIds(Long userId, Pageable pageable) {
        return userRepo.getAllUserFriendsIds(userId, pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Long> getSixFriendsIdsWithTheHighestRating(Long userId) {
        return userRepo.getSixFriendsWithTheHighestRating(userId).stream()
            .map(User::getId)
            .toList();
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
    public void increaseUserRating(UserAddRatingDto userAddRatingDto) {
        User user = findUserById(userAddRatingDto.getId());
        user.setRating(user.getRating() + userAddRatingDto.getRating());
        userRepo.save(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double findUserRating(Long userId) {
        return userRepo.findRatingById(userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserCityDto findAllUsersCities(Long userId) {
        UserLocation userLocation = userLocationRepo.findAllUsersCities(userId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_DID_NOT_SET_ANY_CITY));
        return modelMapper.map(userLocation, UserCityDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserLocationDto findUserLocationDtoByUserId(Long userId) {
        UserLocation userLocation = userLocationRepo.findAllUsersCities(userId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_DID_NOT_SET_ANY_CITY));
        return modelMapper.map(userLocation, UserLocationDto.class);
    }

    @Override
    public void updateUserCredo(UpdateUserCredoDto updateUserCredoDto) {
        userRepo.updateUserCredo(updateUserCredoDto.userId(), updateUserCredoDto.userCredo());
    }

    @Override
    public String findUserCredoByUserId(Long userId) {
        return userRepo.findUserCredoByUserId(userId);
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

    /***
     * {@inheritDoc}
     */
    public boolean update(UpdateUserDto updateUserDto) {
        User user;
        System.out.println(updateUserDto.getUserUpdateType());
        if (!Objects.equals(updateUserDto.getUserUpdateType(), UserUpdateType.CREATE)) {
            user = userRepo.findByEmail(updateUserDto.getEmail()).orElseThrow(
                () -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + updateUserDto.getEmail()));
            updateUserDtoUserMapper.merge(updateUserDto, user);
        } else {
            user = User.builder()
                .id(updateUserDto.getId())
                .email(updateUserDto.getEmail())
                .name(updateUserDto.getName())
                .profilePicturePath(updateUserDto.getProfilePicturePath())
                .rating(AppConstant.DEFAULT_RATING)
                .eventOrganizerRating(AppConstant.DEFAULT_RATING)
                .build();
        }
        userRepo.save(user);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserVOAdvancedDto findByIdAdvanced(Long id) {
        return userRepo.findById(id)
            .map(user -> {
                UserVOAdvancedDto userVOAdvancedDto = modelMapper.map(user, UserVOAdvancedDto.class);
                UserLocation userLocation = user.getUserLocation();
                if (userLocation != null) {
                    UserLocationDto userLocationDto = modelMapper.map(userLocation, UserLocationDto.class);
                    userVOAdvancedDto.setUserLocation(userLocationDto);
                }
                return userVOAdvancedDto;
            })
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
        Optional<User> existingUser = userRepo.findByEmail(createUserDto.getEmail());
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException(HttpStatus.CONFLICT,
                ErrorMessage.USER_ALREADY_REGISTERED_WITH_THIS_EMAIL);
        }
        User userToSave = User.builder()
            .id(createUserDto.getId())
            .email(createUserDto.getEmail())
            .name(createUserDto.getName())
            .profilePicturePath(createUserDto.getProfilePicturePath())
            .rating(AppConstant.DEFAULT_RATING)
            .eventOrganizerRating(AppConstant.DEFAULT_RATING)
            .build();
        userRepo.save(userToSave);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateUserProfilePicture(Long userId, String profilePicturePath) {
        User user = userRepo.findById(userId).orElseThrow(
            () -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId));
        user.setProfilePicturePath(profilePicturePath);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProfilePicturePath(Long userId) {
        User user = userRepo.findById(userId).orElseThrow(
            () -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId));
        return user.getProfilePicturePath();
    }
}