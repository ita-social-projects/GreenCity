package greencity.repository;

import greencity.entity.Tag;
import java.util.List;
import java.util.Optional;

import greencity.enums.TagType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TagsRepo extends JpaRepository<Tag, Long>, JpaSpecificationExecutor<Tag> {

    /**
     * Method finds all tags and fetches theirs translations.
     *
     * @param pageable {@link Pageable}
     * @return list of tags {@link Page}
     * @author Markiyan Derevetskyi
     */
    @Query(value = "SELECT t FROM Tag t JOIN FETCH t.tagTranslations ORDER BY t.id",
        countQuery = "SELECT COUNT(t) FROM Tag t")
    Page<Tag> findAll(Pageable pageable);

    @Query("SELECT t from Tag t JOIN FETCH t.tagTranslations WHERE t.id = :id")
    Optional<Tag> findById(Long id);

    /**
     * Method that allow you to find list of {@link Tag}s by names and type.
     *
     * @param names   list of {@link String} values
     * @param tagType {@link String}
     * @return list of {@link Tag}
     */
    @Query("SELECT t FROM Tag t JOIN FETCH t.tagTranslations tt WHERE LOWER(tt.name) IN :names AND t.type = :tagType")
    List<Tag> findTagsByNamesAndType(List<String> names, TagType tagType);

    @Query(value = "SELECT DISTINCT t FROM Tag t LEFT JOIN FETCH t.tagTranslations AS tt "
        + "WHERE CONCAT(t.id, '') LIKE LOWER(CONCAT(:filter, '')) "
        + "OR LOWER(CONCAT(t.type, '')) LIKE LOWER(CONCAT('%', :filter, '%'))"
        + "OR CONCAT(tt.id, '') LIKE LOWER(CONCAT(:filter, '')) "
        + "OR LOWER(tt.language.code) LIKE LOWER(CONCAT('%', :filter, '%')) "
        + "OR LOWER(tt.name) LIKE LOWER(CONCAT('%', :filter, '%'))",
        countQuery = "SELECT COUNT(t) FROM Tag t")
    Page<Tag> filterByAllFields(Pageable pageable, String filter);

    /**
     * Method that allow you to find list of Tags by type and language code.
     *
     * @param tagType {@link TagType}
     * @param languageCode {@link String}
     * @return list of tag's names.
     */
    @Query(nativeQuery = true, value = "select tt.name from tags t "
        + "inner join tag_translations tt on t.id = tt.tag_id "
        + "inner join languages l on l.id = tt.language_id "
        + "where t.type = :tagType and l.code = :languageCode")
    List<String> findTagsByTypeAndLanguageCode(String tagType, String languageCode);

    /**
     * Method that allow you to find all EcoNews {@link Tag}s.
     *
     * @return list of {@link Tag}'s names
     */
    @Query(nativeQuery = true,
        value = "SELECT DISTINCT tt.name FROM tag_translations tt "
            + "INNER JOIN eco_news_tags ent ON tt.tag_id = ent.tags_id "
            + "INNER JOIN languages l ON l.id = tt.language_id "
            + "WHERE l.code = :languageCode")
    List<String> findAllEcoNewsTags(String languageCode);

    /**
     * Method that allow you to find all Tips & Tricks {@link Tag}s.
     *
     * @return list of {@link Tag}'s names
     */
    @Query(nativeQuery = true,
        value = "SELECT DISTINCT tt.name FROM tag_translations tt "
            + "INNER JOIN tips_and_tricks_tags ttt ON tt.tag_id = ttt.tags_id "
            + "INNER JOIN languages l ON l.id = tt.language_id "
            + "WHERE l.code = :languageCode")
    List<String> findAllTipsAndTricksTags(String languageCode);

    /**
     * Method that finds all Habits {@link Tag}'s.
     *
     * @return list of {@link Tag}'s names
     * @author Markiyan Derevetskyi
     */
    @Query(nativeQuery = true,
        value = "SELECT DISTINCT tt.name FROM tag_translations tt "
            + "INNER JOIN habits_tags ent ON tt.tag_id = ent.tag_id "
            + "INNER JOIN languages l ON l.id = tt.language_id "
            + "WHERE l.code = :languageCode")
    List<String> findAllHabitsTags(String languageCode);

    /**
     * Method that deletes all tags by given ids.
     *
     * @param ids - list of {@link Long}.
     */
    @Modifying
    @Query("DELETE FROM Tag t WHERE t.id in :ids")
    void bulkDelete(List<Long> ids);
}