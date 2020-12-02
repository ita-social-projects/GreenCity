package greencity.entity;

import lombok.*;

import javax.persistence.*;

@Entity

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "user_actions")
@Builder
public class UserAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(name = "eco_news_likes")
    private Integer ecoNewsLikes;

    @Column(name = "published_eco_news")
    private Integer ecoNews;

    @Column(name = "eco_news_comment")
    private Integer ecoNewsComments;

    @Column(name = "tips_and_tricks_likes")
    private Integer tipsAndTricksLikes;

    @Column(name = "tips_and_tricks_comment")
    private Integer tipsAndTricksComments;

    @Column(name = "acquired_habit")
    private Integer acquiredHabit;

    @Column(name = "habit_streak")
    private Integer habitStreak;

    @Column(name = "social_networks")
    private Integer socialNetworks;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "achievements")
    private Integer achievements;
}
