package greencity.repository;

import greencity.entity.User;
import greencity.entity.UserAction;
import greencity.enums.UserActionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActionRepo extends JpaRepository<UserAction, Long> {
    /**
     * Finds out if action is already logged.
     *
     * @param user       {@link User} of action.
     * @param actionType {@link UserActionType} of action.
     * @param actionId   {@link Long} of action.
     * @return {@code true} if action is logged, {@code false} otherwise.
     */
    boolean existsByUserAndActionTypeAndActionId(User user, UserActionType actionType, Long actionId);

    /**
     * Counts all actions of user by given action type.
     *
     * @param user       {@link User} whose actions are to be counted.
     * @param actionType {@link UserActionType} type of actions.
     * @return {@link Long} - number of actions.
     */
    Long countAllByUserAndActionType(User user, UserActionType actionType);
}
