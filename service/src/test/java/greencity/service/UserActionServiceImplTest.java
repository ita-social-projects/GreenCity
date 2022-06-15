package greencity.service;

import greencity.ModelUtils;
import greencity.dto.user.UserVO;
import greencity.dto.useraction.UserActionMessage;
import greencity.entity.User;
import greencity.entity.UserAction;
import greencity.enums.ActionContextType;
import greencity.enums.UserActionType;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.UserActionRepo;
import greencity.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserActionServiceImplTest {
    @InjectMocks
    private UserActionServiceImpl userActionService;
    @Mock
    private UserActionRepo userActionRepo;
    @Mock
    UserRepo userRepo;
    @Mock
    private AchievementServiceImpl achievementService;
    @Mock
    private ModelMapper modelMapper;

    @Test
    void logForUser() {
        User user = ModelUtils.getUser();
        UserActionType actionType = UserActionType.ECO_NEWS_CREATED;
        ActionContextType contextType = ActionContextType.ECO_NEWS;
        Long contextId = 1L;

        when(userActionRepo
            .existsByUserAndActionTypeAndContextTypeAndContextId(user, actionType, contextType, contextId))
                .thenReturn(false);

        userActionService.log(user, actionType, contextType, contextId);

        verify(userActionRepo).saveAndFlush(any(UserAction.class));
        verify(achievementService).tryToGiveUserAchievementsByActionType(user, UserActionType.ECO_NEWS_CREATED);
    }

    @Test
    void logForUserWithAlreadyLoggedAction() {
        User user = ModelUtils.getUser();
        UserActionType actionType = UserActionType.ECO_NEWS_CREATED;
        ActionContextType contextType = ActionContextType.ECO_NEWS;
        Long contextId = 1L;

        when(userActionRepo
            .existsByUserAndActionTypeAndContextTypeAndContextId(user, actionType, contextType, contextId))
                .thenReturn(true);

        userActionService.log(user, actionType, contextType, contextId);

        verify(userActionRepo, never()).saveAndFlush(any(UserAction.class));
        verify(achievementService, never()).tryToGiveUserAchievementsByActionType(user,
            UserActionType.ECO_NEWS_CREATED);
    }

    @Test
    void logForUserVO() {
        User user = ModelUtils.getUser();
        UserVO userVO = ModelUtils.getUserVO();
        UserActionType actionType = UserActionType.ECO_NEWS_CREATED;
        ActionContextType contextType = ActionContextType.ECO_NEWS;
        Long contextId = 1L;

        when(userActionRepo
            .existsByUserAndActionTypeAndContextTypeAndContextId(user, actionType, contextType, contextId))
                .thenReturn(false);
        when(modelMapper.map(userVO, User.class)).thenReturn(user);

        userActionService.log(userVO, UserActionType.ECO_NEWS_CREATED, ActionContextType.ECO_NEWS, 1L);

        verify(userActionRepo).saveAndFlush(any(UserAction.class));
        verify(achievementService).tryToGiveUserAchievementsByActionType(user, UserActionType.ECO_NEWS_CREATED);
    }

    @Test
    void listenAndLog() {
        User user = ModelUtils.getUser();
        UserActionMessage message = ModelUtils.getUserActionMessage();

        when(userRepo.findByEmail(message.getUserEmail())).thenReturn(Optional.of(user));
        when(userActionRepo.existsByUserAndActionTypeAndContextTypeAndContextId(
            user, message.getActionType(), message.getContextType(), message.getContextId()))
                .thenReturn(false);

        userActionService.listenAndLog(message);

        verify(userActionRepo).saveAndFlush(any(UserAction.class));
        verify(achievementService).tryToGiveUserAchievementsByActionType(user, message.getActionType());
    }

    @Test
    void listenAndLogNotFoundException() {
        UserActionMessage message = ModelUtils.getUserActionMessage();

        when(userRepo.findByEmail(message.getUserEmail())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userActionService.listenAndLog(message));
    }
}
