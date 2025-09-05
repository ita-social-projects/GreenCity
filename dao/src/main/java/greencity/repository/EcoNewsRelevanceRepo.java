package greencity.repository;

import greencity.entity.EcoNewsRelevance;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoNewsRelevanceRepo extends JpaRepository<EcoNewsRelevance, Long> {
    @Query("""
            SELECT enr
            FROM EcoNewsRelevance enr
            JOIN FETCH enr.ecoNews news
            JOIN FETCH news.tags tags
            JOIN news.usersLikedNews u
            WHERE u.id = :id
            ORDER BY news.creationDate DESC
            LIMIT 10
        """)
    List<EcoNewsRelevance> findLikedEcoNewsByUserId(@Param("id") Long id);

    @Query("""
            SELECT enr
            FROM EcoNewsRelevance enr
            WHERE enr.ecoNews.id IN (:ecoNewsIds)
                AND enr.isOutdated = false
        """)
    List<EcoNewsRelevance> findAllByEcoNewsIdIn(@Param("ecoNewsIds") Collection<Long> ecoNewsIds);

    @Query("""
            UPDATE EcoNewsRelevance enr
            SET enr.isOutdated = true
            WHERE enr.ecoNews.id = :id
        """)
    @Modifying
    void setOutdatedTrueByEcoNewsId(@Param("id") Long id);

    @Query("""
        SELECT e.ecoNews.id
        FROM EcoNewsRelevance e
        WHERE e.isOutdated
        """)
    List<Long> findOutdatedEcoNewsIds();
}
