package greencity.repository;

import greencity.entity.ChatRoom;
import greencity.entity.Participant;
import greencity.enums.ChatType;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepo extends JpaRepository<ChatRoom, Long>,
    JpaSpecificationExecutor<ChatRoom> {
    /**
     * Method to find all {@link ChatRoom}'s by {@link Participant}/{@code User} id.
     *
     * @param participant {@link Participant} id.
     * @return list of {@link ChatRoom} instances.
     */
    @Query(value = "SELECT dr FROM ChatRoom dr"
        + " WHERE (:part) IN dr.participants")
    List<ChatRoom> findAllByParticipant(@Param("part") Participant participant);

    /**
     * Method to find all {@link ChatRoom}'s by {@link Participant}/{@code User}'s and {@link ChatType}.
     *
     * @param participants      {@link Set} of {@link Participant}'s that are in certain rooms.
     * @param participantsCount participants count from passed {@link Set}.
     * @param chatType          {@link ChatType} room type.
     * @return list of {@link ChatRoom} instances.
     */
    @Query(value = "SELECT dr FROM ChatRoom dr"
        + " WHERE dr.participants IN (:participants)"
        + " GROUP BY dr"
        + " HAVING COUNT(dr) = CAST(:participantsCount AS long)"
        + " AND UPPER(dr.type) = :chatType")
    List<ChatRoom> findByParticipantsAndStatus(@Param("participants") Set<Participant> participants,
                                               @Param("participantsCount") Integer participantsCount,
                                               @Param("chatType") ChatType chatType);
}
