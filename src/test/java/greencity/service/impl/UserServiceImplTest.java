package greencity.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import greencity.GreenCityApplication;
import greencity.entity.User;
import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import greencity.exception.BadEmailException;
import greencity.exception.BadIdException;
import greencity.repository.UserRepo;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = GreenCityApplication.class)
public class UserServiceImplTest {

    @Mock
    UserRepo userRepo;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void saveTest() {
        User user = new User();
        when(userRepo.save(user)).thenReturn(user);
        assertEquals(user, userService.save(user));
    }

    @Test
    public void updateUserStatusBlockedTest() {
        User user =
            User.builder()
                .firstName("test")
                .lastName("test")
                .email("test@gmail.com")
                .role(ROLE.ROLE_USER)
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
                .role(ROLE.ROLE_USER)
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
                .role(ROLE.ROLE_USER)
                .lastVisit(LocalDateTime.now())
                .dateOfRegistration(LocalDateTime.now())
                .build();
        when(userRepo.findById(any())).thenReturn(Optional.of(user));
        when(userRepo.save(any())).thenReturn(user);
        userService.updateRole(user.getId(), ROLE.ROLE_MODERATOR);
        assertEquals(ROLE.ROLE_MODERATOR, user.getRole());
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

    @Test
    public void getUserRoleTest() {
        User user = User.builder().email("nazarvladykaaa@gmail.com").role(ROLE.ROLE_ADMIN).build();
        when(userService.findByEmail("nazarvladykaaa@gmail.com")).thenReturn(user);

        assertEquals(ROLE.ROLE_ADMIN, userService.getRole("nazarvladykaaa@gmail.com"));
    }

    @Test(expected = BadIdException.class)
    public void deleteByIdExceptionBadIdTest() {
        userService.deleteById(1l);
    }
}
