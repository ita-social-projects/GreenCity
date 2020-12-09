package greencity.repository;

import greencity.entity.DirectRoom;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectRoomRepo extends JpaRepository<DirectRoom, Long>,
    JpaSpecificationExecutor<DirectRoom> {
    /**
     * Method to find {@link DirectRoom} by two participants {@link User} id's
     * (order not matters).
     *
     * @param firstPartId  first participant {@link User} id.
     * @param secondPartId second participant {@link User} id.
     * @return {@link DirectRoom} instance.
     */
    @Query(value = "SELECT dr FROM DirectRoom dr"
        + " WHERE dr.firstParticipantId = :firstPartId"
        + " AND dr.secondParticipantId = :secondPartId OR"
        + " dr.firstParticipantId = :secondPartId"
        + " AND dr.secondParticipantId = :firstPartId")
    Optional<DirectRoom> findByParticipants(@Param("firstPartId") Long firstPartId,
                                            @Param("secondPartId") Long secondPartId);

    /**
     * Method to find all {@link DirectRoom}'s by participant {@link User} id
     * (order not matters).
     *
     * @param partId participant {@link User} id.
     * @return list of {@link DirectRoom} instances.
     */
    @Query(value = "SELECT dr FROM DirectRoom dr"
        + " WHERE dr.firstParticipantId = :partId"
        + " OR dr.secondParticipantId = :partId")
    List<DirectRoom> findAllByParticipant(@Param("partId") Long partId);
}
