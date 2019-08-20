package greencity.services.impl;

import static org.junit.Assert.*;

import greencity.GreenCityApplication;
import greencity.entity.User;
import greencity.entity.enums.ROLE;
import greencity.service.UserService;
import java.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GreenCityApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceImplTest {

    @Autowired private UserService userService;

    @Test
    public void blockUserTest() {
        User user =
                User.builder()
                        .firstName("test")
                        .lastName("test")
                        .email("test@gmail.com")
                        .role(ROLE.USER_ROLE)
                        .isBlocked(false)
                        .lastVisit(LocalDateTime.now())
                        .dateOfRegistration(LocalDateTime.now())
                        .build();
        userService.save(user);
        userService.blockUser(user.getId());
        User expectedUser = userService.findById(user.getId());
        assertEquals(true, expectedUser.getIsBlocked());
    }

    @Test
    public void banUserTest() {
        User user =
            User.builder()
                .firstName("test")
                .lastName("test")
                .email("test@gmail.com")
                .role(ROLE.USER_ROLE)
                .isBanned(false)
                .lastVisit(LocalDateTime.now())
                .dateOfRegistration(LocalDateTime.now())
                .build();
        userService.save(user);
        userService.banUser(user.getId());
        User expectedUser = userService.findById(user.getId());
        assertEquals(true, expectedUser.getIsBanned());
    }

    @Test
    public void updateRoleTest() {
        User user =
            User.builder()
                .firstName("test")
                .lastName("test")
                .email("test@gmail.com")
                .role(ROLE.USER_ROLE)
                .lastVisit(LocalDateTime.now())
                .dateOfRegistration(LocalDateTime.now())
                .build();
        userService.save(user);
        userService.updateRole(user.getId(), ROLE.MODERATOR_ROLE);
        User expectedUser = userService.findById(user.getId());
        assertEquals(ROLE.MODERATOR_ROLE, expectedUser.getRole());
    }
}
