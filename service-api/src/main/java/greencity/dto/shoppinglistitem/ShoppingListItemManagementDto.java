package greencity.dto.shoppinglistitem;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class ShoppingListItemManagementDto {
    @NotNull
    private Long id;

    private List<ShoppingListItemTranslationVO> translations;
}
