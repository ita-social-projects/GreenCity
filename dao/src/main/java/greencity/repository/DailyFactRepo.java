package greencity.repository;

import greencity.entity.DailyFact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DailyFactRepo extends JpaRepository<DailyFact, Long> {
    @Query("SELECT df FROM DailyFact df WHERE df.user.id = :userId")
    Optional<DailyFact> findByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(df) > 0 FROM DailyFact df WHERE df.user.id = :userId")
    boolean existsByUserId(@Param("userId") Long userId);
}
