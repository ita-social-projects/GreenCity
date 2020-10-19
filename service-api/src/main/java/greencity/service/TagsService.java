package greencity.service;

import greencity.dto.tag.TagVO;
import java.util.List;

/**
 * Provides the interface to manage Tag.
 *
 * @author Kovaliv Taras
 * @version 1.0
 */
public interface TagsService {
    /**
     * Method that allow you to find list of EcoNews Tags by names.
     *
     * @param ecoNewsTagNames list of {@link String} values
     * @return list of Tags
     */
    List<TagVO> findEcoNewsTagsByNames(List<String> ecoNewsTagNames);

    /**
     * Method that allow you to find list of Tips & Tricks Tags by names.
     *
     * @param tipsAndTricksTagNames list of {@link String} values
     * @return list of Tags
     */
    List<TagVO> findTipsAndTricksTagsByNames(List<String> tipsAndTricksTagNames);

    /**
     * Method that allow you to find all EcoNews Tags.
     *
     * @return list of Tag's names
     */
    List<String> findAllEcoNewsTags();

    /**
     * Method that allow you to find all Tips & Tricks Tags.
     *
     * @return list of Tag's names
     */
    List<String> findAllTipsAndTricksTags();


    /**
     * Method that checks if all Tags are unique.
     *
     * @param tipsAndTricksTagNames list of {@link String} values
     * @return {@link Boolean}
     */
    boolean isAllTipsAndTricksValid(List<String> tipsAndTricksTagNames);

    /**
     * Method that checks if there is allowed amount (less than 3) of unique Tags .
     *
     * @param tagNames list of {@link String} values
     * @return {@link Boolean}
     */
    boolean isValidNumOfUniqueTags(List<String> tagNames);
}
