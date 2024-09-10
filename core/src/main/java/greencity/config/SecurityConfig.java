package greencity.config;

import greencity.security.filters.AccessTokenAuthenticationFilter;
import greencity.security.filters.XSSFilter;
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
    private static final String ADVICES = "/advices";
    private static final String CATEGORIES = "/categories";
    private static final String ECO_NEWS = "/eco-news";
    private static final String ECO_NEWS_ID = "/{ecoNewsId}";
    private static final String ECO_NEWS_COMMENTS = ECO_NEWS + ECO_NEWS_ID + "/comments";
    private static final String REPLIES = "/replies";
    private static final String LIKES = "/likes";
    private static final String DISLIKES = "/dislikes";
    private static final String COUNT = "/count";
    private static final String COMMENT_ID = "/{commentId}";
    private static final String PARENT_COMMENT_ID = "/{parentCommentId}";
    private static final String EVENTS = "/events";
    private static final String FRIENDS = "/friends";
    private static final String HABITS = "/habits";
    private static final String COMMENTS = "/comments";
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
            .addFilterBefore(new XSSFilter(),
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
                .requestMatchers("/css/**", "/img/**").permitAll()
                .requestMatchers(HttpMethod.GET,
                    "/factoftheday/",
                    "/factoftheday/all",
                    "/factoftheday/find/**",
                    "/factoftheday/languages",
                    CATEGORIES,
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
                    HABITS + "/comments/{id}",
                    HABITS + "/comments/{parentCommentId}/replies/active",
                    HABITS + "/{habitId}/comments/count",
                    HABITS + "/comments/{parentCommentId}/replies/active/count",
                    HABITS + "/comments/active",
                    HABITS + "/comments/{commentId}/likes/count",
                    "/place/about/{id}",
                    "/specification",
                    ECO_NEWS,
                    ECO_NEWS + "/tags",
                    ECO_NEWS + ECO_NEWS_ID + "/recommended",
                    ECO_NEWS + ECO_NEWS_ID,
                    ECO_NEWS + ECO_NEWS_ID + LIKES + COUNT,
                    ECO_NEWS + ECO_NEWS_ID + DISLIKES + COUNT,
                    ECO_NEWS_COMMENTS,
                    ECO_NEWS_COMMENTS + PARENT_COMMENT_ID + REPLIES,
                    ECO_NEWS_COMMENTS + PARENT_COMMENT_ID + REPLIES + COUNT,
                    ECO_NEWS_COMMENTS + COUNT,
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
                .requestMatchers(HttpMethod.POST,
                    "/place/getListPlaceLocationByMapsBounds",
                    "/place/filter")
                .permitAll()
                .requestMatchers(HttpMethod.GET,
                    "/achievements",
                    ADVICES,
                    ADVICES + "/{id}",
                    ADVICES + "/random",
                    CUSTOM_SHOPPING_LIST_ITEMS,
                    CUSTOM_SHOPPING_LIST,
                    CUSTOM_SHOPPING_LIST_URL,
                    "/custom/shopping-list-items/{userId}/{habitId}",
                    ECO_NEWS + COUNT,
                    ECO_NEWS + ECO_NEWS_ID + "/summary",
                    ECO_NEWS + ECO_NEWS_ID + LIKES + "/{userId}",
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
                    CATEGORIES,
                    ECO_NEWS,
                    ECO_NEWS + ECO_NEWS_ID + LIKES,
                    ECO_NEWS + ECO_NEWS_ID + DISLIKES,
                    ECO_NEWS_COMMENTS,
                    ECO_NEWS_COMMENTS + COMMENT_ID + LIKES,
                    "/events/comments/{eventId}",
                    "/events/comments/like",
                    EVENTS + "/addAttender/{eventId}",
                    EVENTS + "/addToFavorites/{eventId}",
                    EVENTS + "/create",
                    EVENTS + "/rateEvent/{eventId}/{rate}",
                    CUSTOM_SHOPPING_LIST_ITEMS,
                    "/files",
                    HABIT_ASSIGN_ID,
                    HABIT_ASSIGN_ID + "/custom",
                    "/habit/assign/{habitAssignId}/enroll/**",
                    "/habit/assign/{habitAssignId}/unenroll/{date}",
                    "/habit/statistic/{habitId}",
                    "/habit/like",
                    HABITS + "/{habitId}/comments",
                    HABITS + "/comments/like",
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
                    "/habit/custom",
                    "/custom/shopping-list-items/{userId}/{habitId}/custom-shopping-list-items",
                    FRIENDS + "/{friendId}",
                    "/habit/assign/{habitId}/invite")
                .hasAnyRole(USER, ADMIN, MODERATOR, UBS_EMPLOYEE)
                .requestMatchers(HttpMethod.PUT,
                    "/habit/statistic/{id}",
                    ECO_NEWS + ECO_NEWS_ID,
                    ECO_NEWS_COMMENTS + COMMENT_ID,
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
                    ECO_NEWS + ECO_NEWS_ID,
                    ECO_NEWS_COMMENTS + COMMENT_ID,
                    HABITS + "/comments/{id}",
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
                    "/events/comments/{eventCommentId}",
                    "/user/{userId}/userFriend/{friendId}",
                    "/habit/assign/delete/{habitAssignId}",
                    "/habit/delete/{customHabitId}",
                    FRIENDS,
                    FRIENDS + "/{friendId}",
                    FRIENDS + "/{friendId}/cancelRequest",
                    FRIENDS + "/{friendId}/cancelRequest",
                    "/notification/{notificationId}")
                .hasAnyRole(USER, ADMIN, MODERATOR, UBS_EMPLOYEE)
                .requestMatchers(HttpMethod.GET,
                    "/newsSubscriber",
                    COMMENTS,
                    COMMENTS + "/{id}",
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
                    ADVICES,
                    "/facts",
                    "/user/filter")
                .hasAnyRole(ADMIN)
                .requestMatchers(HttpMethod.PUT,
                    ADVICES + "/{id}",
                    "/facts/{factId}")
                .hasAnyRole(ADMIN)
                .requestMatchers(HttpMethod.PATCH,
                    "/user",
                    "/user/status",
                    "/user/role",
                    "/user/update/role")
                .hasAnyRole(ADMIN)
                .requestMatchers(HttpMethod.DELETE,
                    ADVICES + "/{id}",
                    "/facts/{factId}",
                    COMMENTS)
                .hasAnyRole(ADMIN)
                .requestMatchers(HttpMethod.PATCH,
                    "/events/comments",
                    HABITS + COMMENTS)
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
