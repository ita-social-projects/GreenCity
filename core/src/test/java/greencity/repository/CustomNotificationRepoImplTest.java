package greencity.repository;

import greencity.GreenCityApplication;
import greencity.IntegrationTestBase;
import greencity.entity.Notification;
import greencity.enums.NotificationType;
import greencity.enums.ProjectName;
import greencity.repository.impl.CustomNotificationRepoImpl;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static greencity.repository.ModelUtils.getNotification;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = GreenCityApplication.class)
@Sql(scripts = "/sqlFiles/notificationRepo/insert.sql")
public class CustomNotificationRepoImplTest extends IntegrationTestBase {
    @Autowired
    CustomNotificationRepoImpl customNotificationRepoImpl;

    @Test
    void findNotificationsByFilterTest() {
        Page<Notification> notifications =
            customNotificationRepoImpl.findNotificationsByFilter(1L, ProjectName.GREENCITY,
                List.of(NotificationType.EVENT_CREATED), false, PageRequest.of(0, 20));
        assertEquals(List.of(getNotification()), notifications.getContent());
    }
}
