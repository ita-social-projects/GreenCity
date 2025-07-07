package greencity.cache;

public record RelevantEcoNewsCacheKey(
    Long userId,
    String tags,
    String title,
    String author,
    Integer pageSize
) {
}
