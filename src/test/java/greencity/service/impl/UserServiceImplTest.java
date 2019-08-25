package greencity.service.impl;

import static org.junit.Assert.*;

import greencity.GreenCityApplication;
import greencity.entity.User;
import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import greencity.exception.BadEmailException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GreenCityApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceImplTest {

    @MockBean UserRepo userRepo;

    @Autowired private UserService userService;

    @Test
    public void updateUserStatusBlockedTest() {
        User user =
                User.builder()
                        .firstName("test")
                        .lastName("test")
                        .email("test@gmail.com")
                        .role(ROLE.USER_ROLE)
                        .userStatus(UserStatus.BLOCKED)
                        .lastVisit(LocalDateTime.now())
                        .dateOfRegistration(LocalDateTime.now())
                        .build();
        when(userRepo.findById(any())).thenReturn(Optional.of(user));
        when(userRepo.save(any())).thenReturn(user);
        userService.updateUserStatus(user.getId(), UserStatus.BLOCKED);
        assertEquals(UserStatus.BLOCKED, user.getUserStatus());
    }

    @Test
    public void updateUserStatusDeactivatedTest() {
        User user =
                User.builder()
                        .firstName("test")
                        .lastName("test")
                        .email("test@gmail.com")
                        .role(ROLE.USER_ROLE)
                        .userStatus(UserStatus.DEACTIVATED)
                        .lastVisit(LocalDateTime.now())
                        .dateOfRegistration(LocalDateTime.now())
                        .build();
        when(userRepo.findById(any())).thenReturn(Optional.of(user));
        when(userRepo.save(any())).thenReturn(user);
        userService.updateUserStatus(user.getId(), UserStatus.DEACTIVATED);
        assertEquals(UserStatus.DEACTIVATED, user.getUserStatus());
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

    @Test(expected = BadEmailException.class)
    public void saveExceptionTest() {
        when(userService.findByEmail(any())).thenThrow(BadEmailException.class);
        userService.save(new User());
    }
}
