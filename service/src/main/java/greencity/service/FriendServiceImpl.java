package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link FriendService}.
 */
@Service
@AllArgsConstructor
public class FriendServiceImpl implements FriendService {
    private final UserRepo userRepo;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteUserFriendById(Long userId, Long friendId) {
        if (!userRepo.existsById(userId)) {
            throw new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId);
        }
        if (!userRepo.existsById(friendId)) {
            throw new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + friendId);
        }
        if (!userRepo.isFriend(userId, friendId)) {
            throw new NotDeletedException(ErrorMessage.USER_FRIENDS_LIST + friendId);
        }
        userRepo.deleteUserFriendById(userId, friendId);
    }
}
