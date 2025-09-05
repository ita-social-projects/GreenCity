package greencity.dto.cache;

/**
 * Cache key for storing relevant eco news for specific user.
 *
 * @param userId   user id
 * @param tags     tags for filtering
 * @param title    title for filtering
 * @param author   author for filtering
 * @param pageSize page size
 * @author Rostyslav Zadyraichuk
 */
public record RelevantEcoNewsCacheKey(
    Long userId,
    String tags,
    String title,
    String author,
    Integer pageSize) {
}
