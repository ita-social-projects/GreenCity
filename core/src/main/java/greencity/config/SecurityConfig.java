package greencity.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import greencity.security.filters.AccessTokenAuthenticationFilter;
import greencity.security.jwt.JwtTool;
import greencity.security.providers.JwtAuthenticationProvider;
import greencity.service.UserService;
import java.util.Arrays;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static greencity.constant.AppConstant.*;
import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

/**
 * Config for security.
 *
 * @author Nazar Stasyuk && Yurii Koval
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private static final String ECONEWS_COMMENTS = "/econews/comments";
    private static final String TIPS_AND_TRICKS_COMMENTS = "/tipsandtricks/comments";
    private static final String USER_CUSTOM_GOALS = "/user/{userId}/customGoals";
    private static final String HABIT_ASSIGN_ID = "/habit/assign/{habitId}";
    private final JwtTool jwtTool;
    private final UserService userService;

    /**
     * Constructor.
     */

    @Autowired
    public SecurityConfig(JwtTool jwtTool, UserService userService) {
        this.jwtTool = jwtTool;
        this.userService = userService;
    }

    /**
     * Bean {@link PasswordEncoder} that uses in coding password.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Method for configure security.
     *
     * @param http {@link HttpSecurity}
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors()
            .and()
            .csrf()
            .disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilterBefore(
                new AccessTokenAuthenticationFilter(jwtTool, authenticationManager(), userService),
                UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling()
            .authenticationEntryPoint((req, resp, exc) -> resp.sendError(SC_UNAUTHORIZED, "Authorize first."))
            .accessDeniedHandler((req, resp, exc) -> resp.sendError(SC_FORBIDDEN, "You don't have authorities."))
            .and()
            .authorizeRequests()
            .antMatchers("/management/**",
                "/econews/comments/replies/{parentCommentId}")
            .hasRole(ADMIN)
            .antMatchers("/css/**",
                "/img/**")
            .permitAll()
            .antMatchers(HttpMethod.GET,
                ECONEWS_COMMENTS)
            .hasRole(ADMIN)
            .antMatchers(HttpMethod.GET,
                "/ownSecurity/verifyEmail",
                "/ownSecurity/updateAccessToken",
                "/ownSecurity/restorePassword",
                "/googleSecurity",
                "/facebookSecurity/generateFacebookAuthorizeURL",
                "/facebookSecurity/facebook",
                "/factoftheday/",
                "/factoftheday/all",
                "/factoftheday/find",
                "/factoftheday/languages",
                "/category",
                "/place/info/{id}",
                "/place/info/favorite/{placeId}",
                "/favorite_place/favorite/{placeId}",
                "/place/statuses",
                "/habit",
                "/habit/{id}",
                "/habit/{id}/shopping-list",
                "/habit/tags/search",
                "/habit/tags/all",
                "/habit/assign/{id}",
                HABIT_ASSIGN_ID,
                HABIT_ASSIGN_ID + "/all",
                "/habit/assign/active/{date}",
                "/habit/statistic/{habitId}",
                "/habit/statistic/assign/{habitAssignId}",
                "/habit/statistic/todayStatisticsForAllHabitItems",
                "/place/about/{id}",
                "/specification",
                "/econews",
                "/econews/newest",
                "/econews/tags",
                "/econews/tags/all",
                "/econews/recommended",
                "/econews/{id}",
                "/econews/comments/count/comments/{ecoNewsId}",
                "/econews/comments/count/replies/{parentCommentId}",
                "/econews/comments/count/likes",
                "/econews/comments/replies/active/{parentCommentId}",
                "/econews/comments/active",
                TIPS_AND_TRICKS_COMMENTS,
                "/tipsandtricks/comments/count/comments",
                "/tipsandtricks/comments/replies/{parentCommentId}",
                "/tipsandtricks/comments/count/likes",
                "/tipsandtricks/comments/count/replies",
                "/tipsandtricks/{id}",
                "/tipsandtricks",
                "/tipsandtricks/tags",
                "/tipsandtricks/tags/all",
                "/search",
                "/search/econews",
                "/search/tipsandtricks",
                "/user/emailNotifications",
                "/user/activatedUsersAmount",
                "/user/{userId}/habit/assign",
                "/token",
                "/socket/**")
            .permitAll()
            .antMatchers(HttpMethod.POST,
                "/ownSecurity/signUp",
                "/ownSecurity/signIn",
                "/ownSecurity/changePassword",
                "/place/getListPlaceLocationByMapsBounds",
                "/place/filter")
            .permitAll()
            .antMatchers(HttpMethod.GET,
                "/achievements",
                "/advices/random/{habitId}",
                "/advices",
                "/favorite_place/",
                "/goals",
                "/habit/assign",
                HABIT_ASSIGN_ID + "/active",
                HABIT_ASSIGN_ID,
                "/facts",
                "/facts/random/{habitId}",
                "/facts/dayFact/{languageId}",
                "/newsSubscriber/unsubscribe",
                "/place/{status}",
                "/user",
                "/user/goals/habits/{habitId}/shopping-list",
                USER_CUSTOM_GOALS,
                "/user/{userId}/customGoals/available",
                "/user/{userId}/sixUserFriends/",
                "/user/{userId}/profile/",
                "/user/isOnline/{userId}/",
                "/user/{userId}/profileStatistics/",
                "/user/userAndSixFriendsWithOnlineStatus",
                "/user/userAndAllFriendsWithOnlineStatus",
                "/user/{userId}/recommendedFriends/",
                "/user/{userId}/friends/",
                "/user/{userId}/friendRequests/")
                "/chat")
            .hasAnyRole(USER, ADMIN, MODERATOR)
            .antMatchers(HttpMethod.POST,
                "/category",
                "/econews",
                "/econews/comments/{econewsId}",
                "/econews/comments/like",
                "/files/image",
                HABIT_ASSIGN_ID,
                HABIT_ASSIGN_ID + "/custom",
                HABIT_ASSIGN_ID + "/enroll/{date}",
                HABIT_ASSIGN_ID + "/unenroll/{date}",
                "/habit/statistic/{habitId}",
                "/newsSubscriber",
                "/place/{placeId}/comments",
                "/place/propose",
                "/place/save/favorite/",
                "/tipsandtricks/comments/{tipsAndTricksId}",
                "/tipsandtricks/comments/like",
                "/tipsandtricks",
                USER_CUSTOM_GOALS,
                "/user/goals",
                "/user/{userId}/habit",
                "/user/{userId}/userFriend/{friendId}",
                "/user/{userId}/declineFriend/{friendId}",
                "/user/{userId}/acceptFriend/{friendId}")
            .hasAnyRole(USER, ADMIN, MODERATOR)
            .antMatchers(HttpMethod.PUT,
                "/habit/statistic/{id}",
                "/econews/update",
                "/favorite_place/",
                "/ownSecurity",
                "/user/profile")
            .hasAnyRole(USER, ADMIN, MODERATOR)
            .antMatchers(HttpMethod.PATCH,
                ECONEWS_COMMENTS,
                HABIT_ASSIGN_ID,
                "/goals/shoppingList/{userId}",
                HABIT_ASSIGN_ID,
                TIPS_AND_TRICKS_COMMENTS,
                USER_CUSTOM_GOALS,
                "/user/goals/{userGoalId}",
                "/user/profilePicture",
                "/user/deleteProfilePicture")
            .hasAnyRole(USER, ADMIN, MODERATOR)
            .antMatchers(HttpMethod.DELETE,
                ECONEWS_COMMENTS,
                "/econews/{econewsId}",
                "/favorite_place/{placeId}",
                TIPS_AND_TRICKS_COMMENTS,
                USER_CUSTOM_GOALS,
                "/user/goals/user-goals",
                "/user/goals",
                "/user/{userId}/userFriend/{friendId}")
            .hasAnyRole(USER, ADMIN, MODERATOR)
            .antMatchers(HttpMethod.GET,
                "/newsSubscriber",
                "/comments",
                "/comments/{id}",
                "/user/all",
                "/user/roles")
            .hasAnyRole(ADMIN, MODERATOR)
            .antMatchers(HttpMethod.POST,
                "/place/filter/predicate")
            .hasAnyRole(ADMIN, MODERATOR)
            .antMatchers(HttpMethod.PUT,
                "/place/update/")
            .hasAnyRole(ADMIN, MODERATOR)
            .antMatchers(HttpMethod.PATCH,
                "/place/status",
                "/place/statuses")
            .hasAnyRole(ADMIN, MODERATOR)
            .antMatchers(HttpMethod.DELETE,
                "/place/{id}",
                "/place")
            .hasAnyRole(ADMIN, MODERATOR)
            .antMatchers(HttpMethod.POST,
                "/advices",
                "/facts",
                "/user/filter")
            .hasRole(ADMIN)
            .antMatchers(HttpMethod.PUT,
                "/advices/{adviceId}",
                "/facts/{factId}")
            .hasRole(ADMIN)
            .antMatchers(HttpMethod.PATCH,
                "/user",
                "/user/status",
                "/user/role",
                "/user/update/role")
            .hasRole(ADMIN)
            .antMatchers(HttpMethod.DELETE,
                "/advices/{adviceId}",
                "/facts/{factId}",
                "/comments",
                "/tipsandtricks/{id}")
            .hasRole(ADMIN)
            .anyRequest().hasAnyRole(ADMIN);
    }

    /**
     * Method for configure matchers that will be ignored in security.
     *
     * @param web {@link WebSecurity}
     */
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/v2/api-docs/**");
        web.ignoring().antMatchers("/swagger.json");
        web.ignoring().antMatchers("/swagger-ui.html");
        web.ignoring().antMatchers("/swagger-resources/**");
        web.ignoring().antMatchers("/webjars/**");
    }

    /**
     * Method for configure type of authentication provider.
     *
     * @param auth {@link AuthenticationManagerBuilder}
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(new JwtAuthenticationProvider(jwtTool));
    }

    /**
     * Provides AuthenticationManager.
     *
     * @return {@link AuthenticationManager}
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    /**
     * Bean {@link CorsConfigurationSource} that uses for CORS setup.
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(
            Arrays.asList("GET", "POST", "OPTIONS", "DELETE", "PUT", "PATCH"));
        configuration.setAllowedHeaders(
            Arrays.asList(
                "X-Requested-With", "Origin", "Content-Type", "Accept", "Authorization"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Bean {@link GoogleIdTokenVerifier} that uses in verify googleIdToken.
     *
     * @param clientId {@link String} - google client id.
     */
    @Bean
    public GoogleIdTokenVerifier googleIdTokenVerifier(@Value("${google.clientId}") String clientId) {
        return new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
            .setAudience(Collections.singletonList(clientId))
            .build();
    }
}
