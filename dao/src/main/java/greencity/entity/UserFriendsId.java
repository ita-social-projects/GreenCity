package greencity.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
public class UserFriendsId implements Serializable {
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "friend_id")
    private Long friendId;
}
