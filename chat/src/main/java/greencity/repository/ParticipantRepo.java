package greencity.repository;

import greencity.entity.Participant;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface ParticipantRepo extends JpaRepository<Participant, Long>,
    JpaSpecificationExecutor<Participant> {
    /**
     * Find not 'DEACTIVATED' {@link Participant} by email.
     *
     * @param email - {@link Participant}'s email
     * @return found {@link Participant}
     */
    @Query("FROM Participant WHERE email=:email AND userStatus <> 1")
    Optional<Participant> findNotDeactivatedByEmail(String email);

    /**
     * {@inheritDoc}
     */
    @Query("From Participant WHERE email!=:email")
    List<Participant> findAllExceptCurrentUser(String email);

    /**
     * {@inheritDoc}
     */
    @Query("FROM Participant p WHERE LOWER(p.name) LIKE LOWER(concat(?1, '%')) AND p.email!=?2")
    List<Participant> findAllParticipantsByQuery(String query, String currentUser);
}
