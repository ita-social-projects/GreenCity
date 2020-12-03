package greencity.service;

import greencity.dto.goal.BulkCustomGoalDto;
import greencity.dto.goal.BulkSaveCustomGoalDto;
import greencity.dto.goal.CustomGoalResponseDto;
import greencity.dto.user.UserVO;
import java.util.List;

/**
 * Provides the interface to manage {@code CustomGoal} entity.
 *
 * @author Bogdan Kuzenko
 */
public interface CustomGoalService {
    /**
     * Method saves list of custom goals for user.
     *
     * @param bulkSaveCustomGoalDto {@link BulkSaveCustomGoalDto} with objects list
     *                              for saving.
     * @param userId                {@link UserVO} current user id
     * @return list of saved {@link CustomGoalResponseDto}
     */
    List<CustomGoalResponseDto> save(BulkSaveCustomGoalDto bulkSaveCustomGoalDto, Long userId);

    /**
     * Method for finding all custom goals.
     *
     * @return list of {@link CustomGoalResponseDto}
     */
    List<CustomGoalResponseDto> findAll();

    /**
     * Method for finding all custom goal for one user.
     *
     * @param userId user id.
     * @return list of {@link CustomGoalResponseDto}
     */
    List<CustomGoalResponseDto> findAllByUser(Long userId);

    /**
     * Method for finding one custom goal by id.
     *
     * @param id - custom goal id.
     * @return {@link CustomGoalResponseDto}
     */
    CustomGoalResponseDto findById(Long id);

    /**
     * Method for an update list of custom goals object.
     *
     * @param bulkCustomGoalDto {@link BulkCustomGoalDto} with objects list for
     *                          updating.
     * @return list of updated {@link CustomGoalResponseDto}
     */
    List<CustomGoalResponseDto> updateBulk(BulkCustomGoalDto bulkCustomGoalDto);

    /**
     * Method for deleted list of custom goals.
     *
     * @param ids string with objects id for deleting.
     * @return list ids of deleted custom goals
     */
    List<Long> bulkDelete(String ids);
}
