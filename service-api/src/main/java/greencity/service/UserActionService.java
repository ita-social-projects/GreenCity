package greencity.service;

import greencity.dto.useraction.UserActionVO;

public interface UserActionService {
    /**
     * Method updates {@link UserActionVO}.
     *
     * @param userActionVO {@link UserActionVO}
     * @return {@link UserActionVO}
     * @author Orest Mamchuk
     */
    UserActionVO updateUserActions(UserActionVO userActionVO);

    /**
     * Method find {@link UserActionVO} by id.
     *
     * @param id of {@link UserActionVO}
     * @return {@link UserActionVO}
     * @author Orest Mamchuk
     */
    UserActionVO findUserActionByUserId(Long id);
}
