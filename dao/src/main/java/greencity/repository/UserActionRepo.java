package greencity.repository;

import greencity.entity.User;
import greencity.entity.UserAction;
import greencity.enums.ActionContextType;
import greencity.enums.UserActionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActionRepo extends JpaRepository<UserAction, Long> {
    /**
     * Finds out if action is already logged.
     *
     * @param user        {@link User} of action.
     * @param actionType  {@link UserActionType} of action.
     * @param contextType {@link ActionContextType} of action.
     * @param contextId   {@link Long} of action.
     * @return {@code true} if action is logged, {@code false} otherwise.
     */
    boolean existsByUserAndActionTypeAndContextTypeAndContextId(
        User user, UserActionType actionType, ActionContextType contextType, Long contextId);

    /**
     * Counts all actions of user by given action type.
     *
     * @param user       {@link User} whose actions are to be counted.
     * @param actionType {@link UserActionType} type of actions.
     * @return {@link Long} - number of actions.
     */
    Long countAllByUserAndActionType(User user, UserActionType actionType);
}
