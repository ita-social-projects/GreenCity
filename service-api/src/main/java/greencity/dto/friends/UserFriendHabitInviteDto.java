package greencity.dto.friends;

import greencity.annotations.Sortable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Sortable
@SuperBuilder
public class UserFriendHabitInviteDto extends UserFriendDto {
    private Boolean hasInvitation;
    private Boolean hasAcceptedInvitation;
}
