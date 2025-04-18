package greencity.repository;

import greencity.ModelUtils;
import greencity.entity.Notification;
import greencity.entity.User;
import greencity.repository.util.PostgresInitializer;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class NotificationRepoTest extends PostgresInitializer {

    @Autowired
    private NotificationRepo notificationRepo;

    @Autowired
    private EntityManager entityManager;

    @BeforeAll
    static void startContainer() {
        postgreSQLContainer.start();
    }

    @AfterEach
    void teardown() {
        entityManager.createNativeQuery("TRUNCATE TABLE notifications RESTART IDENTITY CASCADE").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE users RESTART IDENTITY CASCADE").executeUpdate();
    }

    @Test
    void findAllByTargetUser_IdOkTest() {
        User user = ModelUtils.getUserWithToken();
        entityManager.merge(user);

        Notification notification1 = ModelUtils.getNotificationsForUser(user).getFirst();
        entityManager.persist(notification1);
        Notification notification2 = ModelUtils.getNotificationsForUser(user).getLast();
        entityManager.persist(notification2);

        entityManager.flush();

        List<Notification> notifications = notificationRepo.findAllByTargetUser_Id(user.getId());

        assertEquals(2, notifications.size());
        assertTrue(notifications.contains(notification1));
        assertTrue(notifications.contains(notification2));
    }

    @Test
    void findAllByTargetUser_IdNoNotificationsFoundTest() {
        User user = ModelUtils.getUserWithToken();
        entityManager.merge(user);

        entityManager.flush();

        List<Notification> notifications = notificationRepo.findAllByTargetUser_Id(user.getId());

        assertEquals(0, notifications.size());
    }

    @Test
    void findAllByIdInOkTest() {
        Pageable pageable = ModelUtils.getPageable();
        List<Long> ids = List.of(1L, 2L);

        User user = ModelUtils.getUserWithToken();
        entityManager.merge(user);

        Notification notification1 = ModelUtils.getNotificationsForUser(user).getFirst();
        entityManager.persist(notification1);
        Notification notification2 = ModelUtils.getNotificationsForUser(user).getLast();
        entityManager.persist(notification2);

        entityManager.flush();

        Page<Notification> notifications = notificationRepo.findAllByIdIn(ids, pageable);

        assertEquals(2, notifications.getTotalElements());
    }

    @Test
    void findAllByIdInNoNotificationsFoundTest() {
        Pageable pageable = ModelUtils.getPageable();
        List<Long> ids = List.of(55L, 44L);

        User user = ModelUtils.getUserWithToken();
        entityManager.merge(user);

        Notification notification1 = ModelUtils.getNotificationsForUser(user).getFirst();
        entityManager.persist(notification1);
        Notification notification2 = ModelUtils.getNotificationsForUser(user).getLast();
        entityManager.persist(notification2);

        entityManager.flush();

        Page<Notification> notifications = notificationRepo.findAllByIdIn(ids, pageable);

        assertEquals(0, notifications.getTotalElements());
    }
}
