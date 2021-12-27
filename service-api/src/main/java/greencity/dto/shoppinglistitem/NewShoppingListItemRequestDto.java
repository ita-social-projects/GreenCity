package greencity.dto.shoppinglistitem;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class NewShoppingListItemRequestDto {
    private Long habitAssignId;
    private String itemDescription;
}
