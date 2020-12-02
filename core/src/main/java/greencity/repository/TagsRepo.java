package greencity.repository;

import greencity.entity.Tag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TagsRepo extends JpaRepository<Tag, Long> {
    /**
     * Method that allow you to find list of EcoNews {@link Tag}s by names.
     *
     * @param ecoNewsTagNames list of {@link String} values
     * @return list of {@link Tag}
     */
    @Query("SELECT t FROM Tag t WHERE LOWER(t.name) IN :ecoNewsTagNames")
    List<Tag> findEcoNewsTagsByNames(List<String> ecoNewsTagNames);

    /**
     * Method that allow you to find list of Tips & Tricks {@link Tag}s by names.
     *
     * @param tipsAndTricksTagNames list of {@link String} values
     * @return list of {@link Tag}
     */
    @Query("SELECT t FROM Tag t WHERE LOWER(t.name) IN :tipsAndTricksTagNames")
    List<Tag> findTipsAndTricksTagsByNames(List<String> tipsAndTricksTagNames);

    /**
     * Method that allow you to find all EcoNews {@link Tag}s.
     *
     * @return list of {@link Tag}'s names
     */
    @Query(nativeQuery = true, value =
        "SELECT t.name FROM tags t WHERE t.id IN(SELECT tags_id FROM eco_news_tags)")
    List<String> findAllEcoNewsTags();

    /**
     * Method that allow you to find all Tips & Tricks {@link Tag}s.
     *
     * @return list of {@link Tag}'s names
     */
    @Query(nativeQuery = true, value =
        "SELECT t.name FROM tags t WHERE t.id IN(SELECT tags_id FROM tips_and_tricks_tags)")
    List<String> findAllTipsAndTricksTags();
}
