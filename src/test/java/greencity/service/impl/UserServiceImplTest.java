package greencity.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import greencity.GreenCityApplication;
import greencity.dto.user.UserRoleDto;
import greencity.dto.user.UserStatusDto;
import greencity.entity.User;
import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import greencity.exception.BadEmailException;
import greencity.exception.BadIdException;
import greencity.repository.UserRepo;
import java.time.LocalDateTime;
import java.util.Optional;
import javafx.beans.binding.When;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = GreenCityApplication.class)
public class UserServiceImplTest {

    @Mock
    UserRepo userRepo;

    @InjectMocks
    private UserServiceImpl userService;

    User user =
        User.builder()
            .firstName("test")
            .lastName("test")
            .email("test@gmail.com")
            .role(ROLE.ROLE_USER)
            .userStatus(UserStatus.ACTIVATED)
            .lastVisit(LocalDateTime.now())
            .dateOfRegistration(LocalDateTime.now())
            .build();

    @Test
    public void saveTest() {
        when(userRepo.save(user)).thenReturn(user);
        assertEquals(user, userService.save(user));
    }

    @Test
    public void updateUserStatusBlockedTest() {
        when(userRepo.findById(any())).thenReturn(Optional.of(user));
        when(userRepo.save(any())).thenReturn(user);
        ReflectionTestUtils.setField(userService, "modelMapper", new ModelMapper());
        assertEquals(
            UserStatus.BLOCKED,
            userService.updateStatus(user.getId(), UserStatus.BLOCKED).getUserStatus());
    }

    @Test
    public void updateUserStatusDeactivatedTest() {
        User user =
            User.builder()
                .firstName("test")
                .lastName("test")
                .email("test@gmail.com")
                .role(ROLE.ROLE_USER)
                .userStatus(UserStatus.ACTIVATED)
                .lastVisit(LocalDateTime.now())
                .dateOfRegistration(LocalDateTime.now())
                .build();
        when(userRepo.findById(any())).thenReturn(Optional.of(user));
        when(userRepo.save(any())).thenReturn(user);
        ReflectionTestUtils.setField(userService, "modelMapper", new ModelMapper());
        assertEquals(
            UserStatus.DEACTIVATED,
            userService.updateStatus(user.getId(), UserStatus.DEACTIVATED).getUserStatus());
    }

    @Test
    public void updateRoleTest() {
        ReflectionTestUtils.setField(userService, "modelMapper", new ModelMapper());
        when(userRepo.findById(any())).thenReturn(Optional.of(user));
        when(userRepo.save(any())).thenReturn(user);
        ReflectionTestUtils.setField(userService, "modelMapper", new ModelMapper());
        assertEquals(
            ROLE.ROLE_MODERATOR,
            userService.updateRole(user.getId(), ROLE.ROLE_MODERATOR).getRole());
        verify(userRepo, times(1)).save(any());
    }

    @Test
    public void findByIdTest() {
        Long id = 1l;

        User user = new User();
        user.setId(1l);

        when(userRepo.findById(id)).thenReturn(Optional.of(user));

        assertEquals(user, userService.findById(id));
        verify(userRepo, times(1)).findById(id);
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

    @Test(expected = BadIdException.class)
    public void deleteByIdExceptionBadIdTest() {
        userService.deleteById(1l);
    }

    /**
     * @author Zakhar Skaletskyi
     */
    @Test
    public void findIdByEmail() {
        String email = "email";
        when(userRepo.findIdByEmail(email)).thenReturn(2L);
        assertEquals(2L, (long) userService.findIdByEmail(email));
    }
    /**
     * @author Zakhar Skaletskyi
     */
    @Test(expected = BadEmailException.class)
    public void findIdByEmailNotFound() {
        String email = "email";
        when(userRepo.findIdByEmail(email)).thenReturn(null);
        userService.findIdByEmail(email);
    }

    @Test
    public void findByPage() {
        int pageNumber = 0;
        int pageSize = 1;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        User user = new User();
        user.setFirstName("Roman");

        UserForListDto userForListDto = new UserForListDto();
        userForListDto.setFirstName("Roman");

        Page<User> usersPage = new PageImpl<>(Collections.singletonList(user), pageable, 1);
        List<UserForListDto> userForListDtos = Collections.singletonList(userForListDto);

        UserPageableDto userPageableDto =
            new UserPageableDto(userForListDtos,
                userForListDtos.size(), 0, ROLE.class.getEnumConstants());

        ReflectionTestUtils.setField(userService, "modelMapper", new ModelMapper());

        when(userRepo.findAllByOrderByEmail(any())).thenReturn(usersPage);

        assertEquals(userPageableDto, userService.findByPage(any()));
        verify(userRepo, times(1)).findAllByOrderByEmail(any());
    }
}
