package greencity.entity.event;

import greencity.entity.User;
import greencity.enums.EventCommentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Table;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Transient;
import javax.persistence.CascadeType;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "events_comment")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class EventComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Size(min = 1, max = 8000)
    private String text;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(nullable = false)
    private boolean deleted;

    @ManyToOne
    private User user;

    @ManyToOne
    private Event event;

    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    private EventComment parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<EventComment> comments = new ArrayList<>();

    @Transient
    private boolean currentUserLiked = false;

    @ManyToMany
    @JoinTable(
        name = "events_comment_users_likes",
        joinColumns = @JoinColumn(name = "event_comment_id"),
        inverseJoinColumns = @JoinColumn(name = "users_id"))
    private Set<User> usersLiked;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EventCommentStatus status;
}
