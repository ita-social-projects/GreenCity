package greencity.service;

import greencity.ModelUtils;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.friends.UserFriendDto;
import greencity.dto.user.UserManagementDto;
import greencity.entity.User;
import greencity.enums.RecommendedFriendsType;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UnsupportedSortException;
import greencity.repository.CustomUserRepo;
import greencity.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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
        long userId = 1L;
        long friendId = 2L;

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
        long userId = 1L;
        long friendId = 2L;

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
        long userId = 1L;
        long friendId = 2L;

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
        long userId = 1L;
        long friendId = 2L;

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
        long userId = 1L;
        long friendId = 2L;

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
        long userId = 1L;
        long friendId = 1L;

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
        long userId = 1L;
        long friendId = 2L;

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
        long userId = 1L;
        long friendId = 2L;

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
        long userId = 1L;
        long friendId = 2L;

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
        long userId = 1L;
        long friendId = 2L;

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
        long userId = 1L;
        long friendId = 2L;

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
        long userId = 1L;
        long friendId = 1L;

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
        long userId = 1L;
        long friendId = 2L;

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
        long userId = 1L;
        long friendId = 2L;

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
        long userId = 1L;
        long friendId = 2L;

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
        long userId = 1L;
        long friendId = 2L;

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
        long userId = 1L;
        long friendId = 2L;

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
        long userId = 1L;
        long friendId = 1L;

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
        long userId = 1L;
        long friendId = 2L;

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
        long userId = 1L;
        long friendId = 2L;

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
        long userId = 1L;
        long friendId = 2L;

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
        long userId = 1L;
        long friendId = 2L;

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
        long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> users = new PageImpl<>(List.of(ModelUtils.getUser()), pageable, 1);

        when(userRepo.existsById(userId)).thenReturn(true);
        when(userRepo.getAllUserFriendsPage(pageable, userId)).thenReturn(users);

        friendService.findUserFriendsByUserId(pageable, userId);

        verify(userRepo).existsById(userId);
        verify(userRepo).getAllUserFriendsPage(pageable, userId);
        verify(modelMapper).map(users.getContent().get(0), UserManagementDto.class);
    }

    @Test
    void findUserFriendsByUserIdWhenUserNotFoundTest() {
        long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepo.existsById(userId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> friendService.findUserFriendsByUserId(pageable, userId));

        assertEquals(ErrorMessage.USER_NOT_FOUND_BY_ID + userId, exception.getMessage());

        verify(userRepo).existsById(userId);
        verify(userRepo, never()).getAllUserFriends(anyLong());
        verify(modelMapper, never()).map(anyLong(), any());
    }

    @Test
    void findUserFriendsByUserIdWhenUnsupportedSortExceptionTest() {
        long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        when(userRepo.existsById(userId)).thenReturn(true);

        assertThrows(UnsupportedSortException.class,
            () -> friendService.findUserFriendsByUserId(pageable, userId));

        verify(userRepo).existsById(userId);
    }

    @Test
    void findAllUsersExceptMainUserAndUsersFriendTest() {
        long userId = 1L;
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
    void findAllUsersExceptMainUserAndUsersFriendUnsupportedSortExceptionTest() {
        long userId = 1L;

        PageRequest pageable = PageRequest.of(0, 1, Sort.by("id"));
        String name = "vi";

        when(userRepo.existsById(userId)).thenReturn(true);

        assertThrows(UnsupportedSortException.class, () -> {
            friendService.findAllUsersExceptMainUserAndUsersFriend(1L,
                name, pageable);
        });
    }

    @Test
    void findAllUsersExceptMainUserAndUsersFriendWhenNameIsNullTest() {
        long userId = 1L;
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
    void findRecommendedFriendsByFriendsOfFriends() {
        long userId = 1L;
        int page = 0;
        int size = 1;
        long totalElements = 50;
        Pageable pageable = PageRequest.of(page, size);
        UserFriendDto expectedResult = ModelUtils.getUserFriendDto();
        Page<User> userPage = new PageImpl<>(List.of(ModelUtils.getUser()), pageable, totalElements);

        when(userRepo.existsById(userId)).thenReturn(true);
        when(userRepo.getRecommendedFriendsOfFriends(userId, pageable)).thenReturn(userPage);
        when(
            customUserRepo.fillListOfUserWithCountOfMutualFriendsAndChatIdForCurrentUser(userId, userPage.getContent()))
                .thenReturn(List.of(expectedResult));

        PageableDto<UserFriendDto> pageableDto =
            friendService.findRecommendedFriends(userId, RecommendedFriendsType.FRIENDS_OF_FRIENDS, pageable);

        assertNotNull(pageableDto.getPage());
        assertEquals(1, pageableDto.getPage().size());
        assertEquals(expectedResult, pageableDto.getPage().get(0));
        assertEquals(totalElements, pageableDto.getTotalElements());
        assertEquals(totalElements, pageableDto.getTotalPages());
        assertEquals(page, pageableDto.getCurrentPage());

        verify(userRepo).existsById(userId);
        verify(userRepo).getRecommendedFriendsOfFriends(userId, pageable);
        verify(customUserRepo).fillListOfUserWithCountOfMutualFriendsAndChatIdForCurrentUser(userId,
            userPage.getContent());
    }

    @Test
    void findRecommendedFriendsWithoutType() {
        long userId = 1L;
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
            friendService.findRecommendedFriends(userId, null, pageable);

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
    void findRecommendedFriendsByHabits() {
        long userId = 1L;
        int page = 0;
        int size = 1;
        long totalElements = 50;
        Pageable pageable = PageRequest.of(page, size);
        UserFriendDto expectedResult = ModelUtils.getUserFriendDto();
        Page<User> userPage = new PageImpl<>(List.of(ModelUtils.getUser()), pageable, totalElements);

        when(userRepo.existsById(userId)).thenReturn(true);
        when(userRepo.findRecommendedFriendsByHabits(userId, pageable)).thenReturn(userPage);
        when(
            customUserRepo.fillListOfUserWithCountOfMutualFriendsAndChatIdForCurrentUser(userId, userPage.getContent()))
                .thenReturn(List.of(expectedResult));

        PageableDto<UserFriendDto> pageableDto =
            friendService.findRecommendedFriends(userId, RecommendedFriendsType.HABITS, pageable);

        assertNotNull(pageableDto.getPage());
        assertEquals(1, pageableDto.getPage().size());
        assertEquals(expectedResult, pageableDto.getPage().get(0));
        assertEquals(totalElements, pageableDto.getTotalElements());
        assertEquals(totalElements, pageableDto.getTotalPages());
        assertEquals(page, pageableDto.getCurrentPage());

        verify(userRepo).existsById(userId);
        verify(userRepo).findRecommendedFriendsByHabits(userId, pageable);
        verify(customUserRepo).fillListOfUserWithCountOfMutualFriendsAndChatIdForCurrentUser(userId,
            userPage.getContent());
    }

    @Test
    void getMutualFriendsTest() {
        long userId = 1L;
        long friendId = 2L;
        int page = 0;
        int size = 1;
        long totalElements = 50;
        Pageable pageable = PageRequest.of(page, size);
        UserFriendDto expectedResult = ModelUtils.getUserFriendDto();
        Page<User> userPage = new PageImpl<>(List.of(ModelUtils.getUser()), pageable, totalElements);

        when(userRepo.existsById(userId)).thenReturn(true);
        when(userRepo.existsById(friendId)).thenReturn(true);
        when(userRepo.getMutualFriends(userId, friendId, pageable)).thenReturn(userPage);
        when(
            customUserRepo.fillListOfUserWithCountOfMutualFriendsAndChatIdForCurrentUser(userId, userPage.getContent()))
                .thenReturn(List.of(expectedResult));

        PageableDto<UserFriendDto> pageableDto =
            friendService.getMutualFriends(userId, friendId, pageable);

        assertNotNull(pageableDto.getPage());
        assertEquals(1, pageableDto.getPage().size());
        assertEquals(expectedResult, pageableDto.getPage().get(0));
        assertEquals(totalElements, pageableDto.getTotalElements());
        assertEquals(totalElements, pageableDto.getTotalPages());
        assertEquals(page, pageableDto.getCurrentPage());

        verify(userRepo).existsById(userId);
        verify(userRepo).existsById(friendId);
        verify(userRepo).getMutualFriends(userId, friendId, pageable);
        verify(customUserRepo).fillListOfUserWithCountOfMutualFriendsAndChatIdForCurrentUser(userId,
            userPage.getContent());
    }

    @Test
    void findAllUsersExceptMainUserAndUsersFriendWhenUserNotFoundTest() {
        long userId = 1L;
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
    void findAllUsersExceptMainUserAndUsersFriendWhenPageableIsNullTest() {
        long userId = 1L;
        String name = "vi";

        assertThrows(NullPointerException.class,
            () -> friendService.findAllUsersExceptMainUserAndUsersFriend(userId, name, null));

        verify(userRepo, never()).existsById(anyLong());
        verify(userRepo, never()).getAllUsersExceptMainUserAndFriends(anyLong(), anyString(), any());
        verify(customUserRepo, never()).fillListOfUserWithCountOfMutualFriendsAndChatIdForCurrentUser(anyLong(), any());
    }

    @Test
    void getAllUserFriendRequestsTest() {
        long userId = 1L;
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
        long userId = 1L;
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
    void getAllUserFriendRequestsWhenPageableIsNullTest() {
        long userId = 1L;

        assertThrows(NullPointerException.class,
            () -> friendService.getAllUserFriendRequests(userId, null));

        verify(userRepo, never()).existsById(anyLong());
        verify(userRepo, never()).getAllUserFriendRequests(anyLong(), any());
        verify(customUserRepo, never()).fillListOfUserWithCountOfMutualFriendsAndChatIdForCurrentUser(anyLong(), any());
    }

    @Test
    void findAllFriendsOfUserTest() {
        long userId = 1L;
        int page = 0;
        int size = 1;
        long totalElements = 50;
        Pageable pageable = PageRequest.of(page, size);
        UserFriendDto expectedResult = ModelUtils.getUserFriendDto();
        expectedResult.setFriendStatus("FRIEND");
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
        long userId = 1L;
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
    void findAllFriendsOfUserWhenUnsupportedSortExceptionTest() {
        long userId = 1L;
        PageRequest pageable = PageRequest.of(0, 1, Sort.by("id"));
        String name = "vi";

        when(userRepo.existsById(userId)).thenReturn(true);

        assertThrows(UnsupportedSortException.class, () -> {
            friendService.findAllFriendsOfUser(1L,
                name, pageable);
        });

        verify(userRepo).existsById(1L);
    }

    @Test
    void findAllFriendsOfUserWhenUserNotFoundTest() {
        long userId = 1L;
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

    @Test
    void findAllFriendsOfUserWhenPageableIsNullTest() {
        long userId = 1L;
        String name = "vi";

        assertThrows(NullPointerException.class,
            () -> friendService.findAllFriendsOfUser(userId, name, null));

        verify(userRepo, never()).existsById(anyLong());
        verify(userRepo, never()).findAllFriendsOfUser(anyLong(), anyString(), any());
        verify(customUserRepo, never()).fillListOfUserWithCountOfMutualFriendsAndChatIdForCurrentUser(anyLong(), any());
    }

    @Test
    void deleteRequestOfCurrentUserToFriendTest() {
        long userId = 1L;
        long friendId = 2L;

        when(userRepo.existsById(userId)).thenReturn(true);
        when(userRepo.existsById(friendId)).thenReturn(true);
        when(userRepo.isFriend(userId, friendId)).thenReturn(false);
        when(userRepo.isFriendRequestedByCurrentUser(userId, friendId)).thenReturn(true);

        friendService.deleteRequestOfCurrentUserToFriend(userId, friendId);

        verify(userRepo).existsById(userId);
        verify(userRepo).existsById(friendId);
        verify(userRepo).isFriend(userId, friendId);
        verify(userRepo).isFriendRequestedByCurrentUser(userId, friendId);
        verify(userRepo).canselUserRequestToFriend(userId, friendId);
    }

    @Test
    void deleteRequestOfCurrentUserToFriendWhenUserIdEqualFriendIdTest() {
        long userId = 1L;
        long friendId = 1L;

        BadRequestException exception = assertThrows(BadRequestException.class,
            () -> friendService.deleteRequestOfCurrentUserToFriend(userId, friendId));

        assertEquals(ErrorMessage.OWN_USER_ID + friendId, exception.getMessage());

        verify(userRepo, never()).existsById(anyLong());
        verify(userRepo, never()).isFriend(anyLong(), anyLong());
        verify(userRepo, never()).isFriendRequestedByCurrentUser(anyLong(), anyLong());
        verify(userRepo, never()).deleteUserFriendById(anyLong(), anyLong());
    }

    @Test
    void deleteRequestOfCurrentUserToFriendWhenUserNotFoundTest() {
        long userId = 1L;
        long friendId = 2L;

        when(userRepo.existsById(userId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> friendService.deleteRequestOfCurrentUserToFriend(userId, friendId));

        assertEquals(ErrorMessage.USER_NOT_FOUND_BY_ID + userId, exception.getMessage());

        verify(userRepo).existsById(userId);
        verify(userRepo, never()).existsById(friendId);
        verify(userRepo, never()).isFriend(anyLong(), anyLong());
        verify(userRepo, never()).isFriendRequestedByCurrentUser(anyLong(), anyLong());
        verify(userRepo, never()).canselUserRequestToFriend(anyLong(), anyLong());
    }

    @Test
    void deleteRequestOfCurrentUserToFriendWhenFriendNotFoundTest() {
        long userId = 1L;
        long friendId = 2L;

        when(userRepo.existsById(userId)).thenReturn(true);
        when(userRepo.existsById(friendId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> friendService.deleteRequestOfCurrentUserToFriend(userId, friendId));

        assertEquals(ErrorMessage.USER_NOT_FOUND_BY_ID + friendId, exception.getMessage());

        verify(userRepo).existsById(userId);
        verify(userRepo).existsById(friendId);
        verify(userRepo, never()).isFriend(anyLong(), anyLong());
        verify(userRepo, never()).isFriendRequestedByCurrentUser(anyLong(), anyLong());
        verify(userRepo, never()).canselUserRequestToFriend(anyLong(), anyLong());
    }

    @Test
    void deleteRequestOfCurrentUserToFriendWhenUsersIsAlreadyFriendsTest() {
        long userId = 1L;
        long friendId = 2L;

        when(userRepo.existsById(userId)).thenReturn(true);
        when(userRepo.existsById(friendId)).thenReturn(true);
        when(userRepo.isFriend(userId, friendId)).thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class,
            () -> friendService.deleteRequestOfCurrentUserToFriend(userId, friendId));

        assertEquals(ErrorMessage.FRIEND_EXISTS + friendId, exception.getMessage());

        verify(userRepo).existsById(userId);
        verify(userRepo).existsById(friendId);
        verify(userRepo).isFriend(userId, friendId);
        verify(userRepo, never()).isFriendRequestedByCurrentUser(anyLong(), anyLong());
        verify(userRepo, never()).canselUserRequestToFriend(anyLong(), anyLong());
    }

    @Test
    void deleteRequestOfCurrentUserToFriendWhenRequestIsNotMadeTest() {
        long userId = 1L;
        long friendId = 2L;

        when(userRepo.existsById(userId)).thenReturn(true);
        when(userRepo.existsById(friendId)).thenReturn(true);
        when(userRepo.isFriend(userId, friendId)).thenReturn(false);
        when(userRepo.isFriendRequestedByCurrentUser(userId, friendId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> friendService.deleteRequestOfCurrentUserToFriend(userId, friendId));

        assertEquals(ErrorMessage.FRIEND_REQUEST_NOT_SENT, exception.getMessage());

        verify(userRepo).existsById(userId);
        verify(userRepo).existsById(friendId);
        verify(userRepo).isFriend(userId, friendId);
        verify(userRepo).isFriendRequestedByCurrentUser(userId, friendId);
        verify(userRepo, never()).canselUserRequestToFriend(anyLong(), anyLong());
    }
}
