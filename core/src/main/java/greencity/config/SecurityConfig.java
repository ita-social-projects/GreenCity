package greencity.config;

import greencity.security.filters.AccessTokenAuthenticationFilter;
import greencity.security.jwt.JwtTool;
import greencity.security.providers.JwtAuthenticationProvider;
import greencity.service.UserService;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import static greencity.constant.AppConstant.ADMIN;
import static greencity.constant.AppConstant.USER;
import static greencity.constant.AppConstant.MODERATOR;
import static greencity.constant.AppConstant.UBS_EMPLOYEE;
import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

/**
 * Config for security.
 *
 * @author Nazar Stasyuk && Yurii Koval
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@EnableGlobalAuthentication
@RequiredArgsConstructor
public class SecurityConfig {
    private static final String ECONEWS_COMMENTS = "/econews/comments";
    private static final String EVENTS = "/events";
    private static final String FRIENDS = "/friends";
    private static final String USER_CUSTOM_SHOPPING_LIST_ITEMS = "/user/{userId}/custom-shopping-list-items";
    private static final String CUSTOM_SHOPPING_LIST = "/custom/shopping-list-items/{userId}";
    private static final String CUSTOM_SHOPPING_LIST_URL = "/custom/shopping-list-items/{userId}/"
        + "custom-shopping-list-items";
    private static final String CUSTOM_SHOPPING_LIST_ITEMS = "/{userId}/custom-shopping-list-items";
    private static final String HABIT_ASSIGN_ID = "/habit/assign/{habitId}";
    private static final String USER_SHOPPING_LIST = "/user/shopping-list-items";
    private final JwtTool jwtTool;
    private final UserService userService;
    private final AuthenticationConfiguration authenticationConfiguration;

    @Value("${spring.messaging.stomp.websocket.allowed-origins}")
    private String[] allowedOrigins;

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
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOriginPatterns(List.of(allowedOrigins));
            config.setAllowedMethods(
                Arrays.asList("GET", "POST", "OPTIONS", "DELETE", "PUT", "PATCH"));
            config.setAllowedHeaders(
                Arrays.asList("Access-Control-Allow-Origin", "Access-Control-Allow-Headers",
                    "X-Requested-With", "Origin", "Content-Type", "Accept", "Authorization"));
            config.setAllowCredentials(true);
            config.setMaxAge(3600L);
            return config;
        })).csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
            .addFilterBefore(new AccessTokenAuthenticationFilter(jwtTool, authenticationManager(), userService),
                UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(exception -> exception.authenticationEntryPoint((req, resp, exc) -> resp
                .sendError(SC_UNAUTHORIZED, "Authorize first."))
                .accessDeniedHandler((req, resp, exc) -> resp.sendError(SC_FORBIDDEN, "You don't have authorities.")))
            .authorizeHttpRequests(req -> req
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/", "/management/", "/management/login").permitAll()
                .requestMatchers("/management/**").hasAnyRole(ADMIN)
                .requestMatchers("/v2/api-docs/**",
                    "/v3/api-docs/**",
                    "/swagger.json",
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/swagger-resources/**",
                    "/webjars/**")
                .permitAll()
                .requestMatchers("/css/**",
                    "/img/**")
                .permitAll()
                .requestMatchers(HttpMethod.GET, ECONEWS_COMMENTS)
                .hasAnyRole(ADMIN)
                .requestMatchers(HttpMethod.GET,
                    "/factoftheday/",
                    "/factoftheday/all",
                    "/factoftheday/find",
                    "/factoftheday/languages",
                    "/category",
                    "/place/info/{id}",
                    "/place/info/favorite/{placeId}",
                    "/favorite_place/favorite/{placeId}",
                    "/place/statuses",
                    "/place/all",
                    "/habit",
                    "/habit/{id}",
                    "/habit/{id}/shopping-list",
                    "/tags/search",
                    "/tags/v2/search",
                    "/habit/tags/all",
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
                    "/econews/countLikes/{econewsId}",
                    "/econews/comments/replies/{parentCommentId}",
                    "/econews/comments/count/comments/{ecoNewsId}",
                    "/econews/comments/count/replies/{parentCommentId}",
                    "/econews/comments/count/likes",
                    "/econews/comments/replies/active/{parentCommentId}",
                    "/econews/comments/active",
                    "/events/comments/active",
                    "/events/comments/count/{eventId}",
                    "/events/comments/replies/active/{parentCommentId}",
                    "/events/comments/replies/active/count/{parentCommentId}",
                    "/events/comments/likes/count/{commentId}",
                    EVENTS,
                    EVENTS + "/event/{eventId}",
                    EVENTS + "/getAllSubscribers/{eventId}",
                    EVENTS + "/addresses",
                    "/language",
                    "/search",
                    "/search/econews",
                    "/search/events",
                    "/user/emailNotifications",
                    "/user/activatedUsersAmount",
                    "/user/{userId}/habit/assign",
                    "/token",
                    "/socket/**",
                    FRIENDS + "/user/{userId}",
                    "/habit/assign/confirm/{habitAssignId}",
                    "/database/backup",
                    "/database/backupFiles")
                .permitAll()
                .requestMatchers(HttpMethod.DELETE,
                    "/place/{id}",
                    "/place")
                .permitAll()
                .requestMatchers(HttpMethod.POST,
                    "/place/getListPlaceLocationByMapsBounds",
                    "/place/filter")
                .permitAll()
                .requestMatchers(HttpMethod.GET,
                    "/econews/comments/getUsersToTagInComment",
                    "/achievements",
                    "/advices/random/{habitId}",
                    "/advices",
                    CUSTOM_SHOPPING_LIST_ITEMS,
                    CUSTOM_SHOPPING_LIST,
                    CUSTOM_SHOPPING_LIST_URL,
                    "/custom/shopping-list-items/{userId}/{habitId}",
                    "/econews/count",
                    "/econews/isLikedByUser",
                    "/favorite_place/",
                    "/shopping-list-items",
                    "/habit/assign/allForCurrentUser",
                    "/habit/assign/allMutualHabits/{userId}",
                    "/habit/assign/allUser/{userId}",
                    "/habit/assign/myHabits/{userId}",
                    "/habit/assign/active/{date}",
                    "/habit/assign/{habitAssignId}/more",
                    "/habit/assign/activity/{from}/to/{to}",
                    HABIT_ASSIGN_ID + "/active",
                    HABIT_ASSIGN_ID,
                    HABIT_ASSIGN_ID + "/all",
                    "/habit/statistic/acquired/count",
                    "/habit/statistic/in-progress/count",
                    "/facts",
                    "/facts/random/{habitId}",
                    "/facts/dayFact/{languageId}",
                    "/newsSubscriber/unsubscribe",
                    "/place/{status}",
                    "/place/v2/filteredPlacesCategories",
                    "/social-networks/image",
                    "/user",
                    "/user/shopping-list-items/habits/{habitId}/shopping-list",
                    USER_CUSTOM_SHOPPING_LIST_ITEMS,
                    "/user/{userId}/custom-shopping-list-items/available",
                    "/user/{userId}/sixUserFriends/",
                    "/user/{userId}/profile/",
                    "/user/isOnline/{userId}/",
                    "/user/{userId}/profileStatistics/",
                    "/user/userAndSixFriendsWithOnlineStatus",
                    "/user/userAndAllFriendsWithOnlineStatus",
                    "/user/{userId}/recommendedFriends/",
                    "/user/{userId}/friends/",
                    "/user/{userId}/friendRequests/",
                    "/factoftheday/",
                    "/factoftheday/all",
                    "/chat",
                    "/achievements/notification/{userId}",
                    EVENTS + "/myEvents",
                    EVENTS + "/myEvents/createdEvents",
                    EVENTS + "/myEvents/relatedEvents",
                    EVENTS + "/getAllFavoriteEvents",
                    EVENTS + "/userEvents/count",
                    "/user/shopping-list-items/{userId}/get-all-inprogress",
                    "/habit/assign/{habitAssignId}/allUserAndCustomList",
                    "/habit/assign/allUserAndCustomShoppingListsInprogress",
                    "/habit/assign/{habitAssignId}",
                    "/habit/tags/search",
                    "/habit/search",
                    "/habit/{habitId}/friends/profile-pictures",
                    FRIENDS + "/not-friends-yet",
                    FRIENDS + "/recommended-friends",
                    FRIENDS + "/mutual-friends",
                    FRIENDS + "/friendRequests",
                    FRIENDS + "/{userId}/all-user-friends",
                    FRIENDS + "/user-data-as-friend/{friendId}",
                    FRIENDS,
                    "/notification",
                    "/notification/all",
                    "/notification/new",
                    HABIT_ASSIGN_ID + "/friends/habit-duration-info")
                .hasAnyRole(USER, ADMIN, MODERATOR, UBS_EMPLOYEE)
                .requestMatchers(HttpMethod.POST,
                    "/category",
                    "/econews",
                    "/econews/like",
                    "/econews/dislike",
                    "/econews/comments/{econewsId}",
                    "/econews/comments/like",
                    "/events/comments/{eventId}",
                    "/events/comments/like",
                    EVENTS + "/addAttender/{eventId}",
                    EVENTS + "/addToFavorites/{eventId}",
                    EVENTS + "/create",
                    EVENTS + "/rateEvent/{eventId}/{rate}",
                    CUSTOM_SHOPPING_LIST_ITEMS,
                    "/files/image",
                    "/files/convert",
                    HABIT_ASSIGN_ID,
                    HABIT_ASSIGN_ID + "/custom",
                    "/habit/assign/{habitAssignId}/enroll/**",
                    "/habit/assign/{habitAssignId}/unenroll/{date}",
                    "/habit/statistic/{habitId}",
                    "/newsSubscriber",
                    "/place/{placeId}/comments",
                    "/place/propose",
                    "/place/save/favorite/",
                    USER_CUSTOM_SHOPPING_LIST_ITEMS,
                    USER_SHOPPING_LIST,
                    "/user/{userId}/habit",
                    "/user/{userId}/userFriend/{friendId}",
                    "/user/{userId}/declineFriend/{friendId}",
                    "/user/{userId}/acceptFriend/{friendId}",
                    "/achievements/calculate-achievement",
                    "/habit/custom",
                    "/custom/shopping-list-items/{userId}/{habitId}/custom-shopping-list-items",
                    FRIENDS + "/{friendId}",
                    "/habit/assign/{habitId}/invite")
                .hasAnyRole(USER, ADMIN, MODERATOR, UBS_EMPLOYEE)
                .requestMatchers(HttpMethod.PUT,
                    "/habit/statistic/{id}",
                    "/econews/update",
                    "/favorite_place/",
                    "/user/profile",
                    EVENTS + "/update",
                    "/habit/update/{habitId}",
                    HABIT_ASSIGN_ID + "/update-habit-duration",
                    "/habit/assign/{habitAssignId}/updateProgressNotificationHasDisplayed",
                    HABIT_ASSIGN_ID + "/allUserAndCustomList",
                    "/habit/assign/{habitAssignId}/update-status-and-duration")
                .hasAnyRole(USER, ADMIN, MODERATOR, UBS_EMPLOYEE)
                .requestMatchers(HttpMethod.PATCH,
                    ECONEWS_COMMENTS,
                    CUSTOM_SHOPPING_LIST_ITEMS,
                    CUSTOM_SHOPPING_LIST_URL,
                    HABIT_ASSIGN_ID,
                    "/shopping-list-items/shoppingList/{userId}",
                    HABIT_ASSIGN_ID,
                    USER_CUSTOM_SHOPPING_LIST_ITEMS,
                    USER_SHOPPING_LIST + "/{shoppingListItemId}/status/{status}",
                    USER_SHOPPING_LIST + "/{userShoppingListItemId}",
                    "/user/profilePicture",
                    "/user/deleteProfilePicture",
                    "/notification/unread/{notificationId}",
                    "/notification/view/{notificationId}",
                    FRIENDS + "/{friendId}/acceptFriend",
                    FRIENDS + "/{friendId}/declineFriend")
                .hasAnyRole(USER, ADMIN, MODERATOR, UBS_EMPLOYEE)
                .requestMatchers(HttpMethod.DELETE,
                    ECONEWS_COMMENTS,
                    "/events/comments/{eventCommentId}",
                    "/econews/{econewsId}",
                    CUSTOM_SHOPPING_LIST_ITEMS,
                    CUSTOM_SHOPPING_LIST_URL,
                    "/favorite_place/{placeId}",
                    "/social-networks",
                    USER_CUSTOM_SHOPPING_LIST_ITEMS,
                    USER_SHOPPING_LIST + "/user-shopping-list-items",
                    USER_SHOPPING_LIST,
                    EVENTS + "/delete/{eventId}",
                    EVENTS + "/removeAttender/{eventId}",
                    EVENTS + "/removeFromFavorites/{eventId}",
                    "/user/{userId}/userFriend/{friendId}",
                    "/habit/assign/delete/{habitAssignId}",
                    "/habit/delete/{customHabitId}",
                    FRIENDS,
                    FRIENDS + "/{friendId}",
                    FRIENDS + "/{friendId}/cancelRequest",
                    FRIENDS + "/{friendId}/cancelRequest",
                    "/notification/{notificationId}",
                    "/ownSecurity/user")
                .hasAnyRole(USER, ADMIN, MODERATOR, UBS_EMPLOYEE)
                .requestMatchers(HttpMethod.GET,
                    "/newsSubscriber",
                    "/comments",
                    "/comments/{id}",
                    "/user/all",
                    "/user/roles")
                .hasAnyRole(ADMIN, MODERATOR)
                .requestMatchers(HttpMethod.POST,
                    "/place/filter/predicate")
                .hasAnyRole(ADMIN, MODERATOR)
                .requestMatchers(HttpMethod.PUT,
                    "/place/update/")
                .hasAnyRole(ADMIN, MODERATOR)
                .requestMatchers(HttpMethod.PATCH,
                    "/place/status",
                    "/place/statuses")
                .hasAnyRole(ADMIN, MODERATOR)
                .requestMatchers(HttpMethod.DELETE,
                    "/place/{id}",
                    "/place")
                .hasAnyRole(ADMIN, MODERATOR)
                .requestMatchers(HttpMethod.POST,
                    "/advices",
                    "/facts",
                    "/user/filter")
                .hasAnyRole(ADMIN)
                .requestMatchers(HttpMethod.PUT,
                    "/advices/{adviceId}",
                    "/facts/{factId}")
                .hasAnyRole(ADMIN)
                .requestMatchers(HttpMethod.PATCH,
                    "/user",
                    "/user/status",
                    "/user/role",
                    "/user/update/role")
                .hasAnyRole(ADMIN)
                .requestMatchers(HttpMethod.DELETE,
                    "/advices/{adviceId}",
                    "/facts/{factId}",
                    "/comments")
                .hasAnyRole(ADMIN)
                .requestMatchers(HttpMethod.PATCH,
                    "/events/comments")
                .hasAnyRole(USER, ADMIN, MODERATOR, UBS_EMPLOYEE)
                .requestMatchers(HttpMethod.POST,
                    "/events/comments/{eventId}")
                .hasAnyRole(USER, ADMIN, MODERATOR, UBS_EMPLOYEE)
                .anyRequest().hasAnyRole(ADMIN))
            .logout(logout -> logout.logoutUrl("/logout")
                .logoutRequestMatcher(new AntPathRequestMatcher("/management/logout", "GET"))
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .deleteCookies("accessToken")
                .logoutSuccessUrl("/"));
        return http.build();
    }

    /**
     * Method for configure type of authentication provider.
     *
     * @param auth {@link AuthenticationManagerBuilder}
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(new JwtAuthenticationProvider(jwtTool));
    }

    /**
     * Provides AuthenticationManager.
     *
     * @return {@link AuthenticationManager}
     */
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
