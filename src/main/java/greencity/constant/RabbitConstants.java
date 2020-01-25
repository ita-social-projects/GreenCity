package greencity.constant;

public final class RabbitConstants {
    public static final String PASSWORD_RECOVERY_QUEUE = "password-recovery-queue";
    public static final String CHANGE_PLACE_STATUS_QUEUE = "change-place-status";
    public static final String CHANGE_PLACE_STATUS_ROUTING_KEY = "change.place.status";

    private RabbitConstants() {
    }
}
