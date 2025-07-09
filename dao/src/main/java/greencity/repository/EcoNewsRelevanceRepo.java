package greencity.repository;

import greencity.entity.EcoNewsRelevance;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoNewsRelevanceRepo extends JpaRepository<EcoNewsRelevance, Long> {
    @Query("""
            SELECT enr
            FROM EcoNewsRelevance enr
            JOIN FETCH enr.ecoNews news
            JOIN news.usersLikedNews u
            WHERE u.id = :id
            ORDER BY news.creationDate DESC
            LIMIT 10
        """)
    List<EcoNewsRelevance> findLikedEcoNewsById(@Param("id") Long id);

    List<EcoNewsRelevance> findAllByEcoNewsIdIn(List<Long> ecoNewsIds);
}

