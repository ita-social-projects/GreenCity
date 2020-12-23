package greencity.service;

import greencity.dto.ChatRoomDto;
import greencity.entity.ChatRoom;
import greencity.entity.Participant;
import greencity.enums.ChatType;
import java.util.List;
import java.util.Set;

public interface ChatRoomService {
    /**
     * Method to find all {@link ChatRoom}'s by {@link Participant}/{@code User} id.
     *
     * @param name {@link String} name(email) of user.
     * @return {@link ChatRoom} instance.
     */
    List<ChatRoomDto> findAllByParticipantName(String name);

    /**
     * Method to find all {@link ChatRoom}'s by {@link Participant}/{@code User} and
     * {@link ChatType}.
     *
     * @param participants {@link Set} of {@link Participant}'s that are in certain
     *                     rooms.
     * @param chatType     {@link ChatType} room type.
     * @return list of {@link ChatRoom} instances.
     */
    List<ChatRoomDto> findAllRoomsByParticipantsAndStatus(Set<Participant> participants, ChatType chatType);

    /**
     * Method to find {@link ChatRoom} by it's id.
     *
     * @param id {@link ChatRoom} id.
     * @return {@link ChatRoom} instance.
     */
    ChatRoomDto findChatRoomById(Long id);

    /**
     * {@inheritDoc}
     */
    ChatRoomDto findPrivateByParticipants(Long id, String name);
}
