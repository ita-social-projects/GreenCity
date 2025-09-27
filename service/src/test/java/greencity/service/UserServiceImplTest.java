package greencity.service;

import com.google.maps.model.AddressType;
import greencity.ModelUtils;
import greencity.TestConst;
import greencity.client.UserRemoteClient;
import greencity.constant.ErrorMessage;
import greencity.dto.CoordinatesDto;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.PageableDetailedDto;
import greencity.dto.location.UserLocationDto;
import greencity.dto.socialnetwork.SocialNetworkVO;
import greencity.dto.user.GreenCityUserInfoDto;
import greencity.dto.user.GreenCityUserProfileDtoResponse;
import greencity.dto.user.UpdateUserCredoDto;
import greencity.dto.user.UserAddRatingDto;
import greencity.dto.user.UserAddRatingExternalDto;
import greencity.dto.user.UserCityDto;
import greencity.dto.user.UserFilterDto;
import greencity.dto.user.UserManagementVO;
import greencity.dto.user.UserProfileDtoRequest;
import greencity.dto.user.UserRoleDto;
import greencity.dto.user.UserVO;
import greencity.dto.user.CreateGreenCityUserDto;
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
import greencity.exception.exceptions.UserStatusUpdateException;
import greencity.exception.exceptions.WrongIdException;
import greencity.exception.exceptions.UserAlreadyExistsException;
import greencity.filters.UserSpecification;
import greencity.mapping.UserManagementVOMapper;
import greencity.repository.UserDeactivationRepo;
import greencity.repository.UserLocationRepo;
import greencity.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static greencity.ModelUtils.getListUserManagementVO;
import static greencity.ModelUtils.getSortedPageable;
import static greencity.ModelUtils.getUnSortedPageable;
import static greencity.ModelUtils.getUserFilterDto;
import static greencity.ModelUtils.getUserManagementVOPage;
import static greencity.ModelUtils.getUserPage;
import static greencity.ModelUtils.getUser;
import static greencity.enums.UserStatus.ACTIVATED;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalMatchers.aryEq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

@ExtendWith(SpringExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepo userRepo;
    @Mock
    private UserRemoteClient userRemoteClient;
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    UserLocationRepo userLocationRepo;
    @Mock
    UserManagementVOMapper userManagementVOMapper;
    @Mock
    private UserDeactivationRepo userDeactivationRepo;

    @Mock
    GoogleApiService googleApiService;

    private final UserVO userVO = UserVO.builder()
        .id(1L)
        .name("Test Testing")
        .email("test@gmail.com")
        .role(Role.ROLE_USER)
        .status(ACTIVATED)
        .build();
    private final AddressType[] addressTypes =
        {AddressType.LOCALITY, AddressType.ADMINISTRATIVE_AREA_LEVEL_1, AddressType.COUNTRY};
    private final String languageUa = "uk"; // language for GeocodingApi it gets uk not ua.
    private final String languageEn = "en";

    @Test
    void findByIdTest() {
        Long id = 1L;

        User user = new User();
        user.setId(1L);

        when(userRepo.findById(id)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserVO.class)).thenReturn(userVO);
        assertEquals(userVO, userService.findById(id));
        verify(userRepo, times(1)).findById(id);
    }

    @Test
    void checkIfTheUserIsOnlineExceptionTest() {
        assertThrows(WrongIdException.class, () -> userService.checkIfTheUserIsOnline(null));
    }

    @Test
    void checkIfTheUserIsOnlineEqualsTrueTest() {
        Long userId = 4L;
        boolean isOnline = true;

        when(userRemoteClient.checkIfTheUserIsOnline(userId))
            .thenReturn(isOnline);

        boolean actualResult = userService.checkIfTheUserIsOnline(userId);

        assertEquals(isOnline, actualResult);
    }

    @Test
    void checkIfTheUserIsOnlineEqualsFalseTest() {
        Long userId = 3L;
        boolean isOnline = false;

        when(userRemoteClient.checkIfTheUserIsOnline(userId))
            .thenReturn(isOnline);

        boolean actualResult = userService.checkIfTheUserIsOnline(userId);

        assertEquals(isOnline, actualResult);
    }

    @Test
    void getSixFriendsWithTheHighestRatingTest() {
        List<User> friendsList = ModelUtils.getFriendsList();
        List<UserVO> expectedResult = friendsList.stream()
            .map(friend -> modelMapper.map(friend, UserVO.class))
            .toList();
        Long userId = 1L;

        when(userRepo.getSixFriendsWithTheHighestRating(userId))
            .thenReturn(friendsList);

        List<UserVO> actualResult = userService.getSixFriendsWithTheHighestRating(userId);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void updateEventOrganizerRatingTest() {
        Long eventOrganizerId = TestConst.USER_ID;
        Double rate = 3.;

        when(userRepo.updateUserEventOrganizerRating(eventOrganizerId, rate))
            .thenReturn(1);

        assertDoesNotThrow(() -> userService.updateEventOrganizerRating(eventOrganizerId, rate));

        verify(userRepo).updateUserEventOrganizerRating(eventOrganizerId, rate);
    }

    @Test
    void updateEventOrganizerRatingWhenEventOrganizerNotFoundTest() {
        Long eventOrganizerId = TestConst.USER_ID;
        Double rate = 3.;
        String expectedExceptionMessage = ErrorMessage.USER_NOT_FOUND_BY_ID + eventOrganizerId;

        when(userRepo.updateUserEventOrganizerRating(eventOrganizerId, rate))
            .thenReturn(0);

        var wrongIdException = assertThrows(
            WrongIdException.class,
            () -> userService.updateEventOrganizerRating(eventOrganizerId, rate));

        assertEquals(expectedExceptionMessage, wrongIdException.getMessage());
        verify(userRepo).updateUserEventOrganizerRating(eventOrganizerId, rate);
    }

    @Test
    void getUsersIdByEmailPreferenceAndEmailPeriodicityTest() {
        EmailPreference emailPreference = EmailPreference.LIKES;
        EmailPreferencePeriodicity emailPreferencePeriodicity = EmailPreferencePeriodicity.DAILY;
        List<UserVO> userVOs = List.of(ModelUtils.getUserVO());

        when(userRemoteClient.findAllByEmailPreferenceAndEmailPeriodicity(
            emailPreference,
            emailPreferencePeriodicity)).thenReturn(userVOs);

        List<UserVO> actualResult =
            userService.getUsersIdByEmailPreferenceAndEmailPeriodicity(emailPreference, emailPreferencePeriodicity);

        assertEquals(userVOs, actualResult);
    }

    @Test
    void getAllUserFriendsIdsTest() {
        Long userId = TestConst.USER_ID;
        List<Long> userFriendIds = List.of(1L, 2L, 3L);

        when(userRepo.existsById(userId))
            .thenReturn(true);
        when(userRepo.getAllUserFriendsIds(userId))
            .thenReturn(userFriendIds);

        List<Long> actualResult = userService.getAllUserFriendsIds(userId);

        assertEquals(userFriendIds, actualResult);
        verify(userRepo).existsById(userId);
        verify(userRepo).getAllUserFriendsIds(userId);
    }

    @Test
    void getAllUserFriendsIdsWhenUserNotFoundTest() {
        Long userId = TestConst.USER_ID;
        String expectedExceptionMessage = ErrorMessage.USER_NOT_FOUND_BY_ID + userId;

        when(userRepo.existsById(userId))
            .thenReturn(false);

        var wrongIdException = assertThrows(
            WrongIdException.class,
            () -> userService.getAllUserFriendsIds(userId));

        assertEquals(expectedExceptionMessage, wrongIdException.getMessage());
        verify(userRepo).existsById(userId);
        verify(userRepo, never()).getAllUserFriendsIds(any());
    }

    @Test
    void getAllUserFriendsIdsByEmailTest() {
        User user = ModelUtils.getUser();
        Long userId = user.getId();
        String email = user.getEmail();
        List<Long> userFriendIds = List.of(1L, 2L, 3L);

        when(userRepo.findByEmail(email))
            .thenReturn(Optional.of(user));
        when(userRepo.getAllUserFriendsIds(userId))
            .thenReturn(userFriendIds);

        List<Long> actualResult = userService.getAllUserFriendsIds(email);

        assertEquals(userFriendIds, actualResult);
        verify(userRepo).findByEmail(email);
        verify(userRepo).getAllUserFriendsIds(userId);
    }

    @Test
    void getAllUserFriendsIdsByEmailWhenUserNotFoundTest() {
        User user = ModelUtils.getUser();
        String email = user.getEmail();
        String expectedExceptionMessage = ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email;

        when(userRepo.findByEmail(email))
            .thenReturn(Optional.empty());

        var notFoundException = assertThrows(
            NotFoundException.class,
            () -> userService.getAllUserFriendsIds(email));

        assertEquals(expectedExceptionMessage, notFoundException.getMessage());
        verify(userRepo).findByEmail(email);
        verify(userRepo, never()).getAllUserFriendsIds(any());
    }

    @Test
    void getAllUserFriendsIdsPageableTest() {
        Long userId = TestConst.USER_ID;
        Page<Long> userFriendIds = new PageImpl<>(List.of(1L, 2L, 3L));
        int pageNumber = 0;
        int pageSize = 5;
        PageableAdvancedDto<Long> expectedResult = PageableAdvancedDto.<Long>builder()
            .page(userFriendIds.getContent())
            .totalElements(userFriendIds.getTotalElements())
            .currentPage(pageNumber)
            .totalPages(userFriendIds.getTotalPages())
            .number(pageNumber)
            .hasPrevious(userFriendIds.hasPrevious())
            .hasNext(userFriendIds.hasNext())
            .first(userFriendIds.isFirst())
            .last(userFriendIds.isLast())
            .build();
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        when(userRepo.existsById(userId))
            .thenReturn(true);
        when(userRepo.getAllUserFriendsIds(userId, pageable))
            .thenReturn(userFriendIds);

        PageableAdvancedDto<Long> actualResult = userService.getAllUserFriendsIds(userId, pageable);

        assertEquals(expectedResult, actualResult);
        verify(userRepo).existsById(userId);
        verify(userRepo).getAllUserFriendsIds(userId, pageable);
    }

    @Test
    void getAllUserFriendsIdsPageableWhenUserNotFoundTest() {
        Long userId = TestConst.USER_ID;
        Pageable pageable = PageRequest.of(0, 5);
        String expectedExceptionMessage = ErrorMessage.USER_NOT_FOUND_BY_ID + userId;

        when(userRepo.existsById(userId))
            .thenReturn(false);

        var wrongIdException = assertThrows(
            WrongIdException.class,
            () -> userService.getAllUserFriendsIds(userId, pageable));

        assertEquals(expectedExceptionMessage, wrongIdException.getMessage());
        verify(userRepo).existsById(userId);
        verify(userRepo, never()).getAllUserFriendsIds(any(), any());
    }

    @Test
    void getAllUserFriendsIdsPageableByEmailTest() {
        User user = ModelUtils.getUser();
        Long userId = user.getId();
        String email = user.getEmail();
        Page<Long> userFriendIds = new PageImpl<>(List.of(1L, 2L, 3L));
        int pageNumber = 0;
        int pageSize = 5;
        PageableAdvancedDto<Long> expectedResult = PageableAdvancedDto.<Long>builder()
            .page(userFriendIds.getContent())
            .totalElements(userFriendIds.getTotalElements())
            .currentPage(pageNumber)
            .totalPages(userFriendIds.getTotalPages())
            .number(pageNumber)
            .hasPrevious(userFriendIds.hasPrevious())
            .hasNext(userFriendIds.hasNext())
            .first(userFriendIds.isFirst())
            .last(userFriendIds.isLast())
            .build();
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        when(userRepo.findByEmail(email))
            .thenReturn(Optional.of(user));
        when(userRepo.existsById(userId))
            .thenReturn(true);
        when(userRepo.getAllUserFriendsIds(userId, pageable))
            .thenReturn(userFriendIds);

        PageableAdvancedDto<Long> actualResult = userService.getAllUserFriendsIds(email, pageable);

        assertEquals(expectedResult, actualResult);
        verify(userRepo).findByEmail(email);
        verify(userRepo).getAllUserFriendsIds(userId, pageable);
    }

    @Test
    void getAllUserFriendsIdsPageableByEmailWhenUserNotFoundTest() {
        User user = ModelUtils.getUser();
        String email = user.getEmail();
        Pageable pageable = PageRequest.of(0, 5);
        String expectedExceptionMessage = ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email;

        when(userRepo.findByEmail(email))
            .thenReturn(Optional.empty());

        var notFoundException = assertThrows(
            NotFoundException.class,
            () -> userService.getAllUserFriendsIds(email, pageable));

        assertEquals(expectedExceptionMessage, notFoundException.getMessage());
        verify(userRepo).findByEmail(email);
        verify(userRepo, never()).getAllUserFriendsIds(any(), any());
    }

    @Test
    void getSixFriendsIdsWithTheHighestRatingTest() {
        Long userId = TestConst.USER_ID;
        List<Long> userFriendIds = List.of(1L, 2L, 3L, 4L, 5L, 6L);

        when(userRepo.existsById(userId))
            .thenReturn(true);
        when(userRepo.getSixFriendsIdsWithTheHighestRating(userId))
            .thenReturn(userFriendIds);

        List<Long> actualResult = userService.getSixFriendsIdsWithTheHighestRating(userId);

        assertEquals(userFriendIds, actualResult);
        verify(userRepo).existsById(userId);
        verify(userRepo).getSixFriendsIdsWithTheHighestRating(userId);
    }

    @Test
    void getSixFriendsIdsWithTheHighestRatingWhenUserNotFoundTest() {
        Long userId = TestConst.USER_ID;
        String expectedExceptionMessage = ErrorMessage.USER_NOT_FOUND_BY_ID + userId;

        when(userRepo.existsById(userId))
            .thenReturn(false);

        var wrongIdException = assertThrows(
            WrongIdException.class,
            () -> userService.getSixFriendsIdsWithTheHighestRating(userId));

        assertEquals(expectedExceptionMessage, wrongIdException.getMessage());
        verify(userRepo).existsById(userId);
        verify(userRepo, never()).getSixFriendsWithTheHighestRating(any());
    }

    @Test
    void getSixFriendsIdsWithTheHighestRatingByEmailTest() {
        User user = ModelUtils.getUser();
        Long userId = user.getId();
        String email = user.getEmail();
        List<Long> userFriendIds = List.of(1L, 2L, 3L, 4L, 5L, 6L);

        when(userRepo.findByEmail(email))
            .thenReturn(Optional.of(user));
        when(userRepo.getSixFriendsIdsWithTheHighestRating(userId))
            .thenReturn(userFriendIds);

        List<Long> actualResult = userService.getSixFriendsIdsWithTheHighestRating(email);

        assertEquals(userFriendIds, actualResult);
        verify(userRepo).findByEmail(email);
        verify(userRepo).getSixFriendsIdsWithTheHighestRating(userId);
    }

    @Test
    void getSixFriendsIdsWithTheHighestRatingByEmailWhenUserNotFoundTest() {
        User user = ModelUtils.getUser();
        String email = user.getEmail();
        String expectedExceptionMessage = ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email;

        when(userRepo.findByEmail(email))
            .thenReturn(Optional.empty());

        var notFoundException = assertThrows(
            NotFoundException.class,
            () -> userService.getSixFriendsIdsWithTheHighestRating(email));

        assertEquals(expectedExceptionMessage, notFoundException.getMessage());
        verify(userRepo).findByEmail(email);
        verify(userRepo, never()).getSixFriendsWithTheHighestRating(any());
    }

    @Test
    void setLocationForUserTest() {
        Long userId = TestConst.USER_ID;
        var request = ModelUtils.getUserProfileDtoRequest();
        var user = spy(ModelUtils.getUser());
        var userLocation = ModelUtils.getUserLocation();
        var savedUserLocation = userLocation.setId(3L);

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(googleApiService.getLocationByCoordinates(
            request.getCoordinates().getLatitude(),
            request.getCoordinates().getLongitude(),
            languageUa, addressTypes))
            .thenReturn(ModelUtils.getGeocodingResult().getFirst());
        when(googleApiService.getLocationByCoordinates(
            request.getCoordinates().getLatitude(),
            request.getCoordinates().getLongitude(),
            languageEn, addressTypes))
            .thenReturn(ModelUtils.getGeocodingResult().getFirst());
        when(userLocationRepo.getUserLocationByLatitudeAndLongitude(
            request.getCoordinates().getLatitude(),
            request.getCoordinates().getLongitude())).thenReturn(Optional.of(userLocation));
        when(userLocationRepo.save(userLocation))
            .thenReturn(savedUserLocation);

        userService.setLocationForUser(userId, request);

        verify(userRepo).findById(userId);
        verify(googleApiService).getLocationByCoordinates(
            request.getCoordinates().getLatitude(),
            request.getCoordinates().getLongitude(),
            languageUa, addressTypes);
        verify(googleApiService).getLocationByCoordinates(
            request.getCoordinates().getLatitude(),
            request.getCoordinates().getLongitude(),
            languageEn, addressTypes);
        verify(userLocationRepo).getUserLocationByLatitudeAndLongitude(
            request.getCoordinates().getLatitude(),
            request.getCoordinates().getLongitude());
        verify(userLocationRepo).save(userLocation);
        verify(user).setUserLocation(savedUserLocation);
        verify(userRepo).save(user);
    }

    @Test
    void setLocationForUserRemoveOldLocationTest() {
        Long userId = TestConst.USER_ID;
        User user = spy(ModelUtils.getUserWithUserLocation());
        UserLocation userLocation = spy(user.getUserLocation());
        List<User> users = spy(new ArrayList<>(List.of(user)));

        var request = ModelUtils.getUserProfileDtoRequest();
        request.getCoordinates().setLatitude(null);
        request.getCoordinates().setLongitude(null);

        when(userRepo.findById(userId))
            .thenReturn(Optional.of(user));
        when(user.getUserLocation())
            .thenReturn(userLocation);
        when(userLocation.getUsers())
            .thenReturn(users);

        userService.setLocationForUser(userId, request);

        verify(userRepo).findById(userId);
        verify(user).setUserLocation(null);
    }

    @Test
    void setLocationForUserByEmailTest() {
        User user = ModelUtils.getUser();
        Long userId = user.getId();
        String email = user.getEmail();
        var request = ModelUtils.getUserProfileDtoRequest();
        var userLocation = ModelUtils.getUserLocation();
        var savedUserLocation = userLocation.setId(3L);

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(googleApiService.getLocationByCoordinates(
            request.getCoordinates().getLatitude(),
            request.getCoordinates().getLongitude(),
            languageUa, addressTypes))
            .thenReturn(ModelUtils.getGeocodingResult().getFirst());
        when(googleApiService.getLocationByCoordinates(
            request.getCoordinates().getLatitude(),
            request.getCoordinates().getLongitude(),
            languageEn, addressTypes))
            .thenReturn(ModelUtils.getGeocodingResult().getFirst());
        when(userLocationRepo.getUserLocationByLatitudeAndLongitude(
            request.getCoordinates().getLatitude(),
            request.getCoordinates().getLongitude())).thenReturn(Optional.of(userLocation));
        when(userLocationRepo.save(userLocation))
            .thenReturn(savedUserLocation);

        userService.setLocationForUser(email, request);

        verify(userRepo).findByEmail(email);
        verify(userRepo).findById(userId);
        verify(googleApiService).getLocationByCoordinates(
            request.getCoordinates().getLatitude(),
            request.getCoordinates().getLongitude(),
            languageUa, addressTypes);
        verify(googleApiService).getLocationByCoordinates(
            request.getCoordinates().getLatitude(),
            request.getCoordinates().getLongitude(),
            languageEn, addressTypes);
        verify(userLocationRepo).getUserLocationByLatitudeAndLongitude(
            request.getCoordinates().getLatitude(),
            request.getCoordinates().getLongitude());
        verify(userLocationRepo).save(userLocation);
        verify(userRepo).save(user);
    }

    @Test
    void setLocationForUserByEmailRemoveOldLocationTest() {
        User user = spy(ModelUtils.getUserWithUserLocation());
        Long userId = user.getId();
        String email = user.getEmail();
        UserLocation userLocation = spy(user.getUserLocation());
        List<User> users = spy(new ArrayList<>(List.of(user)));

        var request = ModelUtils.getUserProfileDtoRequest();
        request.getCoordinates().setLatitude(null);
        request.getCoordinates().setLongitude(null);

        when(userRepo.findByEmail(email))
            .thenReturn(Optional.of(user));
        when(userRepo.findById(userId))
            .thenReturn(Optional.of(user));
        when(user.getUserLocation())
            .thenReturn(userLocation);
        when(userLocation.getUsers())
            .thenReturn(users);

        userService.setLocationForUser(email, request);

        verify(userRepo).findByEmail(email);
        verify(userRepo).findById(userId);
        verify(user).setUserLocation(null);
    }

    @Test
    void saveUserProfileWithUnusualLongitudeAndLatitudeTest() {
        Long userId = TestConst.USER_ID;
        var request = ModelUtils.getUserProfileDtoRequest();
        var user = ModelUtils.getUser();

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(googleApiService.getLocationByCoordinates(
            request.getCoordinates().getLatitude(),
            request.getCoordinates().getLongitude(),
            languageUa, addressTypes))
            .thenReturn(ModelUtils.getGeocodingResultWithInsufficientData());

        when(googleApiService.getLocationByCoordinates(
            request.getCoordinates().getLatitude(),
            request.getCoordinates().getLongitude(),
            languageEn, addressTypes))
            .thenReturn(ModelUtils.getGeocodingResultWithInsufficientData());

        assertThrows(InsufficientLocationDataException.class,
            () -> userService.setLocationForUser(userId, request));

        verify(userRepo).findById(userId);
        verify(googleApiService, times(2)).getLocationByCoordinates(any(), any(), anyString(), aryEq(addressTypes));
    }

    @Test
    void setLocationForUserWhenMultipleUsersHaveSameLocationTest() {
        Long userId = TestConst.USER_ID;
        var request = ModelUtils.getUserProfileDtoRequest();
        var user = spy(ModelUtils.getUserWithUserLocation());
        var userLocation = user.getUserLocation();
        userLocation.getUsers().add(new User());
        var savedUserLocation = userLocation.setId(3L);

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(googleApiService.getLocationByCoordinates(
            request.getCoordinates().getLatitude(),
            request.getCoordinates().getLongitude(),
            languageUa, addressTypes))
            .thenReturn(ModelUtils.getGeocodingResult().getFirst());
        when(googleApiService.getLocationByCoordinates(
            request.getCoordinates().getLatitude(),
            request.getCoordinates().getLongitude(),
            languageEn, addressTypes))
            .thenReturn(ModelUtils.getGeocodingResult().getFirst());
        when(userLocationRepo.getUserLocationByLatitudeAndLongitude(
            request.getCoordinates().getLatitude(),
            request.getCoordinates().getLongitude())).thenReturn(Optional.of(userLocation));
        when(userLocationRepo.save(userLocation))
            .thenReturn(savedUserLocation);

        userService.setLocationForUser(userId, request);

        verify(userRepo).findById(userId);
        verify(googleApiService).getLocationByCoordinates(
            request.getCoordinates().getLatitude(),
            request.getCoordinates().getLongitude(),
            languageUa, addressTypes);
        verify(googleApiService).getLocationByCoordinates(
            request.getCoordinates().getLatitude(),
            request.getCoordinates().getLongitude(),
            languageEn, addressTypes);
        verify(userLocationRepo).getUserLocationByLatitudeAndLongitude(
            request.getCoordinates().getLatitude(),
            request.getCoordinates().getLongitude());
        verify(userLocationRepo).save(userLocation);
        verify(user).setUserLocation(savedUserLocation);
        verify(userRepo).save(user);
    }

    @Test
    void saveUserProfileUpdatesWithNullValuesTest() {
        Long userId = TestConst.USER_ID;
        UserProfileDtoRequest request = new UserProfileDtoRequest();
        request.setName(null);
        request.setUserCredo(null);
        request.setSocialNetworks(null);
        request.setShowLocation(null);
        request.setShowEcoPlace(null);
        request.setShowToDoList(null);
        request.setCoordinates(CoordinatesDto.builder().latitude(null).longitude(null).build());

        var user = ModelUtils.getUser();
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(userRepo.save(user)).thenReturn(user);

        userService.setLocationForUser(userId, request);

        verify(userRepo).findById(userId);
        verify(userRepo, never()).save(user);
    }

    @Test
    void saveUserProfileUpdatesWithNullLatitudeTest() {
        Long userId = TestConst.USER_ID;
        UserProfileDtoRequest request = new UserProfileDtoRequest();
        request.setName(null);
        request.setUserCredo(null);
        request.setSocialNetworks(null);
        request.setShowLocation(null);
        request.setShowEcoPlace(null);
        request.setShowToDoList(null);
        request.setCoordinates(CoordinatesDto.builder().latitude(null).longitude(1.0d).build());

        var myUser = ModelUtils.getUser();
        when(userRepo.findById(userId)).thenReturn(Optional.of(myUser));
        when(userRepo.save(myUser)).thenReturn(myUser);

        userService.setLocationForUser(userId, request);

        verify(userRepo).findById(userId);
        verify(userRepo, never()).save(myUser);
    }

    @Test
    void saveUserProfileUpdatesWithNullLongitudeTest() {
        Long userId = TestConst.USER_ID;
        UserProfileDtoRequest request = new UserProfileDtoRequest();
        request.setName(null);
        request.setUserCredo(null);
        request.setSocialNetworks(null);
        request.setShowLocation(null);
        request.setShowEcoPlace(null);
        request.setShowToDoList(null);
        request.setCoordinates(CoordinatesDto.builder().latitude(1.0d).longitude(null).build());

        var myUser = ModelUtils.getUser();
        when(userRepo.findById(userId)).thenReturn(Optional.of(myUser));
        when(userRepo.save(myUser)).thenReturn(myUser);

        userService.setLocationForUser(userId, request);

        verify(userRepo).findById(userId);
        verify(userRepo, never()).save(myUser);
    }

    @Test
    void saveUserProfileThrowWrongEmailExceptionTest() {
        Long userId = TestConst.USER_ID;
        var request = UserProfileDtoRequest.builder().build();

        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        var wrongIdException = assertThrows(
            WrongIdException.class,
            () -> userService.setLocationForUser(userId, request));

        assertEquals(ErrorMessage.USER_NOT_FOUND_BY_ID + userId, wrongIdException.getMessage());
        verify(userRepo).findById(userId);
    }

    @Test
    void saveUserProfileWhenLocationIsNotUpdatedTest() {
        Long userId = TestConst.USER_ID;
        UserProfileDtoRequest request = new UserProfileDtoRequest();
        request.setName("Dmutro");
        CoordinatesDto coordinates = new CoordinatesDto(20.0000, 20.0000);
        request.setCoordinates(coordinates);
        var user = ModelUtils.getUserWithUserLocation();
        user.getUserLocation().setUsers(Collections.singletonList(user));

        when(userLocationRepo.getUserLocationByLatitudeAndLongitude(
            request.getCoordinates().getLatitude(), request.getCoordinates().getLongitude()))
            .thenReturn(Optional.of(user.getUserLocation()));
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(userRepo.save(user)).thenReturn(user);
        when(userLocationRepo.save(user.getUserLocation())).thenReturn(user.getUserLocation());
        when(googleApiService.getLocationByCoordinates(
            request.getCoordinates().getLatitude(),
            request.getCoordinates().getLongitude(),
            languageUa, addressTypes))
            .thenReturn(ModelUtils.getGeocodingResult().getFirst());
        when(googleApiService.getLocationByCoordinates(
            request.getCoordinates().getLatitude(),
            request.getCoordinates().getLongitude(),
            languageEn, addressTypes))
            .thenReturn(ModelUtils.getGeocodingResult().getFirst());

        userService.setLocationForUser(userId, request);

        verify(userRepo).findById(userId);
        verify(userLocationRepo).getUserLocationByLatitudeAndLongitude(
            request.getCoordinates().getLatitude(), request.getCoordinates().getLongitude());
        verify(googleApiService, times(2)).getLocationByCoordinates(
            eq(request.getCoordinates().getLatitude()), eq(request.getCoordinates().getLongitude()), anyString(),
            aryEq(addressTypes));
        verify(userLocationRepo).save(any());
        verify(userRepo).save(user);
        verify(userLocationRepo, never()).delete(any());
    }

    @Test
    void findUserRatingTest() {
        Long userId = TestConst.USER_ID;
        Double rating = 1.;

        when(userRepo.existsById(userId))
            .thenReturn(true);
        when(userRepo.findRatingById(userId))
            .thenReturn(rating);

        Double actualResult = userService.findUserRating(userId);

        assertEquals(rating, actualResult);
        verify(userRepo).existsById(userId);
        verify(userRepo).findRatingById(userId);
    }

    @Test
    void findUserRatingWhenUserNotFoundTest() {
        Long userId = TestConst.USER_ID;
        String expectedExceptionMessage = ErrorMessage.USER_NOT_FOUND_BY_ID + userId;

        when(userRepo.existsById(userId))
            .thenReturn(false);

        var wrongIdException = assertThrows(
            WrongIdException.class,
            () -> userService.findUserRating(userId));

        assertEquals(expectedExceptionMessage, wrongIdException.getMessage());
        verify(userRepo).existsById(userId);
        verify(userRepo, never()).findRatingById(any());
    }

    @Test
    void increaseUserRatingTest() {
        Long userId = TestConst.USER_ID;
        UserAddRatingDto userAddRatingDto = new UserAddRatingDto(userId, 1.);
        User user = spy(ModelUtils.getTestUser());
        Double userRating = user.getRating();

        when(userRepo.findById(userId))
            .thenReturn(Optional.of(user));

        userService.increaseUserRating(userAddRatingDto);

        verify(userRepo).findById(userId);
        verify(user).setRating(userRating + userAddRatingDto.getRating());
    }

    @Test
    void increaseUserRatingWhenUserNotFoundTest() {
        Long userId = TestConst.USER_ID;
        UserAddRatingDto userAddRatingDto = new UserAddRatingDto(userId, 1.);
        User user = spy(ModelUtils.getTestUser());
        String expectedExceptionMessage = ErrorMessage.USER_NOT_FOUND_BY_ID + userId;

        when(userRepo.findById(userId))
            .thenReturn(Optional.empty());

        var wrongIdException = assertThrows(
            WrongIdException.class,
            () -> userService.increaseUserRating(userAddRatingDto));

        assertEquals(expectedExceptionMessage, wrongIdException.getMessage());
        verify(userRepo).findById(userId);
        verify(user, never()).setRating(any());
    }

    @Test
    void increaseUserRatingExternalTest() {
        User user = spy(ModelUtils.getUser());
        String email = user.getEmail();
        UserAddRatingExternalDto userAddRatingDto = new UserAddRatingExternalDto(email, 1.);
        Double userRating = user.getRating();

        when(userRepo.findByEmail(email))
            .thenReturn(Optional.of(user));

        userService.increaseUserRating(userAddRatingDto);

        verify(userRepo).findByEmail(email);
        verify(user).setRating(userRating + userAddRatingDto.getRating());
    }

    @Test
    void increaseUserRatingExternalWhenUserNotFoundTest() {
        User user = spy(ModelUtils.getUser());
        String email = user.getEmail();
        UserAddRatingExternalDto userAddRatingDto = new UserAddRatingExternalDto(email, 1.);
        String expectedExceptionMessage = ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email;

        when(userRepo.findByEmail(email))
            .thenReturn(Optional.empty());

        var notFoundException = assertThrows(
            NotFoundException.class,
            () -> userService.increaseUserRating(userAddRatingDto));

        assertEquals(expectedExceptionMessage, notFoundException.getMessage());
        verify(userRepo).findByEmail(email);
        verify(user, never()).setRating(any());
    }

    @Test
    void findAllUsersCitiesTest() {
        Long userId = TestConst.USER_ID;
        UserLocation userLocation = new UserLocation();
        UserCityDto userCityDto = new UserCityDto();

        when(userRepo.existsById(userId))
            .thenReturn(true);
        when(userLocationRepo.findAllUsersCities(userId))
            .thenReturn(Optional.of(userLocation));
        when(modelMapper.map(userLocation, UserCityDto.class))
            .thenReturn(userCityDto);

        UserCityDto actualResult = userService.findAllUsersCities(userId);

        assertEquals(userCityDto, actualResult);
        verify(userRepo).existsById(userId);
        verify(userLocationRepo).findAllUsersCities(userId);
        verify(modelMapper).map(userLocation, UserCityDto.class);
    }

    @Test
    void findAllUsersCitiesWhenUserDidNotSetLocationTest() {
        Long userId = TestConst.USER_ID;
        UserLocation userLocation = new UserLocation();
        String expectedExceptionMessage = ErrorMessage.USER_DID_NOT_SET_ANY_CITY;

        when(userRepo.existsById(userId))
            .thenReturn(true);
        when(userLocationRepo.findAllUsersCities(userId))
            .thenReturn(Optional.empty());

        var notFoundException = assertThrows(
            NotFoundException.class,
            () -> userService.findAllUsersCities(userId));

        assertEquals(expectedExceptionMessage, notFoundException.getMessage());
        verify(userRepo).existsById(userId);
        verify(userLocationRepo).findAllUsersCities(userId);
        verify(modelMapper, never()).map(userLocation, UserCityDto.class);
    }

    @Test
    void findAllUsersCitiesWhenUserNotFoundTest() {
        Long userId = TestConst.USER_ID;
        UserLocation userLocation = new UserLocation();
        String expectedExceptionMessage = ErrorMessage.USER_NOT_FOUND_BY_ID + userId;

        when(userRepo.existsById(userId))
            .thenReturn(false);

        var notFoundException = assertThrows(
            WrongIdException.class,
            () -> userService.findAllUsersCities(userId));

        assertEquals(expectedExceptionMessage, notFoundException.getMessage());
        verify(userRepo).existsById(userId);
        verify(userLocationRepo, never()).findAllUsersCities(userId);
        verify(modelMapper, never()).map(userLocation, UserCityDto.class);
    }

    @Test
    void findAllUsersCitiesByEmailTest() {
        User user = ModelUtils.getUser();
        Long userId = user.getId();
        String email = user.getEmail();
        UserLocation userLocation = new UserLocation();
        UserCityDto userCityDto = new UserCityDto();

        when(userRepo.findByEmail(email))
            .thenReturn(Optional.of(user));
        when(userRepo.existsById(userId))
            .thenReturn(true);
        when(userLocationRepo.findAllUsersCities(userId))
            .thenReturn(Optional.of(userLocation));
        when(modelMapper.map(userLocation, UserCityDto.class))
            .thenReturn(userCityDto);

        UserCityDto actualResult = userService.findAllUsersCities(email);

        assertEquals(userCityDto, actualResult);
        verify(userRepo).findByEmail(email);
        verify(userRepo).existsById(userId);
        verify(userLocationRepo).findAllUsersCities(userId);
        verify(modelMapper).map(userLocation, UserCityDto.class);
    }

    @Test
    void findAllUsersCitiesByEmailWhenUserDidNotSetLocationTest() {
        User user = ModelUtils.getUser();
        Long userId = user.getId();
        String email = user.getEmail();
        UserLocation userLocation = new UserLocation();
        String expectedExceptionMessage = ErrorMessage.USER_DID_NOT_SET_ANY_CITY;

        when(userRepo.findByEmail(email))
            .thenReturn(Optional.of(user));
        when(userRepo.existsById(userId))
            .thenReturn(true);
        when(userLocationRepo.findAllUsersCities(userId))
            .thenReturn(Optional.empty());

        var notFoundException = assertThrows(
            NotFoundException.class,
            () -> userService.findAllUsersCities(email));

        assertEquals(expectedExceptionMessage, notFoundException.getMessage());
        verify(userRepo).findByEmail(email);
        verify(userRepo).existsById(userId);
        verify(userLocationRepo).findAllUsersCities(userId);
        verify(modelMapper, never()).map(userLocation, UserCityDto.class);
    }

    @Test
    void findAllUsersCitiesByEmailWhenUserNotFoundTest() {
        User user = ModelUtils.getUser();
        Long userId = user.getId();
        String email = user.getEmail();
        UserLocation userLocation = new UserLocation();
        String expectedExceptionMessage = ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email;

        when(userRepo.findByEmail(email))
            .thenReturn(Optional.empty());

        var notFoundException = assertThrows(
            NotFoundException.class,
            () -> userService.findAllUsersCities(email));

        assertEquals(expectedExceptionMessage, notFoundException.getMessage());
        verify(userRepo).findByEmail(email);
        verify(userLocationRepo, never()).findAllUsersCities(userId);
        verify(modelMapper, never()).map(userLocation, UserCityDto.class);
    }

    @Test
    void findUserLocationDtoByUserIdTest() {
        Long userId = TestConst.USER_ID;
        UserLocation userLocation = new UserLocation();
        UserLocationDto userLocationDto = new UserLocationDto();

        when(userRepo.existsById(userId))
            .thenReturn(true);
        when(userLocationRepo.findAllUsersCities(userId))
            .thenReturn(Optional.of(userLocation));
        when(modelMapper.map(userLocation, UserLocationDto.class))
            .thenReturn(userLocationDto);

        UserLocationDto actualResult = userService.findUserLocationDtoByUserId(userId);

        assertEquals(userLocationDto, actualResult);
        verify(userRepo).existsById(userId);
        verify(userLocationRepo).findAllUsersCities(userId);
        verify(modelMapper).map(userLocation, UserLocationDto.class);
    }

    @Test
    void findUserLocationDtoByUserIdWhenUserDidNotSetLocationTest() {
        Long userId = TestConst.USER_ID;
        UserLocation userLocation = new UserLocation();
        String expectedExceptionMessage = ErrorMessage.USER_DID_NOT_SET_ANY_CITY;

        when(userRepo.existsById(userId))
            .thenReturn(true);
        when(userLocationRepo.findAllUsersCities(userId))
            .thenReturn(Optional.empty());

        var notFoundException = assertThrows(
            NotFoundException.class,
            () -> userService.findAllUsersCities(userId));

        assertEquals(expectedExceptionMessage, notFoundException.getMessage());
        verify(userRepo).existsById(userId);
        verify(userLocationRepo).findAllUsersCities(userId);
        verify(modelMapper, never()).map(userLocation, UserLocationDto.class);
    }

    @Test
    void findUserLocationDtoByUserIdWhenUserNotFoundTest() {
        Long userId = TestConst.USER_ID;
        UserLocation userLocation = new UserLocation();
        String expectedExceptionMessage = ErrorMessage.USER_NOT_FOUND_BY_ID + userId;

        when(userRepo.existsById(userId))
            .thenReturn(false);

        var notFoundException = assertThrows(
            WrongIdException.class,
            () -> userService.findAllUsersCities(userId));

        assertEquals(expectedExceptionMessage, notFoundException.getMessage());
        verify(userRepo).existsById(userId);
        verify(userLocationRepo, never()).findAllUsersCities(userId);
        verify(modelMapper, never()).map(userLocation, UserLocationDto.class);
    }

    @Test
    void updateUserCredoTest() {
        Long userId = 3L;
        String userCredo = "new user credo";
        UpdateUserCredoDto updateUserCredoDto = new UpdateUserCredoDto(userId, userCredo);

        userService.updateUserCredo(updateUserCredoDto);

        verify(userRepo).updateUserCredo(userId, userCredo);
    }

    @Test
    void getInitialsByIdTest() {
        Long id = 12L;
        User user = Mockito.mock(User.class);
        when(userRepo.findById(id)).thenReturn(Optional.of(user));
        when(user.getName())
            .thenReturn("Taras Tarasovich", "Taras");

        assertEquals("TT", userService.getInitialsById(id));
        assertEquals("T", userService.getInitialsById(id));
    }

    @Test
    void updateRoleTest() {
        Long userId = TestConst.USER_ID;
        Role role = Role.ROLE_USER;
        String email = TestConst.EMAIL;
        UserRoleDto expectedResult = new UserRoleDto(role);
        Map<String, String> body = Map.of("role", role.name());

        when(userRemoteClient.updateUserRole(userId, body))
            .thenReturn(Optional.of(expectedResult));

        UserRoleDto actualResult = userService.updateRole(userId, role, email);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void updateRoleWhenUserNotFoundTest() {
        Long userId = TestConst.USER_ID;
        Role role = Role.ROLE_USER;
        String email = TestConst.EMAIL;
        String expectedExceptionMessage = ErrorMessage.USER_NOT_FOUND_BY_ID + userId;
        Map<String, String> body = Map.of("role", role.name());

        when(userRemoteClient.updateUserRole(userId, body))
            .thenReturn(Optional.empty());

        var wrongIdException = assertThrows(
            WrongIdException.class,
            () -> userService.updateRole(userId, role, email));
        assertEquals(expectedExceptionMessage, wrongIdException.getMessage());
    }

    @Test
    void getAllUsersByCriteriaSortedPageableTest() {
        Page<User> userPage = getUserPage();
        Pageable sortedPageable = getSortedPageable();
        UserFilterDto request = getUserFilterDto();

        List<UserManagementVO> listUserManagementVO = getListUserManagementVO();
        Page<UserManagementVO> userManagementVOPage = getUserManagementVOPage();

        when(userRepo.findAll(any(UserSpecification.class), eq(sortedPageable))).thenReturn(userPage);
        when(userManagementVOMapper.mapAllToPage(getUserPage())).thenReturn(userManagementVOPage);

        PageableDetailedDto<UserManagementVO> allUsersByCriteria =
            userService.getAllUsersByCriteria(request, sortedPageable);

        assertTrue(allUsersByCriteria.getPage().containsAll(listUserManagementVO));

        verify(userRepo).findAll(any(UserSpecification.class), eq(sortedPageable));
        verify(userManagementVOMapper).mapAllToPage(getUserPage());
    }

    @Test
    void getAllUsersByCriteriaUnsortedPageableTest() {
        Page<User> userPage = getUserPage();
        Pageable unsortedPageable = getUnSortedPageable();
        Pageable sortedPageable = getSortedPageable();
        UserFilterDto request = getUserFilterDto();

        List<UserManagementVO> listUserManagementVO = getListUserManagementVO();
        Page<UserManagementVO> userManagementVOPage = getUserManagementVOPage();

        when(userRepo.findAll(any(UserSpecification.class), eq(sortedPageable))).thenReturn(userPage);
        when(userManagementVOMapper.mapAllToPage(getUserPage())).thenReturn(userManagementVOPage);

        PageableDetailedDto<UserManagementVO> allUsersByCriteria =
            userService.getAllUsersByCriteria(request, unsortedPageable);

        assertTrue(allUsersByCriteria.getPage().containsAll(listUserManagementVO));

        verify(userRepo).findAll(any(UserSpecification.class), eq(sortedPageable));
        verify(userManagementVOMapper).mapAllToPage(getUserPage());
    }

    @Test
    void updateUserRatingTest() {
        User user = getUser();
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepo).updateUserRating(1L, 6.0d);

        userService.updateUserRating(1L, 6.0d);

        verify(userRepo).findById(1L);
        verify(userRepo).updateUserRating(1L, 6.0d);
    }

    @Test
    void updateUserRatingsThrowsNotFoundExceptionTest() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(WrongIdException.class, () -> userService.updateUserRating(1L, 6.0d));
        verify(userRepo).findById(1L);
    }

    @Test
    void findByEmailsTest() {
        List<String> emails = List.of("email@gmail.com", "gmail@gmail.com");
        List<UserVO> expectedResult = List.of(userVO, userVO);

        when(userRemoteClient.findAllByEmailIn(emails))
            .thenReturn(expectedResult);

        assertEquals(expectedResult, userService.findByEmails(emails));

        verify(userRemoteClient).findAllByEmailIn(emails);
    }

    @Test
    void getSocialNetworkUrlByNameTest() {
        String expected = "url";
        List<SocialNetworkVO> socialNetworkVOs = ModelUtils.getListSocialNetworkVO();
        String actual = userService.getSocialNetworkUrlByName(socialNetworkVOs, "url");
        assertEquals(expected, actual);
    }

    @Test
    void getSocialNetworkUrlByNameNoResultTest() {
        List<SocialNetworkVO> socialNetworkVOs = ModelUtils.getListSocialNetworkVO();
        String actual = userService.getSocialNetworkUrlByName(socialNetworkVOs, "something");
        assertNull(actual);
    }

    @Test
    void createUserTest() {
        CreateGreenCityUserDto createGreenCityUserDto = ModelUtils.getCreateGreenCityDto();
        User createdUser = getUser();
        when(userRepo.existsById(createGreenCityUserDto.getId())).thenReturn(false);
        when(userRepo.save(any(User.class))).thenReturn(createdUser);

        Boolean result = userService.createUser(createGreenCityUserDto);

        assertTrue(result);
        verify(userRepo).existsById(createGreenCityUserDto.getId());
        verify(userRepo).save(any(User.class));
    }

    @Test
    void createUserIdAlreadyExistsTest() {
        CreateGreenCityUserDto createGreenCityUserDto = ModelUtils.getCreateGreenCityDto();
        when(userRepo.existsById(createGreenCityUserDto.getId())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(createGreenCityUserDto));

        verify(userRepo).existsById(createGreenCityUserDto.getId());
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    void createUserEmailAlreadyExistsTest() {
        CreateGreenCityUserDto createGreenCityUserDto = ModelUtils.getCreateGreenCityDto();
        when(userRepo.existsById(createGreenCityUserDto.getId())).thenReturn(false);
        when(userRepo.existsByEmail(createGreenCityUserDto.getEmail())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(createGreenCityUserDto));

        verify(userRepo).existsById(createGreenCityUserDto.getId());
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    void findByIdAdvancedTest() {
        User user = ModelUtils.getUser();
        UserVOAdvancedDto userVOAdvancedDto = ModelUtils.getUserVOAdvancedDto();
        Long userId = TestConst.USER_ID;

        when(userRepo.findById(userId))
            .thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserVOAdvancedDto.class))
            .thenReturn(userVOAdvancedDto);

        UserVOAdvancedDto actualResult = userService.findByIdAdvanced(userId);

        assertEquals(userVOAdvancedDto, actualResult);
        verify(userRepo).findById(userId);
        verify(modelMapper).map(user, UserVOAdvancedDto.class);
    }

    @Test
    void findByIdAdvancedWhenUserNotFoundTest() {
        Long userId = TestConst.USER_ID;
        String expectedExceptionMessage = ErrorMessage.USER_NOT_FOUND_BY_ID + userId;

        when(userRepo.findById(userId))
            .thenReturn(Optional.empty());

        var wrongIdException = assertThrows(
            WrongIdException.class,
            () -> userService.findByIdAdvanced(userId));

        assertEquals(expectedExceptionMessage, wrongIdException.getMessage());
        verify(userRepo).findById(userId);
        verify(modelMapper, never()).map(any(), eq(UserVOAdvancedDto.class));
    }

    @Test
    void updateUserProfilePictureTest() {
        String email = "test@email";
        String profilePicturePath = "http://newprofilepicture.com.ua";

        when(userRepo.updateUserProfilePictureByEmail(email, profilePicturePath))
            .thenReturn(1);

        userService.updateUserProfilePicture(email, profilePicturePath);

        verify(userRepo).updateUserProfilePictureByEmail(email, profilePicturePath);
    }

    @Test
    void updateUserProfilePictureUserNotFoundTest() {
        String email = "test@email";
        String profilePicturePath = "http://newprofilepicture.com.ua";

        when(userRepo.updateUserProfilePictureByEmail(email, profilePicturePath))
            .thenReturn(0);

        assertThrows(
            NotFoundException.class,
            () -> userService.updateUserProfilePicture(email, profilePicturePath));
    }

    @Test
    void updateUserNameTest() {
        Long userId = 1L;
        String userName = "userName";

        when(userRepo.updateUserName(userId, userName))
            .thenReturn(1);

        userService.updateUserName(userId, userName);

        verify(userRepo).updateUserName(userId, userName);
    }

    @Test
    void updateUserNameWhenUserNotFoundTest() {
        Long userId = 1L;
        String userName = "userName";

        when(userRepo.updateUserName(userId, userName))
            .thenReturn(0);

        assertThrows(
            NotFoundException.class,
            () -> userService.updateUserName(userId, userName));
    }

    @Test
    void updateUserNameByEmailTest() {
        User user = ModelUtils.getUser();
        Long userId = user.getId();
        String email = user.getEmail();
        String userName = "userName";

        when(userRepo.findByEmail(email))
            .thenReturn(Optional.of(user));
        when(userRepo.updateUserName(userId, userName))
            .thenReturn(1);

        userService.updateUserName(email, userName);

        verify(userRepo).findByEmail(email);
        verify(userRepo).updateUserName(userId, userName);
    }

    @Test
    void updateUserNameByEmailWhenUserNotFoundTest() {
        User user = ModelUtils.getUser();
        Long userId = user.getId();
        String email = user.getEmail();
        String userName = "userName";

        when(userRepo.findByEmail(email))
            .thenReturn(Optional.empty());
        when(userRepo.updateUserName(userId, userName))
            .thenReturn(0);

        assertThrows(
            NotFoundException.class,
            () -> userService.updateUserName(email, userName));
    }

    @Test
    void findGreenCityUserProfilesByUserIdsTest() {
        List<Long> userIds = List.of(1L, 2L);
        var greenCityProfiles = userIds.stream()
            .map(ModelUtils::getGreenCityUserProfileDtoResponse)
            .toList();

        when(userRepo.findGreenCityUserProfilesByUserIds(userIds))
            .thenReturn(greenCityProfiles);
        when(userLocationRepo.findAllUsersCities(anyLong()))
            .thenReturn(Optional.of(new UserLocation()));

        List<GreenCityUserProfileDtoResponse> actualResult = userService.findGreenCityUserProfilesByUserIds(userIds);

        assertEquals(greenCityProfiles, actualResult);
        verify(userRepo).findGreenCityUserProfilesByUserIds(userIds);
        verify(userLocationRepo, times(userIds.size())).findAllUsersCities(anyLong());
    }

    @Test
    void findGreenCityUserProfilesByUserIdsWhenUsersNotFoundTest() {
        List<Long> userIds = List.of(1L, 2L, 3L, 7L);
        var greenCityProfiles = userIds.stream()
            .map(ModelUtils::getGreenCityUserProfileDtoResponse)
            .limit(2)
            .toList();
        String expectedExceptionMessage = ErrorMessage.USERS_NOT_FOUND_BY_IDS + "3, 7";

        when(userRepo.findGreenCityUserProfilesByUserIds(userIds))
            .thenReturn(greenCityProfiles);

        NotFoundException notFoundException = assertThrows(
            NotFoundException.class,
            () -> userService.findGreenCityUserProfilesByUserIds(userIds));
        String actualExceptionMessage = notFoundException.getMessage();

        assertEquals(expectedExceptionMessage, actualExceptionMessage);
        verify(userRepo).findGreenCityUserProfilesByUserIds(userIds);
        verify(userLocationRepo, never()).findAllUsersCities(anyLong());
    }

    @Test
    void findGreenCityUserProfilesByEmailsTest() {
        List<String> emails = List.of("email1", "email2");
        List<Long> userIds = List.of(1L, 2L);
        var greenCityProfiles = userIds.stream()
            .map(ModelUtils::getGreenCityUserProfileDtoResponse)
            .toList();

        when(userRepo.getUserIdsByEmails(emails)).thenReturn(userIds);
        when(userRepo.findGreenCityUserProfilesByUserIds(userIds))
            .thenReturn(greenCityProfiles);
        when(userLocationRepo.findAllUsersCities(anyLong()))
            .thenReturn(Optional.of(new UserLocation()));

        List<GreenCityUserProfileDtoResponse> actualResult = userService.findGreenCityUserProfilesByEmails(emails);

        assertEquals(greenCityProfiles, actualResult);
        verify(userRepo).getUserIdsByEmails(emails);
        verify(userRepo).findGreenCityUserProfilesByUserIds(userIds);
        verify(userLocationRepo, times(userIds.size())).findAllUsersCities(anyLong());
    }

    @Test
    void findGreenCityUserProfilesByEmailsWhenUsersNotFoundTest() {
        List<String> emails = List.of("email1", "email2", "email3");
        List<Long> userIds = List.of(1L, 2L, 3L);
        var greenCityProfiles = userIds.stream()
            .map(ModelUtils::getGreenCityUserProfileDtoResponse)
            .limit(2)
            .toList();
        String expectedExceptionMessage = ErrorMessage.USERS_NOT_FOUND_BY_IDS + "3";

        when(userRepo.getUserIdsByEmails(emails)).thenReturn(userIds);
        when(userRepo.findGreenCityUserProfilesByUserIds(userIds))
            .thenReturn(greenCityProfiles);

        NotFoundException notFoundException = assertThrows(
            NotFoundException.class,
            () -> userService.findGreenCityUserProfilesByEmails(emails));
        String actualExceptionMessage = notFoundException.getMessage();

        assertEquals(expectedExceptionMessage, actualExceptionMessage);
        verify(userRepo).getUserIdsByEmails(emails);
        verify(userRepo).findGreenCityUserProfilesByUserIds(userIds);
        verify(userLocationRepo, never()).findAllUsersCities(anyLong());
    }

    @Test
    void fillGreenCityInfoInUsersTest() {
        List<UserManagementVO> users = List.of(
            UserManagementVO.builder().email("test1@email.com").build(),
            UserManagementVO.builder().email("test2@email.com").build());
        List<String> emails = List.of("test1@email.com", "test2@email.com");

        GreenCityUserInfoDto userInfo1 =
            new GreenCityUserInfoDto(1L, "test1@email.com", "path1", "credo1", ACTIVATED, 5.0);
        GreenCityUserInfoDto userInfo2 =
            new GreenCityUserInfoDto(2L, "test2@email.com", "path2", "credo2", ACTIVATED, 4.5);
        List<GreenCityUserInfoDto> userInfoList = List.of(userInfo1, userInfo2);

        when(userRepo.findGreenCityUserInfoDtosByEmails(emails)).thenReturn(userInfoList);

        userService.fillGreenCityInfoInUsers(users);

        verify(userRepo).findGreenCityUserInfoDtosByEmails(emails);
        assertEquals(1L, users.get(0).getId());
        assertEquals("path1", users.get(0).getProfilePicturePath());
        assertEquals("credo1", users.get(0).getUserCredo());
        assertEquals(ACTIVATED, users.get(0).getStatus());
        assertEquals(5.0, users.get(0).getRating());

        assertEquals(2L, users.get(1).getId());
        assertEquals("path2", users.get(1).getProfilePicturePath());
        assertEquals("credo2", users.get(1).getUserCredo());
        assertEquals(ACTIVATED, users.get(1).getStatus());
        assertEquals(4.5, users.get(1).getRating());
    }

    @Test
    void fillGreenCityInfoInUsersWithNullUsersTest() {
        List<UserManagementVO> users = new ArrayList<>();
        users.add(null);
        users.add(UserManagementVO.builder().email("test@email.com").build());

        GreenCityUserInfoDto userInfo = new GreenCityUserInfoDto(1L, "test@email.com", "path", "credo", ACTIVATED, 5.0);

        when(userRepo.findGreenCityUserInfoDtosByEmails(List.of("test@email.com")))
            .thenReturn(List.of(userInfo));

        userService.fillGreenCityInfoInUsers(users);

        verify(userRepo).findGreenCityUserInfoDtosByEmails(List.of("test@email.com"));
        assertEquals(1L, users.get(1).getId());
    }

    @Test
    void getUserStatusByEmailTest() {
        String email = "test@email.com";
        User user = ModelUtils.getUser();
        user.setStatus(ACTIVATED);

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));

        UserStatus actualStatus = userService.getUserStatusByEmail(email);

        assertEquals(ACTIVATED, actualStatus);
        verify(userRepo).findByEmail(email);
    }

    @Test
    void getUserStatusByEmailWhenUserNotFoundTest() {
        String email = "test@email.com";
        String expectedExceptionMessage = ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email;

        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(
            NotFoundException.class,
            () -> userService.getUserStatusByEmail(email));

        assertEquals(expectedExceptionMessage, notFoundException.getMessage());
        verify(userRepo).findByEmail(email);
    }

    @Test
    void deleteUserByEmailTest() {
        String email = "test@email.com";
        User user = ModelUtils.getUser();
        user.setStatus(ACTIVATED);

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepo.save(user)).thenReturn(user);

        userService.deleteUserByEmail(email);

        verify(userRepo).findByEmail(email);
        verify(userRepo).save(user);
        assertEquals(UserStatus.DELETED, user.getStatus());
    }

    @Test
    void deleteUserByEmailWhenUserNotFoundTest() {
        String email = "test@email.com";
        String expectedExceptionMessage = ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email;

        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(
            NotFoundException.class,
            () -> userService.deleteUserByEmail(email));

        assertEquals(expectedExceptionMessage, notFoundException.getMessage());
        verify(userRepo).findByEmail(email);
        verify(userRepo, never()).save(any());
    }

    @Test
    void deleteUserByEmailWhenUserDeactivatedTest() {
        String email = "test@email.com";
        User user = ModelUtils.getUser();
        user.setStatus(UserStatus.DEACTIVATED);

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));

        ForbiddenException forbiddenException = assertThrows(
            ForbiddenException.class,
            () -> userService.deleteUserByEmail(email));

        assertEquals(ErrorMessage.FORBIDDEN_USER_DELETION, forbiddenException.getMessage());
        verify(userRepo).findByEmail(email);
        verify(userRepo, never()).save(any());
    }

    @Test
    void deleteUserByEmailWhenUserBlockedTest() {
        String email = "test@email.com";
        User user = ModelUtils.getUser();
        user.setStatus(UserStatus.BLOCKED);

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));

        ForbiddenException forbiddenException = assertThrows(
            ForbiddenException.class,
            () -> userService.deleteUserByEmail(email));

        assertEquals(ErrorMessage.FORBIDDEN_USER_DELETION, forbiddenException.getMessage());
        verify(userRepo).findByEmail(email);
        verify(userRepo, never()).save(any());
    }

    @Test
    void countAllByStatusTest() {
        UserStatus status = ACTIVATED;
        long expectedCount = 5L;

        when(userRepo.countAllByStatus(status)).thenReturn(expectedCount);

        long actualCount = userService.countAllByStatus(status);

        assertEquals(expectedCount, actualCount);
        verify(userRepo).countAllByStatus(status);
    }

    @Test
    void deactivateAllUsersTest() {
        List<Long> userIds = List.of(1L, 2L, 3L);
        UserVO currentUser = UserVO.builder()
            .id(10L)
            .name("Admin User")
            .email("admin@email.com")
            .role(Role.ROLE_ADMIN)
            .build();

        UserService spyUserService = spy(userService);
        doNothing().when(spyUserService).deactivateUserByIdWithReasons(eq(currentUser), anyLong(), any());

        List<Long> result = spyUserService.deactivateAllUsers(userIds, currentUser);

        assertEquals(userIds, result);
        verify(spyUserService, times(3)).deactivateUserByIdWithReasons(eq(currentUser), anyLong(), any());
    }

    @Test
    void updateUserStatusByIdActivateTest() {
        Long targetUserId = 2L;
        UserVO currentUser = UserVO.builder()
            .id(1L)
            .name("Admin")
            .email("admin@email.com")
            .role(Role.ROLE_ADMIN)
            .build();

        User targetUser = ModelUtils.getUser();
        targetUser.setId(targetUserId);
        targetUser.setEmail("target@email.com");
        targetUser.setStatus(UserStatus.DEACTIVATED);

        UserVO targetUserVO = UserVO.builder()
            .id(targetUserId)
            .email("target@email.com")
            .role(Role.ROLE_USER)
            .build();

        String userLanguage = "en";

        when(userRemoteClient.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        when(userRepo.findById(targetUserId)).thenReturn(Optional.of(targetUser));
        when(userRemoteClient.findByEmail(targetUser.getEmail())).thenReturn(Optional.of(targetUserVO));
        when(userRemoteClient.findUserLanguageByEmail(targetUser.getEmail())).thenReturn(userLanguage);
        doNothing().when(userRemoteClient).sendMessageOfActivation(any());
        when(userRepo.save(targetUser)).thenReturn(targetUser);

        userService.updateUserStatusById(currentUser, targetUserId, ACTIVATED);

        assertEquals(ACTIVATED, targetUser.getStatus());
        verify(userRemoteClient).findByEmail(currentUser.getEmail());
        verify(userRepo).findById(targetUserId);
        verify(userRemoteClient).findByEmail(targetUser.getEmail());
        verify(userRemoteClient).findUserLanguageByEmail(targetUser.getEmail());
        verify(userRemoteClient).sendMessageOfActivation(any());
        verify(userRepo).save(targetUser);
    }

    @Test
    void updateUserStatusByIdDeactivateTest() {
        Long targetUserId = 2L;
        UserVO currentUser = UserVO.builder()
            .id(1L)
            .name("Admin")
            .email("admin@email.com")
            .role(Role.ROLE_ADMIN)
            .build();

        User targetUser = ModelUtils.getUser();
        targetUser.setId(targetUserId);
        targetUser.setEmail("target@email.com");
        targetUser.setStatus(ACTIVATED);

        UserVO targetUserVO = UserVO.builder()
            .id(targetUserId)
            .email("target@email.com")
            .role(Role.ROLE_USER)
            .build();

        String userLanguage = "en";

        when(userRemoteClient.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        when(userRepo.findById(targetUserId)).thenReturn(Optional.of(targetUser));
        when(userRemoteClient.findByEmail(targetUser.getEmail())).thenReturn(Optional.of(targetUserVO));
        when(userRemoteClient.findUserLanguageByEmail(targetUser.getEmail())).thenReturn(userLanguage);
        doNothing().when(userRemoteClient).sendReasonOfDeactivation(any());
        when(userRepo.save(targetUser)).thenReturn(targetUser);
        when(userDeactivationRepo.save(any())).thenReturn(any());

        userService.updateUserStatusById(currentUser, targetUserId, UserStatus.DEACTIVATED);

        assertEquals(UserStatus.DEACTIVATED, targetUser.getStatus());
        verify(userRemoteClient).findByEmail(currentUser.getEmail());
        verify(userRepo).findById(targetUserId);
        verify(userRemoteClient).findByEmail(targetUser.getEmail());
        verify(userRemoteClient).findUserLanguageByEmail(targetUser.getEmail());
        verify(userRemoteClient).sendReasonOfDeactivation(any());
        verify(userDeactivationRepo).save(any());
        verify(userRepo).save(targetUser);
    }

    @Test
    void updateUserStatusByIdWhenCurrentUserNotFoundTest() {
        UserVO currentUser = UserVO.builder()
            .email("admin@email.com")
            .build();
        String expectedExceptionMessage = ErrorMessage.USER_NOT_FOUND_BY_EMAIL + currentUser.getEmail();

        when(userRemoteClient.findByEmail(currentUser.getEmail())).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(
            NotFoundException.class,
            () -> userService.updateUserStatusById(currentUser, 2L, ACTIVATED));

        assertEquals(expectedExceptionMessage, notFoundException.getMessage());
        verify(userRemoteClient).findByEmail(currentUser.getEmail());
    }

    @Test
    void updateUserStatusByIdWhenTargetUserNotFoundTest() {
        Long targetUserId = 2L;
        UserVO currentUser = UserVO.builder()
            .id(1L)
            .email("admin@email.com")
            .build();
        String expectedExceptionMessage = ErrorMessage.USER_NOT_FOUND_BY_ID + targetUserId;

        when(userRemoteClient.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        when(userRepo.findById(targetUserId)).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(
            NotFoundException.class,
            () -> userService.updateUserStatusById(currentUser, targetUserId, ACTIVATED));

        assertEquals(expectedExceptionMessage, notFoundException.getMessage());
        verify(userRemoteClient).findByEmail(currentUser.getEmail());
        verify(userRepo).findById(targetUserId);
    }

    @Test
    void updateUserStatusByIdWhenUserTriesToDeactivateThemselveTest() {
        Long userId = 1L;
        UserVO currentUser = UserVO.builder()
            .id(userId)
            .email("user@email.com")
            .build();

        when(userRemoteClient.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));

        UserStatusUpdateException exception = assertThrows(
            UserStatusUpdateException.class,
            () -> userService.updateUserStatusById(currentUser, userId, UserStatus.DEACTIVATED));

        assertEquals(ErrorMessage.USER_CANNOT_DEACTIVATE_YOURSELF, exception.getMessage());
        verify(userRemoteClient).findByEmail(currentUser.getEmail());
    }

    @Test
    void updateUserStatusByIdWhenAdminTriesToDeactivateAnotherAdminTest() {
        Long targetUserId = 2L;
        UserVO currentUser = UserVO.builder()
            .id(1L)
            .email("admin1@email.com")
            .role(Role.ROLE_ADMIN)
            .build();

        User targetUser = ModelUtils.getUser();
        targetUser.setId(targetUserId);
        targetUser.setEmail("admin2@email.com");

        UserVO targetUserVO = UserVO.builder()
            .id(targetUserId)
            .email("admin2@email.com")
            .role(Role.ROLE_ADMIN)
            .build();

        when(userRemoteClient.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        when(userRepo.findById(targetUserId)).thenReturn(Optional.of(targetUser));
        when(userRemoteClient.findByEmail(targetUser.getEmail())).thenReturn(Optional.of(targetUserVO));

        UserStatusUpdateException exception = assertThrows(
            UserStatusUpdateException.class,
            () -> userService.updateUserStatusById(currentUser, targetUserId, UserStatus.DEACTIVATED));

        assertEquals(ErrorMessage.ADMIN_CANNOT_DEACTIVATE_OTHER_ADMIN, exception.getMessage());
        verify(userRemoteClient).findByEmail(currentUser.getEmail());
        verify(userRepo).findById(targetUserId);
        verify(userRemoteClient).findByEmail(targetUser.getEmail());
    }

    @Test
    void deactivateUserByIdWithReasonsTest() {
        Long targetUserId = 2L;
        UserVO currentUser = UserVO.builder()
            .id(1L)
            .name("Admin")
            .email("admin@email.com")
            .role(Role.ROLE_ADMIN)
            .build();

        User targetUser = ModelUtils.getUser();
        targetUser.setId(targetUserId);
        targetUser.setEmail("target@email.com");
        targetUser.setStatus(ACTIVATED);

        UserVO targetUserVO = UserVO.builder()
            .id(targetUserId)
            .email("target@email.com")
            .role(Role.ROLE_USER)
            .build();

        List<String> reasons = List.of("Reason 1", "Reason 2");
        String userLanguage = "en";

        when(userRemoteClient.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        when(userRepo.findById(targetUserId)).thenReturn(Optional.of(targetUser));
        when(userRemoteClient.findByEmail(targetUser.getEmail())).thenReturn(Optional.of(targetUserVO));
        when(userRemoteClient.findUserLanguageByEmail(targetUser.getEmail())).thenReturn(userLanguage);
        doNothing().when(userRemoteClient).sendReasonOfDeactivation(any());
        when(userRepo.save(targetUser)).thenReturn(targetUser);
        when(userDeactivationRepo.save(any())).thenReturn(any());

        userService.deactivateUserByIdWithReasons(currentUser, targetUserId, reasons);

        assertEquals(UserStatus.DEACTIVATED, targetUser.getStatus());
        verify(userRemoteClient).findByEmail(currentUser.getEmail());
        verify(userRepo).findById(targetUserId);
        verify(userRemoteClient).findByEmail(targetUser.getEmail());
        verify(userRemoteClient).findUserLanguageByEmail(targetUser.getEmail());
        verify(userRemoteClient).sendReasonOfDeactivation(any());
        verify(userDeactivationRepo, times(2)).save(any()); // Called for each reason
        verify(userRepo).save(targetUser);
    }

    @Test
    void getDeactivationReasonsTest() {
        Long userId = 1L;
        UserVO currentUser = UserVO.builder()
            .email("admin@email.com")
            .build();

        UserDeactivationReason reason = UserDeactivationReason.builder()
            .reason("{en}Spam/Inappropriate Content{en}/{uk}Спам/Неприйнятний контент{uk}")
            .build();

        when(userDeactivationRepo.getLastDeactivationReason(userId)).thenReturn(Optional.of(reason));
        when(userRemoteClient.findUserLanguageByEmail(currentUser.getEmail())).thenReturn("en");

        List<String> actualReasons = userService.getDeactivationReasons(userId, currentUser);

        assertEquals(List.of("Spam", "Inappropriate Content"), actualReasons);
        verify(userDeactivationRepo).getLastDeactivationReason(userId);
        verify(userRemoteClient).findUserLanguageByEmail(currentUser.getEmail());
    }

    @Test
    void getDeactivationReasonsUkrainianTest() {
        Long userId = 1L;
        UserVO currentUser = UserVO.builder()
            .email("admin@email.com")
            .build();

        UserDeactivationReason reason = UserDeactivationReason.builder()
            .reason("{en}Spam/Inappropriate Content{en}/{uk}Спам/Неприйнятний контент{uk}")
            .build();

        when(userDeactivationRepo.getLastDeactivationReason(userId)).thenReturn(Optional.of(reason));
        when(userRemoteClient.findUserLanguageByEmail(currentUser.getEmail())).thenReturn("uk");

        List<String> actualReasons = userService.getDeactivationReasons(userId, currentUser);

        assertEquals(List.of("Спам", "Неприйнятний контент"), actualReasons);
        verify(userDeactivationRepo).getLastDeactivationReason(userId);
        verify(userRemoteClient).findUserLanguageByEmail(currentUser.getEmail());
    }

    @Test
    void getDeactivationReasonsWhenReasonsEmptyTest() {
        Long userId = 1L;
        UserVO currentUser = UserVO.builder()
            .email("admin@email.com")
            .build();

        when(userDeactivationRepo.getLastDeactivationReason(userId)).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(
            NotFoundException.class,
            () -> userService.getDeactivationReasons(userId, currentUser));

        assertEquals(ErrorMessage.USER_DEACTIVATION_REASON_IS_EMPTY, notFoundException.getMessage());
        verify(userDeactivationRepo).getLastDeactivationReason(userId);
    }

    @Test
    void getActivatedUsersAmountTest() {
        long expectedCount = 100L;

        when(userRepo.countAllByStatus(ACTIVATED)).thenReturn(expectedCount);

        long actualCount = userService.getActivatedUsersAmount();

        assertEquals(expectedCount, actualCount);
        verify(userRepo).countAllByStatus(ACTIVATED);
    }
}