package greencity.dto.habit;

import greencity.dto.user.UserShoppingListItemAdvanceDto;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
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
