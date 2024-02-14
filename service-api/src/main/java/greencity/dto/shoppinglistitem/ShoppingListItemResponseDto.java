package greencity.dto.shoppinglistitem;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import java.util.List;

@EqualsAndHashCode
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppingListItemResponseDto {
    private Long id;

    private List<ShoppingListItemTranslationDTO> translations;
}
