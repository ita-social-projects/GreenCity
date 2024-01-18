package greencity.entity;

import greencity.entity.event.Event;
import greencity.entity.event.EventComment;
import greencity.enums.NotificationType;
import greencity.enums.ProjectName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "notification")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Getter
@Setter
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User targetUser;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "notification_user",
        joinColumns = @JoinColumn(name = "notification_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> actionUsers = new ArrayList<>();

    private String customMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    private EcoNewsComment ecoNewsComment;
    @ManyToOne(fetch = FetchType.LAZY)
    private EcoNews ecoNews;

    @ManyToOne(fetch = FetchType.LAZY)
    private EventComment eventComment;
    @ManyToOne(fetch = FetchType.LAZY)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserAchievement achievement;

    @Enumerated(value = EnumType.STRING)
    private NotificationType notificationType;

    @Enumerated(value = EnumType.STRING)
    private ProjectName projectName;

    @Column
    private boolean viewed;

    @Column
    private LocalDateTime time;
}
