package greencity.repository;

import greencity.entity.ChatMessage;
import greencity.entity.ChatRoom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ChatMessageRepo extends JpaRepository<ChatMessage, Long>,
    JpaSpecificationExecutor<ChatMessage> {
    /**
     * Method to find all {@link ChatMessage}'s by {@link ChatRoom} id.
     *
     * @param chatRoom {@link ChatRoom} instance.
     * @return list of {@link ChatMessage} instances.
     */
    List<ChatMessage> findAllByRoom(ChatRoom chatRoom);

    /**
     * {@inheritDoc}
     */
    ChatMessage findTopByOrderByIdDesc();

    /**
     * Method to like message.
     *
     * @param messageId {@link Long} id of message.
     * @param userId    {@link Long} id of user.
     */
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "insert into message_like(message_id, participant_id)"
        + " values (:messageId, :userId )")
    void addLikeToMessage(@Param("messageId") Long messageId, @Param("userId") Long userId);

    /**
     * Method to delete like from message.
     *
     * @param messageId {@link Long} id of message.
     * @param userId    {@link Long} id of user.
     */
    @Modifying
    @Transactional
    @Query(nativeQuery = true,
        value = "delete from message_like where message_id = :messageId and participant_id = :userId ")
    void deleteLikeFromMessage(@Param("messageId") Long messageId, @Param("userId") Long userId);

    /**
     * Method return list of {@link greencity.entity.Participant} who liked this
     * message.
     *
     * @param messageId {@link Long} id of message.
     * @return list of {@link Long} instances.
     */
    @Query(nativeQuery = true, value = "select participant_id from message_like where "
        + "message_id = :messageId")
    List<Long> getLikesByMessageId(@Param("messageId") Long messageId);

    /**
     * Method return list of {@link greencity.entity.Participant} who liked this
     * message.
     *
     * @param messageId {@link Long} id of message.
     * @return list of {@link Long} instances.
     */
    @Transactional
    @Query(nativeQuery = true, value = "select participant_id from message_like where "
        + "message_id = :messageId and participant_id = :userId ")
    Long getParticipantIdIfLiked(@Param("messageId") Long messageId, @Param("userId") Long userId);
}
