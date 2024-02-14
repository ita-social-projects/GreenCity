package greencity.entity;

import greencity.enums.CommentStatus;
import lombok.Getter;
import lombok.Builder;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "econews_comment")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@EntityListeners(AuditingEntityListener.class)
public class EcoNewsComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Size(min = 1, max = 8000)
    private String text;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modifiedDate;

    @ManyToOne
    private EcoNewsComment parentComment;

    @Builder.Default
    @OneToMany(mappedBy = "parentComment", cascade = {CascadeType.ALL})
    private List<EcoNewsComment> comments = new ArrayList<>();

    @ManyToOne
    private User user;

    @ManyToOne
    private EcoNews ecoNews;

    @Transient
    @Builder.Default
    private boolean currentUserLiked = false;

    @ManyToMany
    @JoinTable(
        name = "econews_comment_users_liked",
        joinColumns = @JoinColumn(name = "econews_comment_id"),
        inverseJoinColumns = @JoinColumn(name = "users_liked_id"))
    private Set<User> usersLiked;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CommentStatus status;
}
