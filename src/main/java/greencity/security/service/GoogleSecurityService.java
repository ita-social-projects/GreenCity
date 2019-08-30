package greencity.security.service;

import greencity.security.dto.SuccessSignInDto;
import greencity.security.dto.google_security.GoogleSecurityDto;

public interface GoogleSecurityService {
    Boolean isGoogleTokenValid(String token);

    SuccessSignInDto authenticate(GoogleSecurityDto dto);
}
