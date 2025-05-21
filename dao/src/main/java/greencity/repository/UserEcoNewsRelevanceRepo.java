package greencity.repository;

import greencity.entity.UserEcoNewsRelevance;
import jakarta.persistence.QueryHint;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserEcoNewsRelevanceRepo extends JpaRepository<UserEcoNewsRelevance, Long> {
    @EntityGraph(value = "UserEcoNewsRelevance.withEcoNewsAndAuthor")
    @Query(
        "SELECT r FROM UserEcoNewsRelevance r " +
        "WHERE r.user.id = :userId " +
        "AND r.ecoNews.author.email = :aiUserEmail " +
        "AND r.relevance >= :minRelevance"
    )
    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    List<UserEcoNewsRelevance> findByUserIdAndAIGeneratedNews(
        @Param("userId") Long userId,
        @Param("aiUserEmail") String aiUserEmail,
        @Param("minRelevance") Double minRelevance);

    @EntityGraph(value = "UserEcoNewsRelevance.withEcoNewsAndAuthor")
    @Query(
        "SELECT r FROM UserEcoNewsRelevance r " +
        "WHERE r.user.id = :userId " +
        "AND r.ecoNews.author.email = :aiUserEmail " +
        "AND r.relevance >= :minRelevance"
    )
    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    Page<UserEcoNewsRelevance> findByUserIdAndAIGeneratedNewsPaged(
        @Param("userId") Long userId,
        @Param("aiUserEmail") String aiUserEmail,
        @Param("minRelevance") Double minRelevance,
        Pageable pageable);

    @Query(
        "SELECT r FROM UserEcoNewsRelevance r " +
        "WHERE r.user.id = :userId " +
        "AND r.ecoNews.id = :ecoNewsId"
    )
    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    Optional<UserEcoNewsRelevance> findByUserIdAndEcoNewsId(
        @Param("userId") Long userId,
        @Param("ecoNewsId") Long ecoNewsId);

    @Query("SELECT r.relevance FROM UserEcoNewsRelevance r WHERE r.ecoNews.id = :ecoNewsId")
    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    List<Double> findRelevanceScoresByEcoNewsId(@Param("ecoNewsId") Long ecoNewsId);
}
