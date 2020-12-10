package greencity.repository;

import greencity.entity.DirectRoom;
import greencity.entity.Participant;
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
     * Method to find {@link DirectRoom} by two {@link Participant} id's
     * (order not matters).
     *
     * @param firstPartId  first {@link Participant} id.
     * @param secondPartId second {@link Participant} id.
     * @return {@link DirectRoom} instance.
     */
    @Query(value = "SELECT dr FROM DirectRoom dr"
        + " WHERE dr.firstParticipant.id = :firstPartId"
        + " AND dr.secondParticipant.id = :secondPartId OR"
        + " dr.firstParticipant.id = :secondPartId"
        + " AND dr.secondParticipant.id = :firstPartId")
    Optional<DirectRoom> findByParticipants(@Param("firstPartId") Long firstPartId,
                                            @Param("secondPartId") Long secondPartId);

    /**
     * Method to find all {@link DirectRoom}'s by {@link Participant} id
     * (order not matters).
     *
     * @param partId {@link Participant} id.
     * @return list of {@link DirectRoom} instances.
     */
    @Query(value = "SELECT dr FROM DirectRoom dr"
        + " WHERE dr.firstParticipant.id = :partId"
        + " OR dr.secondParticipant.id = :partId")
    List<DirectRoom> findAllByParticipant(@Param("partId") Long partId);
}
