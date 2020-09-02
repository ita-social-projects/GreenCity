package greencity.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "tipsandtricks_comment")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"user"})
@EntityListeners(AuditingEntityListener.class)
public class TipsAndTricksComment {
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
    private TipsAndTricksComment parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = {CascadeType.ALL})
    private List<TipsAndTricksComment> comments = new ArrayList<>();

    @ManyToOne
    private User user;

    @ManyToOne
    private TipsAndTricks tipsAndTricks;

    @Column
    private boolean deleted;

    @Transient
    private boolean currentUserLiked = false;

    @ManyToMany
    @JoinTable(
        name = "tipsandtricks_comment_users_liked",
        joinColumns = @JoinColumn(name = "tipsandtricks_comment_id"),
        inverseJoinColumns = @JoinColumn(name = "users_liked_id"))
    private Set<User> usersLiked;
}
