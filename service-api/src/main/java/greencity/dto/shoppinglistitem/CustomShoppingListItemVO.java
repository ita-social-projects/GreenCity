package greencity.dto.shoppinglistitem;

import greencity.dto.user.UserShoppingListItemVO;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CustomShoppingListItemVO {
    private Long id;

    private String text;

    @Builder.Default
    private List<UserShoppingListItemVO> userShoppingListItems = new ArrayList<>();
}
