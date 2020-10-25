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
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
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
