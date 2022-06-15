package greencity.service;

import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.entity.UserAction;
import greencity.enums.ActionContextType;
import greencity.enums.UserActionType;

public interface UserActionService {
    /**
     * Logs a new {@link UserAction}.
     *
     * @param user        {@link User}
     * @param actionType  {@link UserActionType}
     * @param contextType {@link Long} type of entity linked to action.
     * @param contextId   {@link Long} id of entity linked to action.
     */
    void log(User user, UserActionType actionType, ActionContextType contextType, Long contextId);

    /**
     * Logs a new {@link UserAction}.
     *
     * @param userVO      {@link UserVO}
     * @param actionType  {@link UserActionType}
     * @param contextType {@link Long} type of entity linked to action.
     * @param contextId   {@link Long} id of entity linked to action.
     */
    void log(UserVO userVO, UserActionType actionType, ActionContextType contextType, Long contextId);
}
