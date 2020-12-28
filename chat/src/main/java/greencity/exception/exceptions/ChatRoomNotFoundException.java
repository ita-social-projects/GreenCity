package greencity.exception.exceptions;

import greencity.entity.ChatRoom;

/**
 * Exception that we get when we send request to find {@link ChatRoom} by it's
 * parameters and there is no element, then we get
 * {@link ChatRoomNotFoundException}.
 */
public class ChatRoomNotFoundException extends RuntimeException {
    /**
     * Constructor for ChatRoomNotFoundException.
     *
     * @param message - giving message.
     */
    public ChatRoomNotFoundException(String message) {
        super(message);
    }
}