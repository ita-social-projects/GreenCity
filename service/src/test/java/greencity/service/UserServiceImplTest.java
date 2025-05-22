package greencity.service;

import greencity.ModelUtils;
import greencity.TestConst;
import greencity.client.UserRemoteClient;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDetailedDto;
import greencity.dto.location.UserLocationDto;
import greencity.dto.socialnetwork.SocialNetworkVO;
import greencity.dto.user.UpdateUserCredoDto;
import greencity.dto.user.UserFilterDto;
import greencity.dto.user.UserManagementVO;
import greencity.dto.user.UserStatusDto;
import greencity.dto.user.UserVO;
import greencity.dto.user.CreateGreenCityUserDto;
import greencity.entity.User;
import greencity.entity.UserLocation;
import greencity.enums.Role;
import greencity.exception.exceptions.BadUpdateRequestException;
import greencity.exception.exceptions.LowRoleLevelException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.WrongEmailException;
import greencity.exception.exceptions.WrongIdException;
import greencity.exception.exceptions.UserAlreadyExistsException;
import greencity.enums.UserStatus;
import greencity.mapping.UserManagementVOMapper;
import greencity.repository.UserRepo;
import greencity.repository.options.UserFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.List;
import java.util.Optional;
import static greencity.ModelUtils.getListUserManagementVO;
import static greencity.ModelUtils.getSortedPageable;
import static greencity.ModelUtils.getUnSortedPageable;
import static greencity.ModelUtils.getUserFilterDto;
import static greencity.ModelUtils.getUserManagementVOPage;
import static greencity.ModelUtils.getUserPage;
import static greencity.ModelUtils.getUserVO;
import static greencity.ModelUtils.testEmail;
import static greencity.ModelUtils.testEmail2;
import static greencity.ModelUtils.testUser;
import static greencity.ModelUtils.testUserRoleUser;
import static greencity.ModelUtils.testUserStatusDto;
import static greencity.ModelUtils.testUserVo;
import static greencity.ModelUtils.userVORoleUser;
import static greencity.ModelUtils.getUser;
import static greencity.enums.UserStatus.ACTIVATED;
import static greencity.enums.UserStatus.CREATED;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
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
    UserManagementVOMapper userManagementVOMapper;

    private final UserVO userVO = UserVO.builder()
        .id(1L)
        .name("Test Testing")
        .email("test@gmail.com")
        .role(Role.ROLE_USER)
        .userStatus(ACTIVATED)
        .build();

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
    void saveTest() {
        userService.save(userVO);
        verify(modelMapper).map(userVO, User.class);
        verify(userRepo).save(any());
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
    void checkUpdatableUserTest() {
        Long userId = 1L;
        String email = "email";
        User user = getUser();
        UserLocation userLocation = user.getUserLocation();
        UserLocationDto userLocationDto = new UserLocationDto();

        // when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserVO.class)).thenReturn(userVO);
        when(modelMapper.map(userLocation, UserLocationDto.class)).thenReturn(userLocationDto);
        Exception exception = assertThrows(BadUpdateRequestException.class, () -> {
            userService.checkUpdatableUser(userId, user.getId());
        });
        assertEquals(ErrorMessage.USER_CANT_UPDATE_HIMSELF, exception.getMessage());
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
    void findUserCredoByUserIdTest() {
        Long userId = 5L;
        String expectedResult = "my user credo";

        when(userRepo.findUserCredoByUserId(userId))
            .thenReturn(expectedResult);

        String actualResult = userService.findUserCredoByUserId(userId);

        assertEquals(expectedResult, actualResult);
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
    void testFindNotDeactivatedByEmail() {
        when(userRemoteClient.findNotDeactivatedByEmail(testEmail))
                .thenReturn(Optional.of(testUserVo));

        UserVO actual = userService.findNotDeactivatedByEmail(testEmail);

        assertEquals(testUserVo, actual);
    }

    @Test
    void testFindNotDeactivatedByEmailThrowException() {
        when(userRemoteClient.findNotDeactivatedByEmail(testEmail))
                .thenReturn(Optional.empty());

        assertThrows(
                WrongEmailException.class,
                () -> userService.findNotDeactivatedByEmail(testEmail)
        );
    }

    @Test
    void testUpdateStatus() {
        UserStatusDto userStatusDto = UserStatusDto.builder()
            .id(2L)
            .userStatus(UserStatus.CREATED)
            .build();

        when(userRepo.findById(TestConst.USER_ID)).thenReturn(Optional.ofNullable(testUser));
        when(modelMapper.map(testUser, UserVO.class)).thenReturn(testUserVo);
        when(userRepo.findById(2L)).thenReturn(Optional.ofNullable(testUserRoleUser));
        when(modelMapper.map(testUserRoleUser, UserVO.class)).thenReturn(userVORoleUser);
        when(modelMapper.map(userVORoleUser, UserStatusDto.class)).thenReturn(testUserStatusDto);

        UserStatusDto actual = userService.updateStatus(2L, CREATED, TestConst.USER_ID);

        assertEquals(testUserStatusDto, actual);

        verify(userRepo, times(3)).findById(anyLong());
        verify(modelMapper, times(3)).map(any(User.class), eq(UserVO.class));
        verify(userRemoteClient).updateUserStatus(userStatusDto);
        verify(modelMapper).map(userVORoleUser, UserStatusDto.class);
    }

    @Test
    void testUpdateStatusThrowsBadUpdateRequestException() {
        when(userRepo.findById(TestConst.USER_ID)).thenReturn(Optional.ofNullable(testUser));
        when(modelMapper.map(testUser, UserVO.class)).thenReturn(testUserVo);

        assertThrows(BadUpdateRequestException.class,
            () -> userService.updateStatus(TestConst.USER_ID, CREATED, TestConst.USER_ID));
    }

    @Test
    void testUpdateStatusThrowsLowRoleLevelException() {
         when(userRepo.findById(TestConst.USER_ID)).thenReturn(Optional.ofNullable(testUser));
        when(modelMapper.map(testUser, UserVO.class)).thenReturn(testUserVo);
        when(userRepo.findById(2L)).thenReturn(Optional.ofNullable(testUser));
        when(modelMapper.map(testUser, UserVO.class)).thenReturn(testUserVo);

        assertThrows(LowRoleLevelException.class,
            () -> userService.updateStatus(2L, CREATED, TestConst.USER_ID));
    }

    @Test
    void getAllUsersByCriteriaSortedPageableTest() {
        Page<User> userPage = getUserPage();
        Pageable sortedPageable = getSortedPageable();
        UserFilterDto request = getUserFilterDto();

        List<UserManagementVO> listUserManagementVO = getListUserManagementVO();
        Page<UserManagementVO> userManagementVOPage = getUserManagementVOPage();

        when(userRepo.findAll(any(UserFilter.class), eq(sortedPageable))).thenReturn(userPage);
        when(userManagementVOMapper.mapAllToPage(getUserPage())).thenReturn(userManagementVOPage);

        PageableDetailedDto<UserManagementVO> allUsersByCriteria =
            userService.getAllUsersByCriteria(request, sortedPageable);

        assertTrue(allUsersByCriteria.getPage().containsAll(listUserManagementVO));

        verify(userRepo).findAll(any(UserFilter.class), eq(sortedPageable));
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

        when(userRepo.findAll(any(UserFilter.class), eq(sortedPageable))).thenReturn(userPage);
        when(userManagementVOMapper.mapAllToPage(getUserPage())).thenReturn(userManagementVOPage);

        PageableDetailedDto<UserManagementVO> allUsersByCriteria =
            userService.getAllUsersByCriteria(request, unsortedPageable);

        assertTrue(allUsersByCriteria.getPage().containsAll(listUserManagementVO));

        verify(userRepo).findAll(any(UserFilter.class), eq(sortedPageable));
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
        assertThrows(NotFoundException.class, () -> userService.updateUserRating(1L, 6.0d));
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
    void createUserAlreadyExistsTest() {
        CreateGreenCityUserDto createGreenCityUserDto = ModelUtils.getCreateGreenCityDto();
        when(userRepo.existsById(createGreenCityUserDto.getId())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(createGreenCityUserDto));

        verify(userRepo).existsById(createGreenCityUserDto.getId());
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    void updateUserProfilePictureTest() {
        Long userId = 1L;
        String profilePicturePath = "http://newprofilepicture.com.ua";

        when(userRepo.updateUserProfilePictureByUserId(1L, profilePicturePath))
                .thenReturn(1);

        userService.updateUserProfilePicture(userId, profilePicturePath);

        verify(userRepo).updateUserProfilePictureByUserId(userId, profilePicturePath);
    }

    @Test
    void updateUserProfilePictureUserNotFoundTest() {
        Long userId = 1L;
        String profilePicturePath = "http://newprofilepicture.com.ua";

        when(userRepo.updateUserProfilePictureByUserId(1L, profilePicturePath))
                .thenReturn(0);

        assertThrows(
            NotFoundException.class,
            () -> userService.updateUserProfilePicture(userId, profilePicturePath));
    }

    @Test
    void getProfilePicturePathTest() {
        User user = getUser();
        String profilePicturePath = "http://testprofilepicture.com.ua";
        user.setProfilePicturePath(profilePicturePath);

        when(userRepo.existsById(1L)).thenReturn(true);
        when(userRepo.findProfilePicturePathByUserId(1L)).thenReturn(profilePicturePath);

        String result = userService.getProfilePicturePath(1L);

        assertEquals(profilePicturePath, result);
    }

    @Test
    void getProfilePicturePathUserNotFoundTest() {
        when(userRepo.existsById(999L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> userService.getProfilePicturePath(999L));
    }
}