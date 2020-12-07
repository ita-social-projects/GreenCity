package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.achievement.AchievementDTO;
import greencity.dto.achievement.AchievementManagementDto;
import greencity.dto.achievement.AchievementPostDto;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.useraction.UserActionVO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AchievementService {
    /**
     * Method for finding all the achievements.
     *
     * @return list of all{@link AchievementDTO}.
     */
    List<AchievementDTO> findAll();

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
    AchievementVO save(AchievementPostDto achievementPostDto);

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
     * Method find {@link AchievementVO} by categoryId and condition.
     *
     * @param categoryId of {@link AchievementVO}
     * @param condition  of {@link AchievementVO}
     * @return {@link AchievementVO}
     * @author Orest Mamchuk
     */
    AchievementVO findByCategoryIdAndCondition(Long categoryId, Integer condition);
//
//    /**
//     * Method updates {@link UserActionVO}.
//     *
//     * @param userActionVO {@link UserActionVO}
//     * @return {@link UserActionVO}
//     * @author Orest Mamchuk
//     */
//    UserActionVO updateUserActions(UserActionVO userActionVO);
//
//    /**
//     * Method find {@link UserActionVO} by id.
//     *
//     * @param id of {@link UserActionVO}
//     * @return {@link UserActionVO}
//     * @author Orest Mamchuk
//     */
//    UserActionVO findUserActionByUserId(Long id);
}
