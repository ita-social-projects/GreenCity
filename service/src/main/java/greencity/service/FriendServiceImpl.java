package greencity.service;

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
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementation of {@link FriendService}.
 */
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class FriendServiceImpl implements FriendService {
    private final UserRepo userRepo;
    private final CustomUserRepo customUserRepo;
    private final ModelMapper modelMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteUserFriendById(long userId, long friendId) {
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
    public void addNewFriend(long userId, long friendId) {
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
    public void acceptFriendRequest(long userId, long friendId) {
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
    public void declineFriendRequest(long userId, long friendId) {
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
    public List<UserManagementDto> findUserFriendsByUserId(long userId) {
        validateUserExistence(userId);
        List<User> friends = userRepo.getAllUserFriends(userId);
        return friends.stream().map(friend -> modelMapper.map(friend, UserManagementDto.class))
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<UserFriendDto> findAllUsersExceptMainUserAndUsersFriend(long userId,
        @Nullable String name, Pageable pageable) {
        Objects.requireNonNull(pageable);

        validateUserExistence(userId);
        name = name == null ? "" : name;
        Page<User> users =
            userRepo.getAllUsersExceptMainUserAndFriends(userId, name, pageable);
        List<UserFriendDto> userFriendDtoList =
            customUserRepo.fillListOfUserWithCountOfMutualFriendsAndChatIdForCurrentUser(userId, users.getContent());
        return new PageableDto<>(
            userFriendDtoList,
            users.getTotalElements(),
            users.getPageable().getPageNumber(),
            users.getTotalPages());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<UserFriendDto> findAllUsersByFriendsOfFriends(long userId,
                                                                               @Nullable String name, Pageable pageable) {
        PageableDto<UserFriendDto> allUsersExceptMainUserAndUsersFriend = findAllUsersExceptMainUserAndUsersFriend(userId, name, pageable);
        Page<User> users = userRepo.getAllUsersExceptMainUserAndFriends(userId, name, pageable);
        List<UserFriendDto> userFriendDtoList =
                customUserRepo.fillListOfUserWithCountOfMutualFriendsAndChatIdForCurrentUser(userId, users.getContent());
        allUsersExceptMainUserAndUsersFriend.setPage(userFriendDtoList);
        return allUsersExceptMainUserAndUsersFriend;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<UserFriendDto> getAllUserFriendRequests(long userId, Pageable pageable) {
        Objects.requireNonNull(pageable);

        validateUserExistence(userId);
        Page<User> users = userRepo.getAllUserFriendRequests(userId, pageable);
        List<UserFriendDto> userFriendDtoList =
            customUserRepo.fillListOfUserWithCountOfMutualFriendsAndChatIdForCurrentUser(userId, users.getContent());
        return new PageableDto<>(
            userFriendDtoList,
            users.getTotalElements(),
            users.getPageable().getPageNumber(),
            users.getTotalPages());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<UserFriendDto> findAllFriendsOfUser(long userId, @Nullable String name, Pageable pageable) {
        Objects.requireNonNull(pageable);

        validateUserExistence(userId);
        name = name == null ? "" : name;
        Page<User> users = userRepo.findAllFriendsOfUser(userId, name, pageable);
        List<UserFriendDto> userFriendDtoList =
            customUserRepo.fillListOfUserWithCountOfMutualFriendsAndChatIdForCurrentUser(userId, users.getContent());
        userFriendDtoList.forEach(friend -> friend.setFriendStatus("FRIEND"));
        return new PageableDto<>(
            userFriendDtoList,
            users.getTotalElements(),
            users.getPageable().getPageNumber(),
            users.getTotalPages());
    }

    private void validateUserAndFriendExistence(long userId, long friendId) {
        validateUserExistence(userId);
        validateUserExistence(friendId);
    }

    private void validateUserExistence(long userId) {
        if (!userRepo.existsById(userId)) {
            throw new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId);
        }
    }

    private void validateFriendRequestNotSent(long userId, long friendId) {
        if (userRepo.isFriendRequested(userId, friendId)) {
            throw new BadRequestException(ErrorMessage.FRIEND_REQUEST_ALREADY_SENT);
        }
    }

    private void validateFriendNotExists(long userId, long friendId) {
        if (userRepo.isFriend(userId, friendId)) {
            throw new BadRequestException(ErrorMessage.FRIEND_EXISTS + friendId);
        }
    }

    private void validateFriendRequestSentByFriend(long userId, long friendId) {
        if (!userRepo.isFriendRequestedByCurrentUser(friendId, userId)) {
            throw new NotFoundException(ErrorMessage.FRIEND_REQUEST_NOT_SENT);
        }
    }

    private void validateFriends(long userId, long friendId) {
        if (!userRepo.isFriend(userId, friendId)) {
            throw new NotDeletedException(ErrorMessage.USER_FRIENDS_LIST + friendId);
        }
    }

    private void validateUserAndFriendNotSamePerson(long userId, long friendId) {
        if (userId == friendId) {
            throw new BadRequestException(ErrorMessage.OWN_USER_ID + friendId);
        }
    }
}
