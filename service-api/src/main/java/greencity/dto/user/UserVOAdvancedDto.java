package greencity.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import greencity.dto.achievement.UserAchievementVO;
import greencity.dto.socialnetwork.SocialNetworkVO;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserVOAdvancedDto extends UserVO {
    private String firstName;

    private LocalDateTime dateOfRegistration;

    private List<SocialNetworkVO> socialNetworks;

    private List<UserAchievementVO> userAchievements = new ArrayList<>();

    private List<UserVO> userFriends = new ArrayList<>();

    private Double rating;
}
