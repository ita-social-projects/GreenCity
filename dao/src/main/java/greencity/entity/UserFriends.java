package greencity.entity;

import greencity.enums.FriendStatus;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(exclude = {"status", "createdDate"})
@Getter
@Setter
@Table(name = "users_friends")
@Entity(name = "UserFriends")
public class UserFriends {
    @EmbeddedId
    private UserFriendsId id;
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("friendId")
    private User friend;
    @Enumerated(value = EnumType.ORDINAL)
    @Column(nullable = false)
    private FriendStatus status;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    public UserFriends(User user, User friend, FriendStatus status, LocalDateTime createdDate) {
        this.user = user;
        this.friend = friend;
        this.status = status;
        this.createdDate = createdDate;
        this.id = new UserFriendsId(user.getId(), friend.getId());
    }

    public UserFriends() {
    }
}
