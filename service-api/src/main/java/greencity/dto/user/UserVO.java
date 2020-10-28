package greencity.dto.user;

import greencity.dto.econews.EcoNewsVO;
import greencity.dto.econewscomment.EcoNewsCommentVO;
import greencity.dto.goal.CustomGoalVO;
import greencity.dto.habit.HabitAssignVO;
import greencity.dto.ownsecurity.OwnSecurityVO;
import greencity.dto.place.PlaceVO;
import greencity.dto.ratingstatistics.RatingStatisticsVO;
import greencity.dto.socialnetwork.SocialNetworkVO;
import greencity.dto.tipsandtricks.TipsAndTricksVO;
import greencity.dto.verifyemail.VerifyEmailVO;
import greencity.enums.EmailNotification;
import greencity.enums.ROLE;
import greencity.enums.UserStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(
    exclude = {"lastVisit", "places", "comments", "verifyEmail", "addedPlaces", "favoritePlaces", "ownSecurity",
        "refreshTokenKey", "verifyEmail", "estimates", "restorePasswordEmail", "addedEcoNews",
        "addedTipsAndTricks", "userGoals", "customGoals"})
@ToString(
    exclude = {"places", "comments", "verifyEmail", "addedPlaces", "favoritePlaces", "ownSecurity",
        "refreshTokenKey", "customGoals",
        "verifyEmail", "estimates", "restorePasswordEmail", "addedEcoNews", "addedTipsAndTricks", "userGoals"})
public class UserVO {
    private Long id;

    private String name;

    private String email;

    private ROLE role;

    private String userCredo;

    private UserStatus userStatus;

    private LocalDateTime lastVisit;

    private List<UserGoalVO> userGoals = new ArrayList<>();

    private List<CustomGoalVO> customGoals = new ArrayList<>();

    private VerifyEmailVO verifyEmail;

    private Double rating;

    private EmailNotification emailNotification;

    private LocalDateTime dateOfRegistration;

    private List<SocialNetworkVO> socialNetworks;

    private List<UserVO> userFriends = new ArrayList<>();

    private String refreshTokenKey;

    private OwnSecurityVO ownSecurity;

    private List<PlaceVO> places = new ArrayList<>();

    private List<EcoNewsCommentVO> ecoNewsComments = new ArrayList<>();

    private List<PlaceVO> addedPlaces = new ArrayList<>();

    private List<EcoNewsVO> addedEcoNews = new ArrayList<>();

    private List<TipsAndTricksVO> addedTipsAndTricks = new ArrayList<>();

    private String profilePicturePath;

    private Set<EcoNewsCommentVO> ecoNewsCommentsLiked;

    private String firstName;

    private String city;

    private Boolean showLocation;

    private Boolean showEcoPlace;

    private Boolean showShoppingList;

    private LocalDateTime lastActivityTime;

    private List<HabitAssignVO> habitAssigns = new ArrayList<>();

    private List<RatingStatisticsVO> ratingStatistics = new ArrayList<>();
}
