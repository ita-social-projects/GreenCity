package greencity.security.jwt;

import java.util.Collection;
import java.util.Collections;

import greencity.entity.User;
import greencity.entity.enums.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Implementation of {@link UserDetails}.
 *
 * @author Nazar Stasyuk
 * @version 1.0
 */
public class JwtUser implements UserDetails {

    private User user;

    public JwtUser(User user) {
        this.user = user;
    }

    /** {@inheritDoc} */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(user.getRole().name()));
    }
    /** {@inheritDoc} */
    @Override
    public String getPassword() {
        return user.getOwnSecurity().getPassword();
    }
    /** {@inheritDoc} */
    @Override
    public String getUsername() {
        return user.getEmail();
    }
    /** {@inheritDoc} */
    @Override
    public boolean isAccountNonExpired() {
        return user.getVerifyEmail() == null;
    }
    /** {@inheritDoc} */
    @Override
    public boolean isAccountNonLocked() {
        return user.getUserStatus().equals(UserStatus.ACTIVATED);
    }
    /** {@inheritDoc} */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    /** {@inheritDoc} */
    @Override
    public boolean isEnabled() {
        return user.getUserStatus().equals(UserStatus.ACTIVATED);
    }
}
