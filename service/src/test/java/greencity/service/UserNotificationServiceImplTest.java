package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.achievement.ActionDto;
import greencity.dto.notification.NotificationDto;
import greencity.dto.user.UserVO;
import greencity.entity.Notification;
import greencity.entity.User;
import greencity.enums.NotificationType;
import greencity.enums.ProjectName;
import greencity.repository.NotificationRepo;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static greencity.ModelUtils.TEST_USER;
import static greencity.ModelUtils.TEST_USER_VO;
import static greencity.ModelUtils.getActionDto;
import static greencity.ModelUtils.getNotification;
import static greencity.ModelUtils.getNotificationDto;
import static greencity.ModelUtils.getNotificationWithSeveralActionUsers;
import static greencity.ModelUtils.getPageableAdvancedDtoForNotificationDto;
import static greencity.ModelUtils.getPrincipal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserNotificationServiceImplTest {
    private static final String TOPIC = "/topic/";
    private static final String NOTIFICATION = "/notification";
    @InjectMocks
    UserNotificationServiceImpl userNotificationService;
    @Mock
    private NotificationRepo notificationRepo;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private NotificationService notificationService;
    @Mock
    private UserService userService;
    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Test
    void getNotificationsFilteredTest() {
        Notification notification = getNotification();
        NotificationDto notificationDto = getNotificationDto();

        PageRequest page = PageRequest.of(0, 1);
        PageImpl<Notification> notificationPage = new PageImpl<>(List.of(notification), page, 0);

        PageableAdvancedDto<NotificationDto> actual = getPageableAdvancedDtoForNotificationDto();

        when(userService.findByEmail("danylo@gmail.com")).thenReturn(TEST_USER_VO);

        when(notificationRepo.findNotificationsByFilter(TEST_USER.getId(), ProjectName.GREENCITY, null, true, page))
            .thenReturn(notificationPage);
        when(modelMapper.map(notification, NotificationDto.class)).thenReturn(notificationDto);

        PageableAdvancedDto<NotificationDto> expected = userNotificationService
            .getNotificationsFiltered(page, getPrincipal(), "en", ProjectName.GREENCITY, null, true);

        assertEquals(expected, actual);

        verify(userService).findByEmail("danylo@gmail.com");
        verify(notificationRepo).findNotificationsByFilter(TEST_USER.getId(), ProjectName.GREENCITY, null, true, page);
        verify(modelMapper).map(notification, NotificationDto.class);
    }

    @Test
    void notificationSocketTest() {
        ActionDto dto = getActionDto();

        when(notificationRepo.existsByTargetUserIdAndViewedIsFalse(dto.getUserId()))
            .thenReturn(true);
        userNotificationService.notificationSocket(dto);

        verify(messagingTemplate).convertAndSend(TOPIC + dto.getUserId() + NOTIFICATION, true);
        verify(notificationRepo).existsByTargetUserIdAndViewedIsFalse(dto.getUserId());
    }

    @Test
    void createNotificationForAttendersTest() {
        when(modelMapper.map(TEST_USER_VO, User.class)).thenReturn(TEST_USER);
        userNotificationService.createNotificationForAttenders(List.of(TEST_USER_VO), "",
            NotificationType.EVENT_CREATED, 1L);
        verify(modelMapper).map(TEST_USER_VO, User.class);
        verify(messagingTemplate, times(1))
            .convertAndSend(TOPIC + TEST_USER.getId() + NOTIFICATION, true);
    }

    @Test
    void createNotificationForAttendersWithTitleTest() {
        when(modelMapper.map(TEST_USER_VO, User.class)).thenReturn(TEST_USER);
        userNotificationService.createNotificationForAttenders(List.of(TEST_USER_VO), "",
            NotificationType.EVENT_CREATED, 1L, "Title");
        verify(modelMapper).map(TEST_USER_VO, User.class);
        verify(messagingTemplate, times(1))
            .convertAndSend(TOPIC + TEST_USER.getId() + NOTIFICATION, true);
    }

    @Test
    void createNotificationTest() {
        when(modelMapper.map(TEST_USER_VO, User.class)).thenReturn(TEST_USER);
        userNotificationService.createNotification(TEST_USER_VO, TEST_USER_VO, NotificationType.EVENT_CREATED);
        verify(modelMapper, times(2)).map(TEST_USER_VO, User.class);
        verify(messagingTemplate, times(1))
            .convertAndSend(TOPIC + TEST_USER.getId() + NOTIFICATION, true);
    }

    @Test
    void createNotificationWithCustomMessageTest() {
        when(notificationRepo
            .findNotificationByTargetUserIdAndNotificationTypeAndTargetIdAndViewedIsFalse(1L,
                NotificationType.EVENT_CREATED, 1L)).thenReturn(Optional.empty());
        when(modelMapper.map(TEST_USER_VO, User.class)).thenReturn(TEST_USER);
        userNotificationService.createNotification(TEST_USER_VO, TEST_USER_VO,
            NotificationType.EVENT_CREATED, 1L, "Message");

        verify(notificationRepo)
            .findNotificationByTargetUserIdAndNotificationTypeAndTargetIdAndViewedIsFalse(1L,
                NotificationType.EVENT_CREATED, 1L);
        verify(modelMapper, times(2)).map(TEST_USER_VO, User.class);
        verify(messagingTemplate, times(1))
            .convertAndSend(TOPIC + TEST_USER.getId() + NOTIFICATION, true);
    }

    @Test
    void createNotificationWithSecondMessageTest() {
        when(notificationRepo
            .findNotificationByTargetUserIdAndNotificationTypeAndTargetIdAndViewedIsFalse(1L,
                NotificationType.EVENT_CREATED, 1L)).thenReturn(Optional.empty());
        when(modelMapper.map(TEST_USER_VO, User.class)).thenReturn(TEST_USER);
        userNotificationService.createNotification(TEST_USER_VO, TEST_USER_VO,
            NotificationType.EVENT_CREATED, 1L, "Message", 1L,
            "Second Message");

        verify(notificationRepo)
            .findNotificationByTargetUserIdAndNotificationTypeAndTargetIdAndViewedIsFalse(1L,
                NotificationType.EVENT_CREATED, 1L);
        verify(modelMapper, times(2)).map(TEST_USER_VO, User.class);
        verify(messagingTemplate, times(1))
            .convertAndSend(TOPIC + TEST_USER.getId() + NOTIFICATION, true);
    }

    @Test
    void createNewNotificationTest() {
        when(modelMapper.map(TEST_USER_VO, User.class)).thenReturn(TEST_USER);
        userNotificationService.createNewNotification(TEST_USER_VO, NotificationType.EVENT_CREATED,
            1L, "Custom Message");
        verify(modelMapper).map(TEST_USER_VO, User.class);
        verify(messagingTemplate, times(1))
            .convertAndSend(TOPIC + TEST_USER.getId() + NOTIFICATION, true);
    }

    @Test
    void removeActionUserFromNotificationTest() {
        var notification = getNotification();
        when(notificationRepo
            .findNotificationByTargetUserIdAndNotificationTypeAndTargetId(TEST_USER.getId(),
                NotificationType.EVENT_CREATED, 1L))
            .thenReturn(notification);
        userNotificationService
            .removeActionUserFromNotification(TEST_USER_VO, TEST_USER_VO, 1L, NotificationType.EVENT_CREATED);
        verify(notificationRepo).findNotificationByTargetUserIdAndNotificationTypeAndTargetId(TEST_USER.getId(),
            NotificationType.EVENT_CREATED, 1L);
    }

    @Test
    void removeActionUserFromNotificationWithSeveralActionUsersTest() {
        var notification = getNotificationWithSeveralActionUsers(3);
        when(notificationRepo.findNotificationByTargetUserIdAndNotificationTypeAndTargetId(TEST_USER.getId(),
            NotificationType.EVENT_CREATED, 1L))
            .thenReturn(notification);
        when(modelMapper.map(TEST_USER_VO, User.class)).thenReturn(TEST_USER);
        userNotificationService
            .removeActionUserFromNotification(TEST_USER_VO, TEST_USER_VO, 1L, NotificationType.EVENT_CREATED);

        verify(notificationRepo).findNotificationByTargetUserIdAndNotificationTypeAndTargetId(TEST_USER.getId(),
            NotificationType.EVENT_CREATED, 1L);
        verify(modelMapper).map(TEST_USER_VO, User.class);
    }

    @Test
    void removeActionUserFromNotificationIfNotificationIsNullTest() {
        when(notificationRepo.findNotificationByTargetUserIdAndNotificationTypeAndTargetId(TEST_USER.getId(),
            NotificationType.EVENT_CREATED, 1L)).thenReturn(null);
        userNotificationService.removeActionUserFromNotification(TEST_USER_VO, TEST_USER_VO, 1L,
            NotificationType.EVENT_CREATED);

        verify(notificationRepo).findNotificationByTargetUserIdAndNotificationTypeAndTargetId(TEST_USER.getId(),
            NotificationType.EVENT_CREATED, 1L);
    }

    @Test
    void deleteNotificationTest() {
        when(userService.findByEmail("danylo@gmail.com")).thenReturn(TEST_USER_VO);

        userNotificationService.deleteNotification(getPrincipal(), 1L);

        verify(userService).findByEmail("danylo@gmail.com");
    }

    @Test
    void unreadNotificationTest() {
        Long notificationId = 1L;
        Long userId = 1L;
        Notification notification = mock(Notification.class);
        User user = mock(User.class);

        when(notificationRepo.findById(notificationId)).thenReturn(Optional.of(notification));
        when(notification.getTargetUser()).thenReturn(user);
        when(user.getId()).thenReturn(userId);
        when(notificationRepo.countByTargetUserIdAndViewedIsFalse(userId)).thenReturn(0L);

        userNotificationService.unreadNotification(notificationId);

        verify(notificationRepo).findById(notificationId);
        verify(notificationRepo).countByTargetUserIdAndViewedIsFalse(userId);
        verify(messagingTemplate).convertAndSend(TOPIC + userId + NOTIFICATION, true);
        verify(notificationRepo).markNotificationAsNotViewed(notificationId);
    }

    @Test
    void viewNotificationTest() {
        Long notificationId = 1L;
        Long userId = 1L;
        Notification notification = mock(Notification.class);
        User user = mock(User.class);

        when(notificationRepo.findById(notificationId)).thenReturn(Optional.of(notification));
        when(notification.getTargetUser()).thenReturn(user);
        when(user.getId()).thenReturn(userId);
        when(notificationRepo.countByTargetUserIdAndViewedIsFalse(userId)).thenReturn(1L);

        userNotificationService.viewNotification(notificationId);

        verify(notificationRepo).findById(notificationId);
        verify(notificationRepo).countByTargetUserIdAndViewedIsFalse(userId);
        verify(messagingTemplate).convertAndSend(TOPIC + userId + NOTIFICATION, false);
        verify(notificationRepo).markNotificationAsViewed(notificationId);
    }

    @Test
    @DisplayName("createOrUpdateHabitInviteNotification method updates existing notification")
    void testCreateOrUpdateHabitInviteNotification_UpdateExistingNotification() {
        UserVO targetUserVO = mock(UserVO.class);
        UserVO actionUserVO = mock(UserVO.class);
        User actionUser = mock(User.class);
        Long habitId = 1L;
        String habitName = "Test Habit";

        Notification existingNotification = mock(Notification.class);
        List<User> actionUsers = new ArrayList<>();
        when(existingNotification.getActionUsers()).thenReturn(actionUsers);
        when(notificationRepo.findNotificationByTargetUserIdAndNotificationTypeAndTargetIdAndViewedIsFalse(anyLong(),
            any(), anyLong()))
            .thenReturn(Optional.of(existingNotification));
        when(modelMapper.map(actionUserVO, User.class)).thenReturn(actionUser);

        userNotificationService.createOrUpdateHabitInviteNotification(targetUserVO, actionUserVO, habitId, habitName);

        assertEquals(1, actionUsers.size());
        assertEquals(actionUser, actionUsers.getFirst());

        verify(existingNotification).setCustomMessage(anyString());
        verify(existingNotification).setTime(any(LocalDateTime.class));
        verify(notificationRepo).save(existingNotification);
    }

    @Test
    @DisplayName("createOrUpdateHabitInviteNotification method creates new notification")
    void testCreateOrUpdateHabitInviteNotification_CreateNewNotification() {
        UserVO targetUserVO = mock(UserVO.class);
        UserVO actionUserVO = mock(UserVO.class);
        Long habitId = 1L;
        String habitName = "Test Habit";

        when(notificationRepo.findNotificationByTargetUserIdAndNotificationTypeAndTargetIdAndViewedIsFalse(anyLong(),
            any(), anyLong()))
            .thenReturn(Optional.empty());

        User targetUser = mock(User.class);
        when(modelMapper.map(targetUserVO, User.class)).thenReturn(targetUser);
        when(targetUser.getId()).thenReturn(1L);

        userNotificationService.createOrUpdateHabitInviteNotification(targetUserVO, actionUserVO, habitId, habitName);

        verify(notificationRepo, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("createOrUpdateLikeNotification updates existing notification when liking")
    void testCreateOrUpdateLikeNotification_UpdateExistingNotification_AddLike() {
        UserVO targetUserVO = mock(UserVO.class);
        UserVO actionUserVO = mock(UserVO.class);
        User actionUser = mock(User.class);
        Long newsId = 1L;
        String newsTitle = "Test News";

        Notification existingNotification = mock(Notification.class);
        List<User> actionUsers = new ArrayList<>();
        when(existingNotification.getActionUsers()).thenReturn(actionUsers);
        when(notificationRepo.findNotificationByTargetUserIdAndNotificationTypeAndTargetIdAndViewedIsFalse(anyLong(),
            any(), anyLong()))
            .thenReturn(Optional.of(existingNotification));
        when(modelMapper.map(actionUserVO, User.class)).thenReturn(actionUser);

        userNotificationService.createOrUpdateLikeNotification(targetUserVO, actionUserVO, newsId, newsTitle, true);

        assertTrue(actionUsers.contains(actionUser), "Action users should contain the actionUser.");

        verify(existingNotification).setCustomMessage(anyString());
        verify(existingNotification).setTime(any(LocalDateTime.class));
        verify(notificationRepo).save(existingNotification);
        verify(notificationRepo, never()).delete(existingNotification);
    }

    @Test
    @DisplayName("createOrUpdateLikeNotification updates existing notification when unliking and deletes it if no users left")
    void testCreateOrUpdateLikeNotification_UpdateExistingNotification_RemoveLike() {
        UserVO targetUserVO = mock(UserVO.class);
        UserVO actionUserVO = mock(UserVO.class);
        User actionUser = mock(User.class);
        Long newsId = 1L;
        String newsTitle = "Test News";

        Notification existingNotification = mock(Notification.class);
        List<User> actionUsers = new ArrayList<>();
        actionUsers.add(actionUser);
        when(existingNotification.getActionUsers()).thenReturn(actionUsers);
        when(notificationRepo.findNotificationByTargetUserIdAndNotificationTypeAndTargetIdAndViewedIsFalse(anyLong(),
            any(), anyLong()))
            .thenReturn(Optional.of(existingNotification));
        when(actionUserVO.getId()).thenReturn(1L);
        when(actionUser.getId()).thenReturn(1L);

        userNotificationService.createOrUpdateLikeNotification(targetUserVO, actionUserVO, newsId, newsTitle, false);

        assertTrue(actionUsers.isEmpty(), "Action users should be empty after unliking.");

        verify(notificationRepo).delete(existingNotification);
        verify(notificationRepo, never()).save(existingNotification);
    }

    @Test
    @DisplayName("createOrUpdateLikeNotification creates new notification when liking and no existing notification")
    void testCreateOrUpdateLikeNotification_CreateNewNotification_AddLike() {
        UserVO targetUserVO = mock(UserVO.class);
        UserVO actionUserVO = mock(UserVO.class);
        User actionUser = mock(User.class);
        Long newsId = 1L;
        String newsTitle = "Test News";

        when(notificationRepo.findNotificationByTargetUserIdAndNotificationTypeAndTargetIdAndViewedIsFalse(anyLong(),
            any(), anyLong()))
            .thenReturn(Optional.empty());
        when(modelMapper.map(any(UserVO.class), eq(User.class))).thenReturn(actionUser);
        when(actionUserVO.getName()).thenReturn("John");

        userNotificationService.createOrUpdateLikeNotification(targetUserVO, actionUserVO, newsId, newsTitle, true);

        verify(notificationRepo, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("createLikeNotificationMessage with two users")
    void testCreateLikeNotificationMessage_TwoUsers() throws Exception {
        User user1 = User.builder().name("Taras").build();
        User user2 = User.builder().name("Petro").build();

        List<User> actionUsers = List.of(user1, user2);
        String newsTitle = "Test News";

        Method method = UserNotificationServiceImpl.class.getDeclaredMethod("createLikeNotificationMessage", List.class,
            String.class);
        method.setAccessible(true);
        String result = (String) method.invoke(userNotificationService, actionUsers, newsTitle);

        assertEquals("Taras and Petro like your news Test News.", result);
    }

    @Test
    @DisplayName("createLikeNotificationMessage with more than two users")
    void testCreateLikeNotificationMessage_MoreThanTwoUsers() throws Exception {
        User user1 = User.builder().name("Taras").build();
        User user2 = User.builder().name("Petro").build();
        User user3 = User.builder().name("Vasyl").build();

        List<User> actionUsers = List.of(user1, user2, user3);
        String newsTitle = "Test News";

        Method method = UserNotificationServiceImpl.class.getDeclaredMethod("createLikeNotificationMessage", List.class,
            String.class);
        method.setAccessible(true);
        String result = (String) method.invoke(userNotificationService, actionUsers, newsTitle);

        assertEquals("Petro, Vasyl and other users like your news Test News.", result);
    }

    @Test
    @DisplayName("createInvitationNotificationMessage with two users")
    void testCreateInvitationNotificationMessage_TwoUsers() throws Exception {
        User user1 = User.builder().name("Taras").build();
        User user2 = User.builder().name("Petro").build();

        List<User> actionUsers = List.of(user1, user2);
        String habitName = "Test Habit";

        Method method = UserNotificationServiceImpl.class.getDeclaredMethod("createInvitationNotificationMessage",
            List.class, String.class);
        method.setAccessible(true);
        String result = (String) method.invoke(userNotificationService, actionUsers, habitName);

        assertEquals("Taras and Petro invite you to add new habit Test Habit.", result);
    }

    @Test
    @DisplayName("createInvitationNotificationMessage with more than two users")
    void testCreateInvitationNotificationMessage_MoreThanTwoUsers() throws Exception {
        User user1 = User.builder().name("Taras").build();
        User user2 = User.builder().name("Petro").build();
        User user3 = User.builder().name("Vasyl").build();

        List<User> actionUsers = List.of(user1, user2, user3);
        String habitName = "Test Habit";

        Method method = UserNotificationServiceImpl.class.getDeclaredMethod("createInvitationNotificationMessage",
            List.class, String.class);
        method.setAccessible(true);
        String result = (String) method.invoke(userNotificationService, actionUsers, habitName);

        assertEquals("Petro, Vasyl and other users invite you to add new habit Test Habit.", result);
    }
}
