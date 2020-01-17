package greencity.event;

import greencity.entity.Category;
import greencity.entity.Place;
import greencity.entity.User;
import greencity.entity.enums.EmailNotification;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SendImmediatelyReportEvent extends ApplicationEvent {
    private final List<User> subscribers;
    private final Map<Category, List<Place>> categoriesWithPlacesMap;
    private final EmailNotification emailNotification;

    /**
     * All args constructor.
     */
    public SendImmediatelyReportEvent(Object source,
                                      List<User> subscribers,
                                      Map<Category, List<Place>> categoriesWithPlacesMap,
                                      EmailNotification emailNotification) {
        super(source);
        this.subscribers = subscribers;
        this.categoriesWithPlacesMap = categoriesWithPlacesMap;
        this.emailNotification = emailNotification;
    }
}
