package greencity.service;

import greencity.entity.Tag;
import java.util.List;

/**
 * Provides the interface to manage {@link Tag} entity.
 *
 * @author Kovaliv Taras
 * @version 1.0
 */
public interface TagsService {
    /**
     * Method that allow you to find list of EcoNews {@link Tag}s by names.
     *
     * @param ecoNewsTagNames list of {@link String} values
     * @return list of {@link Tag}
     */
    List<Tag> findEcoNewsTagsByNames(List<String> ecoNewsTagNames);

    /**
     * Method that allow you to find list of Tips & Tricks {@link Tag}s by names.
     *
     * @param tipsAndTricksTagNames list of {@link String} values
     * @return list of {@link Tag}
     */
    List<Tag> findTipsAndTricksTagsByNames(List<String> tipsAndTricksTagNames);

    /**
     * Method that allow you to find all EcoNews {@link Tag}s.
     *
     * @return list of {@link Tag}'s names
     */
    List<String> findAllEcoNewsTags();

    /**
     * Method that allow you to find all Tips & Tricks {@link Tag}s.
     *
     * @return list of {@link Tag}'s names
     */
    List<String> findAllTipsAndTricksTags();


    /**
     * Method that checks if all {@link Tag}s are unique.
     *
     * @param tipsAndTricksTagNames list of {@link String} values
     * @return {@link Boolean}
     */
    boolean isAllTipsAndTricksValid(List<String> tipsAndTricksTagNames);

    /**
     * Method that checks if there is allowed amount (less than 3) of unique {@link Tag}s .
     *
     * @param tagNames list of {@link String} values
     * @return {@link Boolean}
     */
    boolean isValidNumOfUniqueTags(List<String> tagNames);
}
