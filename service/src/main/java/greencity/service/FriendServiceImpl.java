package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.friends.UserFriendDto;
import greencity.dto.user.UserManagementDto;
import greencity.entity.User;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link FriendService}.
 */
@Service
@AllArgsConstructor
public class FriendServiceImpl implements FriendService {
    private final UserRepo userRepo;
    private final ModelMapper modelMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteUserFriendById(Long userId, Long friendId) {
        validateUserAndFriendNotSamePerson(userId, friendId);
        validateUserAndFriendExistence(userId, friendId);
        validateFriends(userId, friendId);
        userRepo.deleteUserFriendById(userId, friendId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void addNewFriend(Long userId, Long friendId) {
        validateUserAndFriendNotSamePerson(userId, friendId);
        validateUserAndFriendExistence(userId, friendId);
        validateFriendRequestNotSent(userId, friendId);
        validateFriendNotExists(userId, friendId);
        userRepo.addNewFriend(userId, friendId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void acceptFriendRequest(Long userId, Long friendId) {
        validateUserAndFriendNotSamePerson(userId, friendId);
        validateUserAndFriendExistence(userId, friendId);
        validateFriendNotExists(userId, friendId);
        validateFriendRequestSentByFriend(userId, friendId);
        userRepo.acceptFriendRequest(userId, friendId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void declineFriendRequest(Long userId, Long friendId) {
        validateUserAndFriendNotSamePerson(userId, friendId);
        validateUserAndFriendExistence(userId, friendId);
        validateFriendNotExists(userId, friendId);
        validateFriendRequestSentByFriend(userId, friendId);
        userRepo.declineFriendRequest(userId, friendId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserManagementDto> findUserFriendsByUserId(Long userId) {
        validateUserExistence(userId);
        List<User> friends = userRepo.getAllUserFriends(userId);
        return friends.stream().map(friend -> modelMapper.map(friend, UserManagementDto.class))
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<UserFriendDto> findAllUsersExceptMainUserAndUsersFriend(Pageable pageable, Long userId,
        String name) {
        validateUserExistence(userId);
        List<User> friends = userRepo.getAllUserFriends(userId);
        name = name == null ? "" : name;
        Slice<UserFriendDto> users =
            userRepo.getAllUsersExceptMainUserAndFriends(pageable, userId, friends, name);
        Long totalElements = userRepo.getCountOfNotUserFriends(userId, name);
        return new PageableDto<>(
            users.getContent(),
            totalElements,
            users.getPageable().getPageNumber(),
            (int) Math.ceil(totalElements * 1.0 / pageable.getPageSize()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public PageableDto<UserFriendDto> getAllUserFriendRequests(Long userId, Pageable pageable) {
        validateUserExistence(userId);
        List<User> friends = userRepo.getAllUserFriends(userId);
        Slice<UserFriendDto> usersThatSentRequest = userRepo.getAllUserFriendRequests(userId, pageable, friends);
        Long totalElements = userRepo.getCountOfIncomingRequests(userId);
        return new PageableDto<>(
            usersThatSentRequest.getContent(),
            totalElements,
            usersThatSentRequest.getPageable().getPageNumber(),
            (int) Math.ceil(totalElements * 1.0 / pageable.getPageSize()));
    }

    private void validateUserAndFriendExistence(Long userId, Long friendId) {
        validateUserExistence(userId);
        validateUserExistence(friendId);
    }

    private void validateUserExistence(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId);
        }
    }

    private void validateFriendRequestNotSent(Long userId, Long friendId) {
        if (userRepo.isFriendRequested(userId, friendId)) {
            throw new BadRequestException(ErrorMessage.FRIEND_REQUEST_ALREADY_SENT);
        }
    }

    private void validateFriendNotExists(Long userId, Long friendId) {
        if (userRepo.isFriend(userId, friendId)) {
            throw new BadRequestException(ErrorMessage.FRIEND_EXISTS + friendId);
        }
    }

    private void validateFriendRequestSentByFriend(Long userId, Long friendId) {
        if (!userRepo.isFriendRequestedByCurrentUser(friendId, userId)) {
            throw new NotFoundException(ErrorMessage.FRIEND_REQUEST_NOT_SENT);
        }
    }

    private void validateFriends(Long userId, Long friendId) {
        if (!userRepo.isFriend(userId, friendId)) {
            throw new NotDeletedException(ErrorMessage.USER_FRIENDS_LIST + friendId);
        }
    }

    private void validateUserAndFriendNotSamePerson(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new BadRequestException(ErrorMessage.OWN_USER_ID + friendId);
        }
    }
}
