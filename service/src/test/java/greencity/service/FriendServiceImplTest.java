package greencity.service;

import greencity.ModelUtils;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.friends.UserFriendDto;
import greencity.dto.user.UserManagementDto;
import greencity.entity.User;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.CustomUserRepo;
import greencity.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FriendServiceImplTest {
    @InjectMocks
    private FriendServiceImpl friendService;

    @Mock
    private UserRepo userRepo;
    @Mock
    private CustomUserRepo customUserRepo;
    @Mock
    private ModelMapper modelMapper;

    @Test
    void deleteUserFriendByIdTest() {
        Long userId = 1L;
        Long friendId = 2L;

        when(userRepo.existsById(userId)).thenReturn(true);
        when(userRepo.existsById(friendId)).thenReturn(true);
        when(userRepo.isFriend(userId, friendId)).thenReturn(true);

        friendService.deleteUserFriendById(userId, friendId);

        verify(userRepo).existsById(userId);
        verify(userRepo).existsById(friendId);
        verify(userRepo).isFriend(userId, friendId);
        verify(userRepo).deleteUserFriendById(userId, friendId);
    }

    @Test
    void deleteUserFriendByIdWhenUserNotFoundTest() {
        Long userId = 1L;
        Long friendId = 2L;

        when(userRepo.existsById(userId)).thenReturn(false);

        Exception exception = assertThrows(NotFoundException.class,
            () -> friendService.deleteUserFriendById(userId, friendId));

        assertEquals(ErrorMessage.USER_NOT_FOUND_BY_ID + userId, exception.getMessage());

        verify(userRepo).existsById(userId);
        verify(userRepo, never()).existsById(friendId);
        verify(userRepo, never()).isFriend(anyLong(), anyLong());
        verify(userRepo, never()).deleteUserFriendById(anyLong(), anyLong());
    }

    @Test
    void deleteUserFriendByIdWhenFriendNotFoundTest() {
        Long userId = 1L;
        Long friendId = 2L;

        when(userRepo.existsById(userId)).thenReturn(true);
        when(userRepo.existsById(friendId)).thenReturn(false);

        Exception exception = assertThrows(NotFoundException.class,
            () -> friendService.deleteUserFriendById(userId, friendId));

        assertEquals(ErrorMessage.USER_NOT_FOUND_BY_ID + friendId, exception.getMessage());

        verify(userRepo).existsById(userId);
        verify(userRepo).existsById(friendId);
        verify(userRepo, never()).isFriend(anyLong(), anyLong());
        verify(userRepo, never()).deleteUserFriendById(anyLong(), anyLong());
    }

    @Test
    void deleteUserFriendByIdWhenUserIsNotFriendTest() {
        Long userId = 1L;
        Long friendId = 2L;

        when(userRepo.existsById(userId)).thenReturn(true);
        when(userRepo.existsById(friendId)).thenReturn(true);
        when(userRepo.isFriend(userId, friendId)).thenReturn(false);

        Exception exception = assertThrows(NotDeletedException.class,
            () -> friendService.deleteUserFriendById(userId, friendId));

        assertEquals(ErrorMessage.USER_FRIENDS_LIST + friendId, exception.getMessage());

        verify(userRepo).existsById(userId);
        verify(userRepo).existsById(friendId);
        verify(userRepo).isFriend(userId, friendId);
        verify(userRepo, never()).deleteUserFriendById(anyLong(), anyLong());
    }

    @Test
    void addNewFriendTest() {
        Long userId = 1L;
        Long friendId = 2L;

        when(userRepo.existsById(userId)).thenReturn(true);
        when(userRepo.existsById(friendId)).thenReturn(true);
        when(userRepo.isFriendRequested(userId, friendId)).thenReturn(false);
        when(userRepo.isFriend(userId, friendId)).thenReturn(false);

        friendService.addNewFriend(userId, friendId);

        verify(userRepo).existsById(userId);
        verify(userRepo).existsById(friendId);
        verify(userRepo).isFriendRequested(userId, friendId);
        verify(userRepo).isFriend(userId, friendId);
        verify(userRepo).addNewFriend(userId, friendId);
    }

    @Test
    void addNewFriendWhenUserIdEqualFriendIdTest() {
        Long userId = 1L;
        Long friendId = 1L;

        BadRequestException exception = assertThrows(BadRequestException.class,
            () -> friendService.addNewFriend(userId, friendId));

        assertEquals(ErrorMessage.OWN_USER_ID + friendId, exception.getMessage());

        verify(userRepo, never()).existsById(anyLong());
        verify(userRepo, never()).isFriendRequested(anyLong(), anyLong());
        verify(userRepo, never()).isFriend(anyLong(), anyLong());
        verify(userRepo, never()).addNewFriend(anyLong(), anyLong());
    }

    @Test
    void addNewFriendWhenUserNotFoundTest() {
        Long userId = 1L;
        Long friendId = 2L;

        when(userRepo.existsById(userId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> friendService.addNewFriend(userId, friendId));

        assertEquals(ErrorMessage.USER_NOT_FOUND_BY_ID + userId, exception.getMessage());

        verify(userRepo).existsById(userId);
        verify(userRepo, never()).existsById(friendId);
        verify(userRepo, never()).isFriendRequested(anyLong(), anyLong());
        verify(userRepo, never()).isFriend(anyLong(), anyLong());
        verify(userRepo, never()).addNewFriend(anyLong(), anyLong());
    }

    @Test
    void addNewFriendWhenFriendNotFoundTest() {
        Long userId = 1L;
        Long friendId = 2L;

        when(userRepo.existsById(userId)).thenReturn(true);
        when(userRepo.existsById(friendId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> friendService.addNewFriend(userId, friendId));

        assertEquals(ErrorMessage.USER_NOT_FOUND_BY_ID + friendId, exception.getMessage());

        verify(userRepo).existsById(userId);
        verify(userRepo).existsById(friendId);
        verify(userRepo, never()).isFriendRequested(anyLong(), anyLong());
        verify(userRepo, never()).isFriend(anyLong(), anyLong());
        verify(userRepo, never()).addNewFriend(anyLong(), anyLong());
    }

    @Test
    void addNewFriendWhenRequestIsAlreadyMadeTest() {
        Long userId = 1L;
        Long friendId = 2L;

        when(userRepo.existsById(userId)).thenReturn(true);
        when(userRepo.existsById(friendId)).thenReturn(true);
        when(userRepo.isFriendRequested(userId, friendId)).thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class,
            () -> friendService.addNewFriend(userId, friendId));

        assertEquals(ErrorMessage.FRIEND_REQUEST_ALREADY_SENT, exception.getMessage());

        verify(userRepo).existsById(userId);
        verify(userRepo).existsById(friendId);
        verify(userRepo).isFriendRequested(userId, friendId);
        verify(userRepo, never()).isFriend(anyLong(), anyLong());
        verify(userRepo, never()).addNewFriend(anyLong(), anyLong());
    }

    @Test
    void addNewFriendWhenUsersIsAlreadyFriendsTest() {
        Long userId = 1L;
        Long friendId = 2L;

        when(userRepo.existsById(userId)).thenReturn(true);
        when(userRepo.existsById(friendId)).thenReturn(true);
        when(userRepo.isFriendRequested(userId, friendId)).thenReturn(false);
        when(userRepo.isFriend(userId, friendId)).thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class,
            () -> friendService.addNewFriend(userId, friendId));

        assertEquals(ErrorMessage.FRIEND_EXISTS + friendId, exception.getMessage());

        verify(userRepo).existsById(userId);
        verify(userRepo).existsById(friendId);
        verify(userRepo).isFriendRequested(userId, friendId);
        verify(userRepo).isFriend(userId, friendId);
        verify(userRepo, never()).addNewFriend(anyLong(), anyLong());
    }

    @Test
    void acceptFriendRequestTest() {
        Long userId = 1L;
        Long friendId = 2L;

        when(userRepo.existsById(userId)).thenReturn(true);
        when(userRepo.existsById(friendId)).thenReturn(true);
        when(userRepo.isFriend(userId, friendId)).thenReturn(false);
        when(userRepo.isFriendRequestedByCurrentUser(friendId, userId)).thenReturn(true);

        friendService.acceptFriendRequest(userId, friendId);

        verify(userRepo).existsById(userId);
        verify(userRepo).existsById(friendId);
        verify(userRepo).isFriend(userId, friendId);
        verify(userRepo).isFriendRequestedByCurrentUser(friendId, userId);
        verify(userRepo).acceptFriendRequest(userId, friendId);
    }

    @Test
    void acceptFriendRequestWhenUserIdEqualFriendIdTest() {
        Long userId = 1L;
        Long friendId = 1L;

        BadRequestException exception = assertThrows(BadRequestException.class,
            () -> friendService.acceptFriendRequest(userId, friendId));

        assertEquals(ErrorMessage.OWN_USER_ID + friendId, exception.getMessage());

        verify(userRepo, never()).existsById(anyLong());
        verify(userRepo, never()).isFriend(anyLong(), anyLong());
        verify(userRepo, never()).isFriendRequestedByCurrentUser(anyLong(), anyLong());
        verify(userRepo, never()).acceptFriendRequest(anyLong(), anyLong());
    }

    @Test
    void acceptFriendRequestWhenUserNotFoundTest() {
        Long userId = 1L;
        Long friendId = 2L;

        when(userRepo.existsById(userId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> friendService.acceptFriendRequest(userId, friendId));

        assertEquals(ErrorMessage.USER_NOT_FOUND_BY_ID + userId, exception.getMessage());

        verify(userRepo).existsById(userId);
        verify(userRepo, never()).existsById(friendId);
        verify(userRepo, never()).isFriend(anyLong(), anyLong());
        verify(userRepo, never()).isFriendRequestedByCurrentUser(anyLong(), anyLong());
        verify(userRepo, never()).acceptFriendRequest(anyLong(), anyLong());
    }

    @Test
    void acceptFriendRequestWhenFriendNotFoundTest() {
        Long userId = 1L;
        Long friendId = 2L;

        when(userRepo.existsById(userId)).thenReturn(true);
        when(userRepo.existsById(friendId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> friendService.acceptFriendRequest(userId, friendId));

        assertEquals(ErrorMessage.USER_NOT_FOUND_BY_ID + friendId, exception.getMessage());

        verify(userRepo).existsById(userId);
        verify(userRepo).existsById(friendId);
        verify(userRepo, never()).isFriend(anyLong(), anyLong());
        verify(userRepo, never()).isFriendRequestedByCurrentUser(anyLong(), anyLong());
        verify(userRepo, never()).acceptFriendRequest(anyLong(), anyLong());
    }

    @Test
    void acceptFriendRequestWhenUsersIsAlreadyFriendsTest() {
        Long userId = 1L;
        Long friendId = 2L;

        when(userRepo.existsById(userId)).thenReturn(true);
        when(userRepo.existsById(friendId)).thenReturn(true);
        when(userRepo.isFriend(userId, friendId)).thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class,
            () -> friendService.acceptFriendRequest(userId, friendId));

        assertEquals(ErrorMessage.FRIEND_EXISTS + friendId, exception.getMessage());

        verify(userRepo).existsById(userId);
        verify(userRepo).existsById(friendId);
        verify(userRepo).isFriend(userId, friendId);
        verify(userRepo, never()).isFriendRequestedByCurrentUser(anyLong(), anyLong());
        verify(userRepo, never()).acceptFriendRequest(anyLong(), anyLong());
    }

    @Test
    void acceptFriendRequestWhenRequestIsAlreadyMadeTest() {
        Long userId = 1L;
        Long friendId = 2L;

        when(userRepo.existsById(userId)).thenReturn(true);
        when(userRepo.existsById(friendId)).thenReturn(true);
        when(userRepo.isFriend(userId, friendId)).thenReturn(false);
        when(userRepo.isFriendRequestedByCurrentUser(friendId, userId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> friendService.acceptFriendRequest(userId, friendId));

        assertEquals(ErrorMessage.FRIEND_REQUEST_NOT_SENT, exception.getMessage());

        verify(userRepo).existsById(userId);
        verify(userRepo).existsById(friendId);
        verify(userRepo).isFriend(userId, friendId);
        verify(userRepo).isFriendRequestedByCurrentUser(friendId, userId);
        verify(userRepo, never()).acceptFriendRequest(anyLong(), anyLong());
    }

    @Test
    void declineFriendRequestTest() {
        Long userId = 1L;
        Long friendId = 2L;

        when(userRepo.existsById(userId)).thenReturn(true);
        when(userRepo.existsById(friendId)).thenReturn(true);
        when(userRepo.isFriend(userId, friendId)).thenReturn(false);
        when(userRepo.isFriendRequestedByCurrentUser(friendId, userId)).thenReturn(true);

        friendService.declineFriendRequest(userId, friendId);

        verify(userRepo).existsById(userId);
        verify(userRepo).existsById(friendId);
        verify(userRepo).isFriend(userId, friendId);
        verify(userRepo).isFriendRequestedByCurrentUser(friendId, userId);
        verify(userRepo).declineFriendRequest(userId, friendId);
    }

    @Test
    void declineFriendRequestWhenUserIdEqualFriendIdTest() {
        Long userId = 1L;
        Long friendId = 1L;

        BadRequestException exception = assertThrows(BadRequestException.class,
            () -> friendService.declineFriendRequest(userId, friendId));

        assertEquals(ErrorMessage.OWN_USER_ID + friendId, exception.getMessage());

        verify(userRepo, never()).existsById(anyLong());
        verify(userRepo, never()).isFriend(anyLong(), anyLong());
        verify(userRepo, never()).isFriendRequestedByCurrentUser(anyLong(), anyLong());
        verify(userRepo, never()).declineFriendRequest(anyLong(), anyLong());
    }

    @Test
    void declineFriendRequestWhenUserNotFoundTest() {
        Long userId = 1L;
        Long friendId = 2L;

        when(userRepo.existsById(userId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> friendService.declineFriendRequest(userId, friendId));

        assertEquals(ErrorMessage.USER_NOT_FOUND_BY_ID + userId, exception.getMessage());

        verify(userRepo).existsById(userId);
        verify(userRepo, never()).existsById(friendId);
        verify(userRepo, never()).isFriend(anyLong(), anyLong());
        verify(userRepo, never()).isFriendRequestedByCurrentUser(anyLong(), anyLong());
        verify(userRepo, never()).declineFriendRequest(anyLong(), anyLong());
    }

    @Test
    void declineFriendRequestWhenFriendNotFoundTest() {
        Long userId = 1L;
        Long friendId = 2L;

        when(userRepo.existsById(userId)).thenReturn(true);
        when(userRepo.existsById(friendId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> friendService.declineFriendRequest(userId, friendId));

        assertEquals(ErrorMessage.USER_NOT_FOUND_BY_ID + friendId, exception.getMessage());

        verify(userRepo).existsById(userId);
        verify(userRepo).existsById(friendId);
        verify(userRepo, never()).isFriend(anyLong(), anyLong());
        verify(userRepo, never()).isFriendRequestedByCurrentUser(anyLong(), anyLong());
        verify(userRepo, never()).declineFriendRequest(anyLong(), anyLong());
    }

    @Test
    void declineFriendRequestWhenUsersIsAlreadyFriendsTest() {
        Long userId = 1L;
        Long friendId = 2L;

        when(userRepo.existsById(userId)).thenReturn(true);
        when(userRepo.existsById(friendId)).thenReturn(true);
        when(userRepo.isFriend(userId, friendId)).thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class,
            () -> friendService.declineFriendRequest(userId, friendId));

        assertEquals(ErrorMessage.FRIEND_EXISTS + friendId, exception.getMessage());

        verify(userRepo).existsById(userId);
        verify(userRepo).existsById(friendId);
        verify(userRepo).isFriend(userId, friendId);
        verify(userRepo, never()).isFriendRequestedByCurrentUser(anyLong(), anyLong());
        verify(userRepo, never()).declineFriendRequest(anyLong(), anyLong());
    }

    @Test
    void declineFriendRequestWhenRequestIsAlreadyMadeTest() {
        Long userId = 1L;
        Long friendId = 2L;

        when(userRepo.existsById(userId)).thenReturn(true);
        when(userRepo.existsById(friendId)).thenReturn(true);
        when(userRepo.isFriend(userId, friendId)).thenReturn(false);
        when(userRepo.isFriendRequestedByCurrentUser(friendId, userId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> friendService.declineFriendRequest(userId, friendId));

        assertEquals(ErrorMessage.FRIEND_REQUEST_NOT_SENT, exception.getMessage());

        verify(userRepo).existsById(userId);
        verify(userRepo).existsById(friendId);
        verify(userRepo).isFriend(userId, friendId);
        verify(userRepo).isFriendRequestedByCurrentUser(friendId, userId);
        verify(userRepo, never()).declineFriendRequest(anyLong(), anyLong());
    }

    @Test
    void findUserFriendsByUserIdTest() {
        Long userId = 1L;
        List<User> users = List.of(ModelUtils.getUser());

        when(userRepo.existsById(userId)).thenReturn(true);
        when(userRepo.getAllUserFriends(userId)).thenReturn(users);

        friendService.findUserFriendsByUserId(userId);

        verify(userRepo).existsById(userId);
        verify(userRepo).getAllUserFriends(userId);
        verify(modelMapper).map(users.get(0), UserManagementDto.class);
    }

    @Test
    void findUserFriendsByUserIdWhenUserNotFoundTest() {
        Long userId = 1L;

        when(userRepo.existsById(userId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> friendService.findUserFriendsByUserId(userId));

        assertEquals(ErrorMessage.USER_NOT_FOUND_BY_ID + userId, exception.getMessage());

        verify(userRepo).existsById(userId);
        verify(userRepo, never()).getAllUserFriends(anyLong());
        verify(modelMapper, never()).map(anyLong(), any());
    }

    @Test
    void findAllUsersExceptMainUserAndUsersFriendTest() {
        Long userId = 1L;
        int page = 0;
        int size = 1;
        long totalElements = 50;
        Pageable pageable = PageRequest.of(page, size);
        UserFriendDto expectedResult = ModelUtils.getUserFriendDto();
        Page<User> userPage = new PageImpl<>(List.of(ModelUtils.getUser()), pageable, totalElements);
        String name = "vi";

        when(userRepo.existsById(userId)).thenReturn(true);
        when(userRepo.getAllUsersExceptMainUserAndFriends(userId, name, pageable)).thenReturn(userPage);
        when(
            customUserRepo.fillListOfUserWithCountOfMutualFriendsAndChatIdForCurrentUser(userId, userPage.getContent()))
                .thenReturn(List.of(expectedResult));

        PageableDto<UserFriendDto> pageableDto =
            friendService.findAllUsersExceptMainUserAndUsersFriend(userId, name, pageable);

        assertNotNull(pageableDto);
        assertNotNull(pageableDto.getPage());
        assertEquals(1, pageableDto.getPage().size());
        assertEquals(expectedResult, pageableDto.getPage().get(0));
        assertEquals(totalElements, pageableDto.getTotalElements());
        assertEquals(totalElements, pageableDto.getTotalPages());
        assertEquals(page, pageableDto.getCurrentPage());

        verify(userRepo).existsById(userId);
        verify(userRepo).getAllUsersExceptMainUserAndFriends(userId, name, pageable);
        verify(customUserRepo).fillListOfUserWithCountOfMutualFriendsAndChatIdForCurrentUser(userId,
            userPage.getContent());
    }

    @Test
    void findAllUsersExceptMainUserAndUsersFriendWhenNameIsNullTest() {
        Long userId = 1L;
        int page = 0;
        int size = 1;
        long totalElements = 50;
        Pageable pageable = PageRequest.of(page, size);
        UserFriendDto expectedResult = ModelUtils.getUserFriendDto();
        Page<User> userPage = new PageImpl<>(List.of(ModelUtils.getUser()), pageable, totalElements);

        when(userRepo.existsById(userId)).thenReturn(true);
        when(userRepo.getAllUsersExceptMainUserAndFriends(userId, "", pageable)).thenReturn(userPage);
        when(
            customUserRepo.fillListOfUserWithCountOfMutualFriendsAndChatIdForCurrentUser(userId, userPage.getContent()))
                .thenReturn(List.of(expectedResult));

        PageableDto<UserFriendDto> pageableDto =
            friendService.findAllUsersExceptMainUserAndUsersFriend(userId, null, pageable);

        assertNotNull(pageableDto);
        assertNotNull(pageableDto.getPage());
        assertEquals(1, pageableDto.getPage().size());
        assertEquals(expectedResult, pageableDto.getPage().get(0));
        assertEquals(totalElements, pageableDto.getTotalElements());
        assertEquals(totalElements, pageableDto.getTotalPages());
        assertEquals(page, pageableDto.getCurrentPage());

        verify(userRepo).existsById(userId);
        verify(userRepo).getAllUsersExceptMainUserAndFriends(userId, "", pageable);
        verify(customUserRepo).fillListOfUserWithCountOfMutualFriendsAndChatIdForCurrentUser(userId,
            userPage.getContent());
    }

    @Test
    void findAllUsersExceptMainUserAndUsersFriendWhenUserNotFoundTest() {
        Long userId = 1L;
        String name = "vi";
        Pageable pageable = PageRequest.of(0, 20);

        when(userRepo.existsById(userId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> friendService.findAllUsersExceptMainUserAndUsersFriend(userId, name, pageable));

        assertEquals(ErrorMessage.USER_NOT_FOUND_BY_ID + userId, exception.getMessage());

        verify(userRepo).existsById(userId);
        verify(userRepo, never()).getAllUsersExceptMainUserAndFriends(anyLong(), anyString(), any());
        verify(customUserRepo, never()).fillListOfUserWithCountOfMutualFriendsAndChatIdForCurrentUser(anyLong(), any());
    }

    @Test
    void getAllUserFriendRequestsTest() {
        Long userId = 1L;
        int page = 0;
        int size = 1;
        long totalElements = 50;
        Pageable pageable = PageRequest.of(page, size);
        UserFriendDto expectedResult = ModelUtils.getUserFriendDto();
        Page<User> userPage = new PageImpl<>(List.of(ModelUtils.getUser()), pageable, totalElements);

        when(userRepo.existsById(userId)).thenReturn(true);
        when(userRepo.getAllUserFriendRequests(userId, pageable)).thenReturn(userPage);
        when(
            customUserRepo.fillListOfUserWithCountOfMutualFriendsAndChatIdForCurrentUser(userId, userPage.getContent()))
                .thenReturn(List.of(expectedResult));

        PageableDto<UserFriendDto> pageableDto =
            friendService.getAllUserFriendRequests(userId, pageable);

        assertNotNull(pageableDto);
        assertNotNull(pageableDto.getPage());
        assertEquals(1, pageableDto.getPage().size());
        assertEquals(expectedResult, pageableDto.getPage().get(0));
        assertEquals(totalElements, pageableDto.getTotalElements());
        assertEquals(totalElements, pageableDto.getTotalPages());
        assertEquals(page, pageableDto.getCurrentPage());

        verify(userRepo).existsById(userId);
        verify(userRepo).getAllUserFriendRequests(userId, pageable);
        verify(customUserRepo).fillListOfUserWithCountOfMutualFriendsAndChatIdForCurrentUser(userId,
            userPage.getContent());
    }

    @Test
    void getAllUserFriendRequestsWhenUserNotFoundTest() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 20);

        when(userRepo.existsById(userId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> friendService.getAllUserFriendRequests(userId, pageable));

        assertEquals(ErrorMessage.USER_NOT_FOUND_BY_ID + userId, exception.getMessage());

        verify(userRepo).existsById(1L);
        verify(userRepo, never()).getAllUserFriendRequests(anyLong(), any());
        verify(customUserRepo, never()).fillListOfUserWithCountOfMutualFriendsAndChatIdForCurrentUser(anyLong(), any());
    }

    @Test
    void findAllFriendsOfUserTest() {
        Long userId = 1L;
        int page = 0;
        int size = 1;
        long totalElements = 50;
        Pageable pageable = PageRequest.of(page, size);
        UserFriendDto expectedResult = ModelUtils.getUserFriendDto();
        Page<User> userPage = new PageImpl<>(List.of(ModelUtils.getUser()), pageable, totalElements);
        String name = "vi";

        when(userRepo.existsById(userId)).thenReturn(true);
        when(userRepo.findAllFriendsOfUser(userId, name, pageable)).thenReturn(userPage);
        when(
            customUserRepo.fillListOfUserWithCountOfMutualFriendsAndChatIdForCurrentUser(userId, userPage.getContent()))
                .thenReturn(List.of(expectedResult));

        PageableDto<UserFriendDto> pageableDto =
            friendService.findAllFriendsOfUser(userId, name, pageable);

        assertNotNull(pageableDto);
        assertNotNull(pageableDto.getPage());
        assertEquals(1, pageableDto.getPage().size());
        assertEquals(expectedResult, pageableDto.getPage().get(0));
        assertEquals(totalElements, pageableDto.getTotalElements());
        assertEquals(totalElements, pageableDto.getTotalPages());
        assertEquals(page, pageableDto.getCurrentPage());

        verify(userRepo).existsById(userId);
        verify(userRepo).findAllFriendsOfUser(userId, name, pageable);
        verify(customUserRepo).fillListOfUserWithCountOfMutualFriendsAndChatIdForCurrentUser(userId,
            userPage.getContent());
    }

    @Test
    void findAllFriendsOfUserWhenNameIsNullTest() {
        Long userId = 1L;
        int page = 0;
        int size = 1;
        long totalElements = 50;
        Pageable pageable = PageRequest.of(page, size);
        UserFriendDto expectedResult = ModelUtils.getUserFriendDto();
        Page<User> userPage = new PageImpl<>(List.of(ModelUtils.getUser()), pageable, totalElements);

        when(userRepo.existsById(userId)).thenReturn(true);
        when(userRepo.findAllFriendsOfUser(userId, "", pageable)).thenReturn(userPage);
        when(
            customUserRepo.fillListOfUserWithCountOfMutualFriendsAndChatIdForCurrentUser(userId, userPage.getContent()))
                .thenReturn(List.of(expectedResult));

        PageableDto<UserFriendDto> pageableDto =
            friendService.findAllFriendsOfUser(userId, null, pageable);

        assertNotNull(pageableDto);
        assertNotNull(pageableDto.getPage());
        assertEquals(1, pageableDto.getPage().size());
        assertEquals(expectedResult, pageableDto.getPage().get(0));
        assertEquals(totalElements, pageableDto.getTotalElements());
        assertEquals(totalElements, pageableDto.getTotalPages());
        assertEquals(page, pageableDto.getCurrentPage());

        verify(userRepo).existsById(userId);
        verify(userRepo).findAllFriendsOfUser(userId, "", pageable);
        verify(customUserRepo).fillListOfUserWithCountOfMutualFriendsAndChatIdForCurrentUser(userId,
            userPage.getContent());
    }

    @Test
    void findAllFriendsOfUserWhenUserNotFoundTest() {
        Long userId = 1L;
        String name = "vi";
        Pageable pageable = PageRequest.of(0, 20);

        when(userRepo.existsById(userId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> friendService.findAllFriendsOfUser(userId, name, pageable));

        assertEquals(ErrorMessage.USER_NOT_FOUND_BY_ID + userId, exception.getMessage());

        verify(userRepo).existsById(userId);
        verify(userRepo, never()).findAllFriendsOfUser(anyLong(), anyString(), any());
        verify(customUserRepo, never()).fillListOfUserWithCountOfMutualFriendsAndChatIdForCurrentUser(anyLong(), any());
    }
}
