package greencity.dto.shoppinglistitem;

import lombok.*;

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
