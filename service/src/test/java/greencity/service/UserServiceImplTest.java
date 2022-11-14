package greencity.service;

import greencity.ModelUtils;
import greencity.constant.ErrorMessage;
import greencity.dto.user.UserManagementVO;
import greencity.dto.user.UserStatusDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.enums.EmailNotification;
import greencity.enums.Role;
import greencity.exception.exceptions.BadUpdateRequestException;
import greencity.exception.exceptions.WrongIdException;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.*;
import greencity.repository.UserRepo;
import greencity.repository.options.UserFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static greencity.ModelUtils.*;
import static greencity.enums.UserStatus.ACTIVATED;
import static greencity.enums.UserStatus.CREATED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepo userRepo;
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private ModelMapper modelMapper;

    private UserVO userVO = UserVO.builder()
        .id(1L)
        .name("Test Testing")
        .email("test@gmail.com")
        .role(Role.ROLE_USER)
        .userStatus(ACTIVATED)
        .emailNotification(EmailNotification.DISABLED)
        .lastActivityTime(LocalDateTime.of(2020, 10, 10, 20, 10, 10))
        .dateOfRegistration(LocalDateTime.now())
        .socialNetworks(new ArrayList<>())
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
    void checkIfTheUserIsOnlineExceptionTest() {
        assertThrows(WrongIdException.class, () -> userService.checkIfTheUserIsOnline(null));
    }

    @Test
    void checkIfTheUserIsOnlineEqualsTrueTest() {
        ReflectionTestUtils.setField(userService, "timeAfterLastActivity", 300000);
        Timestamp userLastActivityTime = Timestamp.valueOf(LocalDateTime.now());
        User user = ModelUtils.getUser();

        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepo.findLastActivityTimeById(anyLong())).thenReturn(Optional.of(userLastActivityTime));

        assertTrue(userService.checkIfTheUserIsOnline(1L));
    }

    @Test
    void checkIfTheUserIsOnlineEqualsFalseTest() {
        ReflectionTestUtils.setField(userService, "timeAfterLastActivity", 300000);
        LocalDateTime localDateTime = LocalDateTime.of(
            2015, Month.JULY, 29, 19, 30, 40);
        Timestamp userLastActivityTime = Timestamp.valueOf(localDateTime);
        User user = ModelUtils.getUser();

        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepo.findLastActivityTimeById(anyLong())).thenReturn(Optional.empty());

        assertFalse(userService.checkIfTheUserIsOnline(1L));
    }

    @Test
    void getSixFriendsWithTheHighestRatingTest() {
        List<User> friendsList = ModelUtils.getFriendsList();
        User user = User.builder()
            .id(1L)
            .userFriends(friendsList)
            .build();

        userVO.setUserFriends(friendsList.stream()
            .map(friend -> modelMapper.map(friend, UserVO.class))
            .collect(Collectors.toList()));

        when(userRepo.getSixFriendsWithTheHighestRating(user.getId())).thenReturn(user.getUserFriends().stream()
            .sorted((f1, f2) -> f2.getRating().compareTo(f1.getRating()))
            .limit(6)
            .collect(Collectors.toList()));

        assertEquals(userVO.getUserFriends().subList(2, 8),
            userService.getSixFriendsWithTheHighestRating(user.getId()));
    }

    @Test
    void checkUpdatableUserTest() {
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(ModelUtils.getUser()));
        when(modelMapper.map(any(), any())).thenReturn(userVO);
        Exception exception = assertThrows(BadUpdateRequestException.class, () -> {
            userService.checkUpdatableUser(1L, "email");
        });
        assertEquals(ErrorMessage.USER_CANT_UPDATE_HIMSELF, exception.getMessage());
    }

    @Test
    void getInitialsByIdTest() {
        when(userRepo.findById(any())).thenReturn(Optional.of(ModelUtils.getUser()));
        when(modelMapper.map(any(), any())).thenReturn(userVO);
        assertEquals("TT", userService.getInitialsById(12L));
        userVO.setName("Taras");
        assertEquals("T", userService.getInitialsById(12L));
    }

    @Test
    void testFindByEmail() {
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.ofNullable(TEST_USER));
        when(modelMapper.map(TEST_USER, UserVO.class)).thenReturn(TEST_USER_VO);

        UserVO actual = userService.findByEmail(TEST_EMAIL);

        assertEquals(TEST_USER_VO, actual);

        verify(userRepo).findByEmail(TEST_EMAIL);
        verify(modelMapper).map(TEST_USER, UserVO.class);
    }

    @Test
    void testFindByEmailReturnNull() {
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        UserVO actual = userService.findByEmail(TEST_EMAIL);

        assertNull(actual);

        verify(userRepo).findByEmail(TEST_EMAIL);
    }

    @Test
    void testFindNotDeactivatedByEmail() {
        when(userRepo.findNotDeactivatedByEmail(TEST_EMAIL))
            .thenReturn(Optional.of(TEST_USER));
        when(modelMapper.map(Optional.of(TEST_USER), UserVO.class))
            .thenReturn(TEST_USER_VO);

        Optional<UserVO> actual = userService.findNotDeactivatedByEmail(TEST_EMAIL);

        assertEquals(Optional.of(TEST_USER_VO), actual);
    }

    @Test
    void testFindIdByEmail() {
        when(userRepo.findIdByEmail(TEST_EMAIL)).thenReturn(Optional.of(1L));

        Long actual = userService.findIdByEmail(TEST_EMAIL);

        assertEquals(1L, actual);
    }

    @Test
    void testFindIdByEmailThrowsException() {
        when(userRepo.findIdByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        assertThrows(WrongEmailException.class,
            () -> userService.findIdByEmail(TEST_EMAIL));
    }

    @Test
    void testUpdateUserLastActivityTime() {
        Date date = new Date();

        doNothing().when(userRepo).updateUserLastActivityTime(1L, date);

        userService.updateUserLastActivityTime(1L, date);

        verify(userRepo).updateUserLastActivityTime(1L, date);
    }

    @Test
    void testUpdateStatus() {
        when(userRepo.findByEmail(TEST_EMAIL_2)).thenReturn(Optional.ofNullable(TEST_USER));
        when(modelMapper.map(TEST_USER, UserVO.class)).thenReturn(TEST_USER_VO);
        when(userRepo.findById(2L)).thenReturn(Optional.ofNullable(TEST_USER_ROLE_USER));
        when(modelMapper.map(TEST_USER_ROLE_USER, UserVO.class)).thenReturn(TEST_USER_VO_ROLE_USER);
        doNothing().when(userRepo).updateUserStatus(2L, String.valueOf(UserStatus.CREATED));
        when(modelMapper.map(TEST_USER_VO_ROLE_USER, UserStatusDto.class)).thenReturn(TEST_USER_STATUS_DTO);

        UserStatusDto actual = userService.updateStatus(2L, CREATED, TEST_EMAIL_2);

        assertEquals(TEST_USER_STATUS_DTO, actual);

        verify(userRepo, times(2)).findByEmail(anyString());
        verify(modelMapper, times(4)).map(any(User.class), eq(UserVO.class));
        verify(userRepo, times(2)).findById(anyLong());
        verify(userRepo).updateUserStatus(2L, String.valueOf(CREATED));
        verify(modelMapper).map(TEST_USER_VO_ROLE_USER, UserStatusDto.class);
    }

    @Test
    void testUpdateStatusThrowsBadUpdateRequestException() {
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.ofNullable(TEST_USER));
        when(modelMapper.map(TEST_USER, UserVO.class)).thenReturn(TEST_USER_VO);

        assertThrows(BadUpdateRequestException.class,
            () -> userService.updateStatus(1L, CREATED, TEST_EMAIL));
    }

    @Test
    void testUpdateStatusThrowsLowRoleLevelException() {
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.ofNullable(TEST_USER));
        when(modelMapper.map(TEST_USER, UserVO.class)).thenReturn(TEST_USER_VO);
        when(userRepo.findById(2L)).thenReturn(Optional.ofNullable(TEST_USER));
        when(modelMapper.map(TEST_USER, UserVO.class)).thenReturn(TEST_USER_VO);

        assertThrows(LowRoleLevelException.class,
            () -> userService.updateStatus(2L, CREATED, TEST_EMAIL));
    }

    @Test
    void getAllUsersByCriteriaTest() {
        Pageable pageable = PageRequest.of(0, 10);
        List<User> users = new ArrayList<>();
        User user1 = ModelUtils.getUser();
        UserManagementVO userManagementVO = UserManagementVO.builder()
            .id(1L)
            .userStatus(ACTIVATED)
            .email("Test@gmail.com")
            .role(Role.ROLE_ADMIN).build();

        users.add(user1);
        Page<User> page = new PageImpl<>(users, pageable, 1);

        when(userRepo.findAll(any(UserFilter.class), eq(pageable))).thenReturn(page);
        when(modelMapper.map(user1, UserManagementVO.class)).thenReturn(userManagementVO);

        userService.getAllUsersByCriteria("Test", "ROLE_ADMIN", "ACTIVATED", pageable);

        verify(userRepo, times(1)).findAll(any(UserFilter.class), eq(pageable));
        verify(modelMapper, times(1)).map(user1, UserManagementVO.class);
    }
}