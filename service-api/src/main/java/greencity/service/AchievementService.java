package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.achievement.*;
import greencity.dto.user.UserVO;
import greencity.entity.Achievement;
import greencity.entity.User;
import greencity.enums.UserActionType;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AchievementService {
    /**
     * Method for finding all the achievements.
     *
     * @return list of all{@link AchievementDTO}.
     */
    List<AchievementVO> findAll();

    /**
     * Find {@link AchievementVO} for management by page .
     *
     * @param page a value with pageable configuration.
     * @return a dto of {@link PageableAdvancedDto}.
     * @author Orest Mamchuk
     */
    PageableAdvancedDto<AchievementVO> findAll(Pageable page);

    /**
     * {@inheritDoc} Method that allow you to save new achievement
     *
     * @param achievementPostDto a value of {@link AchievementVO}
     * @return a dto of {@link AchievementVO}
     * @author Orest Mamchuk
     */
    AchievementVO save(AchievementPostDto achievementPostDto, MultipartFile icon);

    /**
     * Method for getting {@link AchievementVO} by search query.
     *
     * @param paging {@link Pageable}.
     * @param query  query to search,
     * @return {@link PageableAdvancedDto} of {@link AchievementVO} instances.
     * @author Orest Mamchuk
     */
    PageableAdvancedDto<AchievementVO> searchAchievementBy(Pageable paging, String query);

    /**
     * Method delete {@link AchievementVO} by id.
     *
     * @param id Long
     * @return id of deleted {@link AchievementVO}
     * @author Orest Mamchuk
     */
    Long delete(Long id);

    /**
     * Method that deletes all achievement by given ids.
     *
     * @param ids - list of {@link Long}
     * @author Orest Mamchuk
     */
    void deleteAll(List<Long> ids);

    /**
     * Method find {@link AchievementVO} by id.
     *
     * @param id of {@link AchievementVO}
     * @return {@link AchievementVO}
     * @author Orest Mamchuk
     */
    AchievementVO findById(Long id);

    /**
     * Method updates {@link AchievementVO}.
     *
     * @param achievementManagementDto {@link AchievementManagementDto}
     * @return instance of {@link AchievementPostDto}
     * @author Orest Mamchuk
     */
    AchievementPostDto update(AchievementManagementDto achievementManagementDto);

    /**
     * Returns all unnotified achievement notifications for user.
     *
     * @param userId of {@link UserVO}
     * @return {@link List} of {@link AchievementNotification}
     */
    List<AchievementNotification> findAllUnnotifiedForUser(Long userId);

    /**
     * Checks if the user has completed all conditions for the achievement.
     *
     * @param user        {@link User}
     * @param achievement {@link Achievement}
     * @return {@code true} if user has competed all conditions, {@code false}
     *         otherwise.
     */
    boolean isAchieved(User user, Achievement achievement);

    /**
     * Creates and saves a new {@link greencity.entity.UserAchievement} for the user
     * if all conditions are completed.
     *
     * @param user        {@link User}
     * @param achievement {@link Achievement}
     */
    void tryToGiveUserAchievement(User user, Achievement achievement);

    /**
     * Checks if the user has completed all the conditions for achievements of given
     * {@link UserActionType}. Creates and saves a new
     * {@link greencity.entity.UserAchievement} for each completed achievement.
     *
     * @param user       {@link User}
     * @param actionType {@link UserActionType}
     */
    void tryToGiveUserAchievementsByActionType(User user, UserActionType actionType);
}
