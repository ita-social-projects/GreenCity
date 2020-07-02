package greencity.repository;

import greencity.entity.TipsAndTricksTag;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TipsAndTricksTagsRepo extends JpaRepository<TipsAndTricksTag, Long> {
    /**
     * method, that returns {@link TipsAndTricksTag} by it`s name.
     *
     * @param name name of Tag.
     * @return {@link TipsAndTricksTag} by it's name.
     */
    Optional<TipsAndTricksTag> findByName(String name);

    /**
     * Find tips & tricks tags by names from the list.
     *
     * @param tipsAndTricksTagsNames list of {@link String} values
     * @return list of {@link TipsAndTricksTag}
     */
    @Query("SELECT ttt FROM TipsAndTricksTag ttt WHERE lower(ttt.name) IN :tipsAndTricksTagsNames")
    List<TipsAndTricksTag> findAllByNames(List<String> tipsAndTricksTagsNames);
}
