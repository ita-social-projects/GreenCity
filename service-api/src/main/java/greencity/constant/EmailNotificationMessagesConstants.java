package greencity.constant;

public final class EmailNotificationMessagesConstants {
    private EmailNotificationMessagesConstants() {
    }

    public static final String ECONEWS_CREATION_SUBJECT = "You have created eco news";
    public static final String ECONEWS_CREATION_MESSAGE = "you successfully created eco news %s";
    public static final String ECONEWS_LIKE_SUBJECT = "Your news received a like";
    public static final String ECONEWS_LIKE_MESSAGE = "Somebody liked %s";
    public static final String ECONEWS_COMMENTED_SUBJECT = "You received a comment";
    public static final String ECONEWS_COMMENTED_MESSAGE = "You received a comment on your econews: %s";
    public static final String EVENT_CREATION_SUBJECT = "You have created an event";
    public static final String EVENT_CREATION_MESSAGE = "You have created an event: %s";
    public static final String EVENT_CANCELED_SUBJECT = "Event you have joined was canceled";
    public static final String EVENT_CANCELED_MESSAGE = "This event was canceled: %s";
    public static final String EVENT_UPDATED_SUBJECT = "Event you have joined was updated";
    public static final String EVENT_UPDATED_MESSAGE = "This event was updated: %s";
    public static final String EVENT_JOINED_SUBJECT = "New people joined your event";
    public static final String EVENT_JOINED_MESSAGE = "%s has joined your event";
    public static final String EVENT_COMMENTED_SUBJECT = "You received a comment";
    public static final String EVENT_COMMENTED_MESSAGE = "You received a comment on your event: %s";
    public static final String REPLY_SUBJECT = "You received a reply";
    public static final String REPLY_MESSAGE = "%s replied to you";
    public static final String COMMENT_LIKE_SUBJECT = "You receive a like on your comment";
    public static final String COMMENT_LIKE_MESSAGE = "%s liked your comment";
    public static final String FRIEND_REQUEST_RECEIVED_SUBJECT = "You have received a friend request";
    public static final String FRIEND_REQUEST_RECEIVED_MESSAGE = "%s  sent you a friend request";
    public static final String FRIEND_REQUEST_ACCEPTED_SUBJECT = "Your friend request was accepted";
    public static final String FRIEND_REQUEST_ACCEPTED_MESSAGE = "Now you are friends with %s";
    public static final String JOIN_REQUEST_APPROVED_SUBJECT = "You have successfully joined the event";
    public static final String JOIN_REQUEST_APPROVED_MESSAGE = "You have successfully joined %s";
    public static final String JOIN_REQUEST_DECLINED_SUBJECT = "The organizer didn't approve your request";
    public static final String JOIN_REQUEST_DECLINED_MESSAGE =
        "While we can't confirm your attendance this time, there's another way to stay connected! Join our "
            + "organizer's friend list for future updates and opportunities.";
    public static final String NEW_JOIN_REQUEST_SUBJECT = "New people want to join your event";
    public static final String NEW_JOIN_REQUEST_MESSAGE = "%s wants to join your event";
}
