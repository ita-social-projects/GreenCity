package greencity.dto.shoppinglistitem;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class NewShoppingListItemRequestDto {
    private Long habitAssignId;
    private String itemDescription;
}
