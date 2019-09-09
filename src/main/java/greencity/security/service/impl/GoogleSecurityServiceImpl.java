package greencity.security.service.impl;

import greencity.entity.User;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.dto.googlesecurity.GoogleSecurityDto;
import greencity.security.service.GoogleSecurityService;
import greencity.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GoogleSecurityServiceImpl implements GoogleSecurityService {
    private UserService userService;

    @Override
    public Boolean isGoogleTokenValid(String token) {
        return null;
    }

    @Override
    public SuccessSignInDto authenticate(GoogleSecurityDto dto) {
        User byEmail = userService.findByEmail(dto.getEmail());
        return null;
    }
}
