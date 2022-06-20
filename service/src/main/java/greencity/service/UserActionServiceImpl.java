package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.user.UserVO;
import greencity.dto.useraction.UserActionMessage;
import greencity.entity.User;
import greencity.entity.UserAction;
import greencity.enums.ActionContextType;
import greencity.enums.UserActionType;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.UserActionRepo;
import greencity.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
@AllArgsConstructor
public class UserActionServiceImpl implements UserActionService {
    private final UserActionRepo userActionRepo;
    private final UserRepo userRepo;
    private final AchievementService achievementService;
    private final ModelMapper modelMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public void log(User user, UserActionType actionType, ActionContextType contextType, Long contextId) {
        logWithTimestamp(user, actionType, contextType, contextId, ZonedDateTime.now());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void log(UserVO userVO, UserActionType actionType, ActionContextType contextType, Long contextId) {
        logWithTimestamp(modelMapper.map(userVO, User.class), actionType, contextType, contextId, ZonedDateTime.now());
    }

    private void logWithTimestamp(User user, UserActionType actionType, ActionContextType contextType, Long contextId,
        ZonedDateTime timestamp) {
        if (userActionRepo.existsByUserIdAndActionTypeAndContextTypeAndContextId(user.getId(), actionType, contextType,
            contextId)) {
            return;
        }

        UserAction userAction = UserAction.builder()
            .user(user)
            .timestamp(timestamp)
            .actionType(actionType)
            .contextType(contextType)
            .contextId(contextId).build();
        userActionRepo.saveAndFlush(userAction);

        achievementService.tryToGiveUserAchievementsByActionType(user, actionType);
    }

    /**
     * Listens {@code user.actions} topic and logs {@link UserAction}s.
     */
    @KafkaListener(topics = "${kafka.topic.user.actions}", autoStartup = "${kafka.enable}")
    public void listenAndLog(@Payload UserActionMessage message) {
        User user = userRepo.findByEmail(message.getUserEmail())
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + message.getUserEmail()));
        logWithTimestamp(
            user, message.getActionType(), message.getContextType(), message.getContextId(), message.getTimestamp());
    }
}
