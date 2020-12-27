package greencity.constant;

public final class RabbitConstants {
    public static final String PASSWORD_RECOVERY_ROUTING_KEY = "password.recovery";
    public static final String VERIFY_EMAIL_ROUTING_KEY = "verify.email";
    public static final String SEND_USER_APPROVAL_ROUTING_KEY = "finish.user.approval";

    private RabbitConstants() {
    }
}
