package greencity.repository;

import greencity.entity.DailyFact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DailyFactRepo extends JpaRepository<DailyFact, Long> {
    Optional<DailyFact> findByEmail(String email);

    boolean existsByEmail(String email);

    @Modifying
    @Query("UPDATE DailyFact f SET f.factEn = :factEn, f.factUk = :factUk WHERE f.email = :email")
    int updateFactByEmail(String email, String factEn, String factUk);
}
