package greencity.service;

import greencity.dto.tag.TagVO;
import greencity.enums.TagType;

import java.util.List;

/**
 * Provides the interface to manage Tag.
 *
 * @author Kovaliv Taras
 * @version 1.0
 */
public interface TagsService {
    /**
     * Method that allow you to find list of Tags by names.
     *
     * @param tags list of {@link String} values
     * @return list of Tags
     */
    List<TagVO> findTagsByNamesAndType(List<String> tags, TagType tagType);

    /**
     * Method that allow you to find list of Tags by type and language code.
     *
     * @param type         {@link String}
     * @param languageCode {@link String}
     * @return list of tag's names.
     */
    List<String> findByTypeAndLanguageCode(String type, String languageCode);

    /**
     * Method that allow you to find all EcoNews Tags.
     *
     * @return list of Tag's names
     */
    List<String> findAllEcoNewsTags(String languageCode);

    /**
     * Method that allow you to find all Tips & Tricks Tags.
     *
     * @return list of Tag's names
     */
    List<String> findAllTipsAndTricksTags(String languageCode);

    /**
     * Method that finds all Habits {@link Tag}'s.
     *
     * @param languageCode {@link String}
     * @return list of {@link Tag}'s names
     * @author Markiyan Derevetskyi
     */
    List<String> findAllHabitsTags(String languageCode);

    /**
     * Method that checks if all Tags are unique.
     *
     * @param tipsAndTricksTagNames list of {@link String} values
     * @return {@link Boolean}
     */
    boolean isAllTipsAndTricksValid(List<String> tipsAndTricksTagNames, TagType type);

    /**
     * Method that checks if there is allowed amount (less than 3) of unique Tags .
     *
     * @param tagNames list of {@link String} values
     * @return {@link Boolean}
     */
    boolean isValidNumOfUniqueTags(List<String> tagNames);
}
