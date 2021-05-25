package greencity.dto.shoppinglistitem;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class ShoppingListItemViewDto {
    private String id;
    private String content;
}
