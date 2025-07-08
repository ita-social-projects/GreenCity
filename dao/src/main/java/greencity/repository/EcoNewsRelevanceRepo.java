package greencity.repository;

import greencity.dto.econews.RelevantEcoNewsDto;
import greencity.entity.EcoNews;
import greencity.entity.EcoNewsRelevance;
import java.time.ZonedDateTime;
import java.util.List;

import org.hibernate.annotations.NamedNativeQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoNewsRelevanceRepo extends JpaRepository<EcoNewsRelevance, Long> {
    /**
     * Method returns list of EcoNews with relevance and tags by range of creation date.
     *
     * @param startDate start date of search
     * @param endDate   end date of search
     * @return list of RelevantEcoNewsDto
     */
    @Query(value = """
        SELECT new greencity.dto.econews.RelevantEcoNewsDto(en, enr)
        FROM EcoNews en
        LEFT JOIN EcoNewsRelevance enr ON en.id = enr.ecoNews.id
        LEFT JOIN en.tags et
        WHERE en.creationDate >= :startDate
            AND en.creationDate <= :endDate
        ORDER BY en.creationDate
        """)
    List<RelevantEcoNewsDto> findRelevantEcoNewsByCreationDate(@Param("startDate") ZonedDateTime startDate,
                                                               @Param("endDate") ZonedDateTime endDate);

    @Query(value = """
    SELECT *
    FROM eco_news_relevance
    LEFT JOIN eco_news ON eco_news.id = eco_news_relevance.eco_news_id
    LEFT JOIN eco_news_users_likes ON eco_news.id = eco_news_users_likes.eco_news_id
    WHERE eco_news_users_likes.users_id = :id
    ORDER BY eco_news.creation_date DESC
    LIMIT 10
""", nativeQuery = true)
    List<EcoNewsRelevance> findLikedEcoNewsById(@Param("id") Long id);
}

