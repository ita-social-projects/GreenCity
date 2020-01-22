package greencity.repository;

import greencity.entity.EcoNews;
import greencity.entity.localization.EcoNewsTranslation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoNewsTranslationRepo extends JpaRepository<EcoNewsTranslation, Long> {
    /**
     * Method returns all {@link EcoNewsTranslation} for specific language.
     *
     * @param languageCode code of the needed language.
     * @return all {@link EcoNewsTranslation} for specific language.
     */
    List<EcoNewsTranslation> findAllByLanguageCode(String languageCode);

    /**
     * Method returns n last {@link EcoNewsTranslation} for specific language.
     *
     * @param n number of needed {@link EcoNewsTranslation}.
     * @return n last {@link EcoNewsTranslation} for specific language.
     */
    @Query(nativeQuery = true, value = ""
        + "SELECT * from eco_news_translations ent "
        + "INNER JOIN eco_news en "
        + "ON ent.eco_news_id = en.id "
        + "WHERE ent.language_id = (SELECT id FROM languages WHERE code = :languageCode) "
        + "ORDER BY en.creation_date DESC "
        + "limit :n")
    List<EcoNewsTranslation> getNLastEcoNewsByLanguageCode(int n, String languageCode);

    /**
     * returns {@link EcoNewsTranslation} for specific {@link EcoNews} and language code.
     *
     * @param ecoNews      {@link EcoNews} for which translation is needed.
     * @param languageCode code of the needed language.
     * @return {@link EcoNewsTranslation} for specific {@link EcoNews} and language code.
     */
    EcoNewsTranslation findByEcoNewsAndLanguageCode(EcoNews ecoNews, String languageCode);
}
