package greencity.service;

import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.entity.UserAction;
import greencity.enums.UserActionType;

public interface UserActionService {
    /**
     * Logs a new {@link UserAction}.
     *
     * @param user       {@link User}
     * @param actionType {@link UserActionType}
     * @param actionId   {@link Long} id of entity linked to action.
     */
    void log(User user, UserActionType actionType, Long actionId);

    /**
     * Logs a new {@link UserAction}.
     *
     * @param userVO     {@link UserVO}
     * @param actionType {@link UserActionType}
     * @param actionId   {@link Long} id of entity linked to action.
     */
    void log(UserVO userVO, UserActionType actionType, Long actionId);
}
