//package greencity.config;
//
//import io.jsonwebtoken.Jwts;
//
//import java.util.Base64;
//import java.util.function.Function;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class JwtBasedSecurityConfig {
//    /**
//     * Gets email from token.
//     *
//     * @param tokenKey - a tokenKey
//     * @return - parser that gets email from a token
//     */
//    @Bean
//    public Function<String, String> tokenToEmailParser(@Autowired final String tokenKey) {
//        return token -> Jwts.parser()
//                .setSigningKey(tokenKey)
//                .parseClaimsJws(token)
//                .getBody()
//                .getSubject(); // TODO - only email?
//    }
//
//    /**
//     * Returns encoded token key.
//     *
//     * @param rawTokenKey - a token key that is used for signature. Should be specified at application.properties.
//     * @return - encoded token key.
//     */
//    @Bean
//    public String getTokenKey(@Value("${tokenKey}") final String rawTokenKey) {
//        return Base64.getEncoder().encodeToString(rawTokenKey.getBytes());
//    }
//}
