package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class FriendServiceImplTest {
    @InjectMocks
    private FriendServiceImpl friendService;

    @Mock
    private UserRepo userRepo;

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
}
