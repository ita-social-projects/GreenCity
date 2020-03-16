package greencity.service;

import greencity.entity.Tag;

/**
 * Provides the interface to manage {@link Tag} entity.
 *
 * @author Kovaliv Taras
 * @version 1.0
 */
public interface TagService {
    /**
     * Method that allow you to find {@link Tag} by name.
     *
     * @param name a value of {@link String}
     * @return {@link Tag}
     */
    Tag findByName(String name);
}
