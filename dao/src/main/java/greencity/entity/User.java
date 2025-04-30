package greencity.entity;

import greencity.dto.friends.UserFriendDto;
import greencity.dto.user.RegistrationStatisticsDtoResponse;
import greencity.entity.event.Event;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.SqlResultSetMappings;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@SqlResultSetMappings(value = {
    @SqlResultSetMapping(
        name = "monthsStatisticsMapping",
        classes = {
            @ConstructorResult(
                targetClass = RegistrationStatisticsDtoResponse.class,
                columns = {
                    @ColumnResult(name = "month", type = Integer.class),
                    @ColumnResult(name = "count", type = Long.class)
                })
        }),
    @SqlResultSetMapping(
        name = "userFriendDtoMapping",
        classes = {
            @ConstructorResult(
                targetClass = UserFriendDto.class,
                columns = {
                    @ColumnResult(name = "id", type = Long.class),
                    @ColumnResult(name = "name", type = String.class),
                    @ColumnResult(name = "email", type = String.class),
                    @ColumnResult(name = "rating", type = Double.class),
                    @ColumnResult(name = "ulId", type = Long.class),
                    @ColumnResult(name = "cityEn", type = String.class),
                    @ColumnResult(name = "cityUa", type = String.class),
                    @ColumnResult(name = "regionEn", type = String.class),
                    @ColumnResult(name = "regionUa", type = String.class),
                    @ColumnResult(name = "countryEn", type = String.class),
                    @ColumnResult(name = "countryUa", type = String.class),
                    @ColumnResult(name = "latitude", type = Double.class),
                    @ColumnResult(name = "longitude", type = Double.class),
                    @ColumnResult(name = "mutualFriends", type = Long.class),
                    @ColumnResult(name = "profilePicturePath", type = String.class),
                    @ColumnResult(name = "chatId", type = Long.class),
                    @ColumnResult(name = "friendStatus", type = String.class),
                    @ColumnResult(name = "requesterId", type = Long.class)
                })
        })
})
@NamedNativeQueries(value = {
    @NamedNativeQuery(name = "User.findAllRegistrationMonths",
        query = "SELECT EXTRACT(MONTH FROM date_of_registration) - 1 as month, count(date_of_registration) FROM users "
            + "WHERE EXTRACT(YEAR from date_of_registration) = EXTRACT(YEAR FROM CURRENT_DATE) "
            + "GROUP BY month",
        resultSetMapping = "monthsStatisticsMapping"),
    @NamedNativeQuery(name = "User.fillListOfUserWithCountOfMutualFriendsAndChatIdForCurrentUser",
        query = """
                WITH current_user_friends AS (
                    SELECT user_id
                    FROM users_friends
                    WHERE friend_id = :userId AND status = 'FRIEND'
                    UNION
                    SELECT friend_id
                    FROM users_friends
                    WHERE user_id = :userId AND status = 'FRIEND'
                )
                SELECT
                    u.id,
                    u.name,
                    u.email,
                    u.rating,
                    ul.id AS ulId,
                    ul.city_en AS cityEn,
                    ul.city_uk AS cityUa,
                    ul.region_en AS regionEn,
                    ul.region_uk AS regionUa,
                    ul.country_en AS countryEn,
                    ul.country_uk AS countryUa,
                    ul.latitude,
                    ul.longitude,
                    (
                        SELECT COUNT(*)
                        FROM users_friends uf1
                        WHERE (
                            (uf1.friend_id = u.id AND uf1.user_id IN
                                        (
                                            SELECT user_id
                                            FROM current_user_friends) AND uf1.status = 'FRIEND'
                                        )
                                        OR
                                        (
                                            uf1.friend_id IN
                                            (
                                                SELECT user_id FROM current_user_friends
                                            )
                                            AND uf1.user_id = u.id AND uf1.status = 'FRIEND'
                                        )
                        )
                    ) AS mutualFriends,
                    u.profile_picture AS profilePicturePath,
                    (
                        SELECT p.room_id
                        FROM chat_rooms_participants p
                        WHERE p.participant_id IN (u.id, :userId)
                        GROUP BY p.room_id
                        HAVING COUNT(DISTINCT p.participant_id) = 2 LIMIT 1
                    ) AS chatId,
                    (
                        SELECT uf2.status
                        FROM users_friends uf2
                        WHERE (uf2.user_id = :userId AND uf2.friend_id = u.id)
                           OR (uf2.user_id = u.id AND uf2.friend_id = :userId)
                        LIMIT 1
                    ) AS friendStatus,
                    (
                        SELECT uf3.user_id
                        FROM users_friends uf3
                        WHERE (uf3.user_id = :userId AND uf3.friend_id = u.id)
                           OR (uf3.user_id = u.id AND uf3.friend_id = :userId)
                        LIMIT 1
                    ) AS requesterId
                FROM users u
                LEFT JOIN user_location ul ON u.user_location = ul.id
                WHERE
                    u.id IN (:users)
                    OR (
                        (
                            (
                                SELECT uf2.status
                                FROM users_friends uf2
                                WHERE (uf2.user_id = :userId AND uf2.friend_id = u.id)
                                   OR (uf2.user_id = u.id AND uf2.friend_id = :userId)
                                LIMIT 1
                            ) = 'REQUEST'
                        )
                        AND (
                            (
                                SELECT uf3.user_id
                                FROM users_friends uf3
                                WHERE (uf3.user_id = :userId AND uf3.friend_id = u.id)
                                   OR (uf3.user_id = u.id AND uf3.friend_id = :userId)
                                LIMIT 1
                            ) = (:userId)
                        )
                    )
            """,
        resultSetMapping = "userFriendDtoMapping")
})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "greencity_users")
@EqualsAndHashCode(
    exclude = {"emailPreference", "favoriteHabits", "language", "userLocation", "verifyEmail", "ownSecurity",
        "ecoNewsLiked", "refreshTokenKey", "estimates", "restorePasswordEmail",
        "customToDoListItems", "eventOrganizerRating", "favoriteEcoNews", "favoriteEvents", "requestedEvents",
        "subscribedEvents"})
@ToString(
    exclude = {"emailPreference", "favoriteHabits", "language", "userLocation", "verifyEmail", "ownSecurity",
        "refreshTokenKey", "ecoNewsLiked", "estimates", "restorePasswordEmail",
        "customToDoListItems", "eventOrganizerRating", "favoriteEcoNews", "favoriteEvents", "requestedEvents",
        "subscribedEvents"})
public class User {
    @Id
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String email;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(name = "profile_picture")
    private String profilePicturePath;

    @Column(name = "user_credo")
    private String userCredo;

    @Column(name = "rating")
    private Double rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_location")
    private UserLocation userLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    private Language language;

    @Column(name = "event_organizer_rating")
    private Double eventOrganizerRating;

    @Builder.Default
    @OneToMany
    @JoinTable(name = "users_friends",
        joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "friend_id", referencedColumnName = "id"))
    private List<User> userFriends = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Estimate> estimates = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<CustomToDoListItem> customToDoListItems = new ArrayList<>();

    @ManyToMany(mappedBy = "usersLikedNews")
    private Set<EcoNews> ecoNewsLiked;

    @Builder.Default
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UserAchievement> userAchievements = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserAction> userActions = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Filter> filters = new ArrayList<>();

    @ManyToMany(mappedBy = "followers", fetch = FetchType.LAZY)
    private Set<EcoNews> favoriteEcoNews;

    @ManyToMany(mappedBy = "followers", fetch = FetchType.LAZY)
    private Set<Habit> favoriteHabits;

    @ManyToMany(mappedBy = "followers", fetch = FetchType.LAZY)
    private Set<Event> favoriteEvents;

    @ManyToMany(mappedBy = "attenders", fetch = FetchType.LAZY)
    private Set<Event> subscribedEvents;

    @ManyToMany(mappedBy = "requesters", fetch = FetchType.LAZY)
    private Set<Event> requestedEvents;
}
