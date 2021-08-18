package greencity.dto.habit;

import greencity.dto.user.UserShoppingListItemAdvanceDto;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@Setter
@Builder
public class UpdateUserShoppingListDto {
    private Long habitAssignId;
    private Long userShoppingListItemId;
    private List<UserShoppingListItemAdvanceDto> userShoppingListAdvanceDto;
}
