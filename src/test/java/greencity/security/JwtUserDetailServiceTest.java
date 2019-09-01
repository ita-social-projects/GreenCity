package greencity.security;

import greencity.entity.User;
import greencity.entity.UserOwnSecurity;
import greencity.entity.enums.ROLE;
import greencity.service.impl.UserServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class JwtUserDetailServiceTest {

    @Mock private UserServiceImpl userService;
    @InjectMocks private JwtUserDetailService jwtUserDetailService;

    @Test
    public void loadUserByUsername() {
        String email = "email";
        String password = "password";
        when(userService.findByEmail(any()))
                .thenReturn(
                        User.builder()
                                .id(1L)
                                .email(email)
                                .userOwnSecurity(
                                        UserOwnSecurity.builder().password(password).build())
                                .role(ROLE.USER_ROLE)
                                .build());

        UserDetails userDetails = jwtUserDetailService.loadUserByUsername(email);

        verify(userService, times(1)).findByEmail(any());
        assertEquals(email, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
    }
}
