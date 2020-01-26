package greencity.constant;

public final class RabbitConstants {
    public static final String PASSWORD_RECOVERY_ROUTING_KEY = "password.recovery";
    public static final String CHANGE_PLACE_STATUS_ROUTING_KEY = "change.place.status";
    public static final String ADD_ECO_NEWS_ROUTING_KEY = "eco_news.add";
    public static final String ADD_ECO_NEWS_QUEUE_NAME = "eco_news_queue";
    public static final String EMAIL_TOPIC_EXCHANGE_NAME = "email-topic-exchange";

    private RabbitConstants() {
    }
}
