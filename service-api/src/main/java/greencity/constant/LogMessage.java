package greencity.constant;

public final class LogMessage {
    public static final String IN_SAVE = "in save(), entity: {}";
    public static final String IN_FIND_BY_ID = "in findById(), id: {}";
    public static final String IN_DELETE_BY_ID = "in deleteById(), id: {}";
    public static final String IN_FIND_ALL = "in findAll()";
    public static final String IN_UPDATE = "in update(), updated entity: {}";

    private LogMessage() {
    }
}
