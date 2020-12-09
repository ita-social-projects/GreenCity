package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.tag.TagPostDto;
import greencity.dto.tag.TagVO;
import greencity.enums.TagType;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Provides the interface to manage Tag.
 *
 * @author Kovaliv Taras
 * @version 1.0
 */
public interface TagsService {
    /**
     * Method that returns all tags.
     *
     * @param pageable {@link Pageable}
     * @return all tags {@link PageableAdvancedDto}
     * */
    PageableAdvancedDto<TagVO> findAll(Pageable pageable);

    /**
     * Method that saves new tag.
     *
     * @param tag - new tag {@link greencity.dto.tag.TagPostDto}
     * @return saved tag {@link TagVO}
     * */
    TagVO save(TagPostDto tag);

    /**
     * Method that allow you to find list of Tags by names.
     *
     * @param tags list of {@link String} values
     * @return list of Tags
     */
    List<TagVO> findTagsByNamesAndType(List<String> tags, TagType tagType);

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
