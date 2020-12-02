package greencity.dto.useraction;

import greencity.dto.user.UserVO;
import lombok.*;

@EqualsAndHashCode
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserActionVO {
    private Long id;

    private UserVO user;

    private Integer ecoNewsLikes;

    private Integer ecoNews;

    private Integer ecoNewsComments;

    private Integer tipsAndTricksLikes;

    private Integer tipsAndTricksComments;

    private Integer acquiredHabit;

    private Integer habitStreak;

    private Integer socialNetworks;

    private Double rating;

    private Integer achievements;
}
