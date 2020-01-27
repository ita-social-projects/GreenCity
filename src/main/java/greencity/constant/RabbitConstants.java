package greencity.constant;

public final class RabbitConstants {
    public static final String PASSWORD_RECOVERY_ROUTING_KEY = "password.recovery";
    public static final String CHANGE_PLACE_STATUS_ROUTING_KEY = "change.place.status";
    public static final String VERIFY_EMAIL_ROUTING_KEY = "verify.email";

    private RabbitConstants() {
    }
}
