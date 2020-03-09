package greencity.repository;

import greencity.entity.EcoNews;
import greencity.entity.localization.EcoNewsTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    @Query(nativeQuery = true, value = "" +
            "SELECT * FROM eco_news_translations as tr " +
            "INNER JOIN eco_news as e on e.id = tr.eco_news_id " +
            "WHERE tr.language_id = " +
            "(SELECT l.id FROM languages as l WHERE l.code = :language) " +
            "AND tr.eco_news_id IN (SELECT coun.id FROM " +
            "(SELECT en.eco_news_id as id, count(t.id) as c " +
            "FROM eco_news_tags as en " +
            "INNER JOIN tags as t on en.tags_id = t.id " +
            "WHERE t.name IN :tags GROUP BY en.eco_news_id) as coun " +
            "WHERE coun.c = :countOfTags) " +
            "ORDER BY e.creation_date")
    List<EcoNewsTranslation> find(List<String> tags, Long countOfTags, String language);

}
