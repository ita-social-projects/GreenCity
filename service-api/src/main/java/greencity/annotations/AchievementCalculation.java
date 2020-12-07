package greencity.annotations;

import greencity.dto.achievementcategory.AchievementCategoryVO;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AchievementCalculation {
    /**
     * {@inheritDoc} This method is used for determine the category
     * {@link AchievementCategoryVO}
     *
     * @return achievement category
     * @author Orest Mamchuk
     */
    String category();

    /**
     * {@inheritDoc} This method is used for determine the column name
     *
     * @return column name in table
     * @author Orest Mamchuk
     */
    String column();
}
