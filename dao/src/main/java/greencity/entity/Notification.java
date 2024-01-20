package greencity.entity;

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
@Table(name = "notifications")
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

    @ManyToMany
    @JoinTable(
        name = "notifications_users",
        joinColumns = @JoinColumn(name = "notification_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> actionUsers = new ArrayList<>();

    private String customMessage;

    private Long targetId;

    @Enumerated(value = EnumType.STRING)
    private NotificationType notificationType;

    @Enumerated(value = EnumType.STRING)
    private ProjectName projectName;

    @Column
    private boolean viewed;

    @Column
    private LocalDateTime time;
}
