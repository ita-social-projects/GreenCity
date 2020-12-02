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
    @Query("SELECT t FROM Tag t join fetch t.tagTranslations tt WHERE LOWER(tt.name) IN :names")
    List<Tag> findTagsByNames(List<String> names);

    /**
     * Method that allow you to find all EcoNews {@link Tag}s.
     *
     * @return list of {@link Tag}'s names
     */
    @Query(nativeQuery = true,
        value = "SELECT name FROM tag_translations WHERE tag_id IN(SELECT tags_id FROM eco_news_tags) "
            + "and language_id = (select id from languages where code = :languageCode)")
    List<String> findAllEcoNewsTags(String languageCode);

    /**
     * Method that allow you to find all Tips & Tricks {@link Tag}s.
     *
     * @return list of {@link Tag}'s names
     */
    @Query(nativeQuery = true,
        value = "SELECT name FROM tag_translations WHERE tag_id IN(SELECT tags_id FROM tips_and_tricks_tags) "
            + "and language_id = (select id from languages where code = :languageCode)")
    List<String> findAllTipsAndTricksTags(String languageCode);

    /**
     * Method that finds all Habits {@link Tag}'s.
     *
     * @return list of {@link Tag}'s names
     * @author Markiyan Derevetskyi
     */
    @Query(nativeQuery = true,
        value = "SELECT name FROM tag_translations WHERE tag_id IN(SELECT tag_id FROM habits_tags) "
            + "and language_id = (select id from languages where code = :languageCode)")
    List<String> findAllHabitsTags(String languageCode);
}
