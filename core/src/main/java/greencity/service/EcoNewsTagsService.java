package greencity.service;

import greencity.entity.Tag;
import java.util.List;

/**
 * Provides the interface to manage {@link Tag} entity.
 *
 * @author Kovaliv Taras
 * @version 1.0
 */
public interface EcoNewsTagsService {
    /**
     * Method that allow you to find {@link Tag} by name.
     *
     * @param name a value of {@link String}
     * @return {@link Tag}
     */
    Tag findByName(String name);

    /**
     * Method that allow you to find all {@link Tag}.
     *
     * @return list of {@link Tag}'s names
     */
    List<String> findAll();
  
    /**
     * Method that allow you to check is valid all {@link Tag} by name.
     *
     * @param tags list of {@link String}
     * @return {@link Boolean}
     */
    Boolean isAllValid(List<String> tags);
}
