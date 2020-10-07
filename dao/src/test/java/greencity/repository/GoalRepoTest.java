package greencity.repository;

import greencity.dto.goal.ShoppingListDtoResponse;
import greencity.entity.Goal;
import greencity.entity.User;
import greencity.entity.enums.EmailNotification;
import greencity.entity.enums.GoalStatus;
import greencity.entity.enums.ROLE;
import static greencity.entity.enums.UserStatus.ACTIVATED;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Sql("classpath:sql/goal.sql")
class GoalRepoTest {

    @Autowired
    GoalRepo goalRepo;

    private final User testUser =
        User.builder()
            .id(1L)
            .dateOfRegistration(LocalDateTime.parse("2019-09-30T00:00"))
            .email("test@email.com")
            .emailNotification(EmailNotification.DISABLED)
            .name("SuperTest")
            .lastVisit(LocalDateTime.parse("2020-09-30T00:00"))
            .role(ROLE.ROLE_USER)
            .userStatus(ACTIVATED)
            .refreshTokenKey("secret")
            .city("New York")
            .build();


    @Test
    void findAvailableGoalsByUserTest() {
        List<Goal> goals = goalRepo.findAvailableGoalsByUser(testUser);
        assertEquals(2, goals.size());
        assertEquals(1, goals.get(0).getId());
    }

    @Test
    void getShoppingListTest() {
        List<ShoppingListDtoResponse> shoppingListDtoResponses = goalRepo.getShoppingList(1L, "en");
        assertEquals(1, shoppingListDtoResponses.size());
        assertEquals("goal translation", shoppingListDtoResponses.get(0).getText());
    }

    @Test
    void changeGoalStatusTest() {
        List<Goal> goals = goalRepo.findAvailableGoalsByUser(testUser);
        goalRepo.changeGoalStatus(1L, 1L, "DISABLED", LocalDateTime.now());
        assertEquals(GoalStatus.DISABLED, goals.get(0).getUserGoals().get(0).getStatus());
    }
}
