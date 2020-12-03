package greencity.repository;

import greencity.entity.Tag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TagsRepo extends JpaRepository<Tag, Long> {
    /**
     * Method that allow you to find list of {@link Tag}s by names.
     *
     * @param names list of {@link String} values
     * @return list of {@link Tag}
     */
    @Query("SELECT t FROM Tag t JOIN FETCH t.tagTranslations tt WHERE LOWER(tt.name) IN :names")
    List<Tag> findTagsByNames(List<String> names);

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
}
