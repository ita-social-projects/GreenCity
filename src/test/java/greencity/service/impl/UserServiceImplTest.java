package greencity.service.impl;

import static org.junit.Assert.*;

import greencity.GreenCityApplication;
import greencity.entity.User;
import greencity.entity.enums.ROLE;
import greencity.exception.BadIdException;
import greencity.repository.UserRepo;
import greencity.service.UserService;
import greencity.service.impl.UserServiceImpl;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GreenCityApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceImplTest {

    @Mock UserRepo userRepo;

    private UserService userService;

    @Before
    public void init() {
        userService = new UserServiceImpl(userRepo);
    }

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
        when(userRepo.findById(any())).thenReturn(Optional.of(user));
        when(userRepo.save(any())).thenReturn(user);
        userService.blockUser(user.getId());
        assertEquals(true, user.getIsBlocked());
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
        when(userRepo.findById(any())).thenReturn(Optional.of(user));
        when(userRepo.save(any())).thenReturn(user);
        userService.banUser(user.getId());
        assertEquals(true, user.getIsBanned());
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
        when(userRepo.findById(any())).thenReturn(Optional.of(user));
        when(userRepo.save(any())).thenReturn(user);
        userService.updateRole(user.getId(), ROLE.MODERATOR_ROLE);
        assertEquals(ROLE.MODERATOR_ROLE, user.getRole());
    }

    @Test
    public void findByIdTest() {
        Long id = 1l;

        User user = new User();
        user.setId(1l);

        when(userRepo.findById(id)).thenReturn(Optional.of(user));

        User expectedUser = userService.findById(id);
        assertEquals(user, expectedUser);
    }

    @Test(expected = BadIdException.class)
    public void findByIdBadIdTest() {
        when(userRepo.findById(any())).thenThrow(BadIdException.class);
        userService.findById(1l);
    }
}
