package greencity.repository;

import greencity.dto.econews.RelevantEcoNewsDto;
import greencity.entity.EcoNewsRelevance;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}

