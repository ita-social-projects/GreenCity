package greencity.service;

import greencity.dto.achievementcategory.AchievementCategoryDto;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import java.util.List;

public interface AchievementCategoryService {
    /**
     * Method for saving new {@link AchievementCategoryVO} to database.
     *
     * @param achievementCategoryDto - dto for {@link AchievementCategoryVO} entity.
     * @return a {@link AchievementCategoryVO}.
     * @author Orest Mamchuk
     */
    AchievementCategoryVO save(AchievementCategoryDto achievementCategoryDto);

    /**
     * Method for finding all {@link AchievementCategoryVO}.
     *
     * @return list of {@link AchievementCategoryVO}.
     * @author Orest Mamchuk
     */
    List<AchievementCategoryVO> findAll();

    /**
     * Method for finding {@link AchievementCategoryVO}.
     *
     * @param name - name category for {@link AchievementCategoryVO}
     * @return {@link AchievementCategoryVO}.
     * @author Orest Mamchuk
     */
    AchievementCategoryVO findByName(String name);
}
