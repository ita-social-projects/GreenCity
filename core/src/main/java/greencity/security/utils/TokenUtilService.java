package greencity.security.utils;

import greencity.dto.user.UserVO;
import greencity.security.jwt.JwtTool;
import greencity.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Utility service for extracting user-related information from JWT tokens.
 * <p>
 * This service is responsible for parsing JWT tokens found in HTTP requests,
 * extracting user emails, and retrieving corresponding user IDs from the database.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenUtilService {
    private final JwtTool jwtTool;
    private final UserService userService;

    /**
     * Extracts the user ID from the JWT access token found in the HTTP request.
     * <p>
     * The method will:
     * <ul>
     *     <li>Retrieve the token from the request header</li>
     *     <li>Parse the token to extract the email</li>
     *     <li>Look up the user by email</li>
     *     <li>Return the user's ID if found</li>
     * </ul>
     * </p>
     *
     * @param request the {@link HttpServletRequest} containing the Authorization header
     * @return an {@link Optional} containing the user ID if present and valid, otherwise {@link Optional#empty()}
     */
    public Optional<Long> getUserIdFromRequest(HttpServletRequest request) {
        String token = jwtTool.getTokenFromHttpServletRequest(request);
        if (token == null) {
            return Optional.empty();
        }
        try {
            String email = jwtTool.getEmailOutOfAccessToken(token);
            UserVO user = userService.findByEmail(email);
            return Optional.ofNullable(user)
                .map(UserVO::getId);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
