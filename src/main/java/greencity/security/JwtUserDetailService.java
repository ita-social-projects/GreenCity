package greencity.security;

import greencity.entity.User;
import greencity.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class JwtUserDetailService implements UserDetailsService {

    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("User with this email not found: " + email);
        }
        return new JwtUser(user);
    }
}
