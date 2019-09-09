package greencity.security.jwt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import greencity.entity.OwnSecurity;
import greencity.entity.User;
import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import greencity.security.jwt.JwtUser;
import java.util.Collection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class JwtUserTest {

    private JwtUser jwtUser;

    @Before
    public void init() {
        jwtUser =
                new JwtUser(
                        User.builder()
                                .id(1L)
                                .firstName("Nazar")
                                .lastName("Stasyuk")
                                .email("nazar.stasyuk@gmail.com")
                                .role(ROLE.ROLE_USER)
                                .userStatus(UserStatus.ACTIVATED)
                                .ownSecurity(
                                        OwnSecurity.builder().password("123123").build())
                                .build());
    }

    @Test
    public void getAuthorities() {
        Collection<? extends GrantedAuthority> authorities = jwtUser.getAuthorities();
        assertEquals(1, authorities.size());
    }

    @Test
    public void getPassword() {
        assertEquals(jwtUser.getPassword(), "123123");
    }

    @Test
    public void getUsername() {
        assertEquals(jwtUser.getUsername(), "nazar.stasyuk@gmail.com");
    }

    @Test
    public void isAccountNonExpired() {
        assertTrue(jwtUser.isAccountNonExpired());
    }

    @Test
    public void isAccountNonLocked() {
        assertTrue(jwtUser.isAccountNonLocked());
    }

    @Test
    public void isCredentialsNonExpired() {
        assertTrue(jwtUser.isCredentialsNonExpired());
    }

    @Test
    public void isEnabled() {
        assertTrue(jwtUser.isEnabled());
    }
}
