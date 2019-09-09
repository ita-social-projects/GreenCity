package greencity.security.jwt;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import greencity.entity.OwnSecurity;
import greencity.entity.User;
import greencity.entity.enums.ROLE;
import greencity.security.jwt.JwtUserDetailService;
import greencity.service.impl.UserServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

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
                                .ownSecurity(
                                        OwnSecurity.builder().password(password).build())
                                .role(ROLE.ROLE_USER)
                                .build());

        UserDetails userDetails = jwtUserDetailService.loadUserByUsername(email);

        verify(userService, times(1)).findByEmail(any());
        assertEquals(email, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
    }
}
