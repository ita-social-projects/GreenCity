package greencity.dto.shoppinglistitem;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class ShoppingListItemDto {
    @NotNull
    @Min(value = 1)
    private Long id;

    @NotEmpty
    private String text;

    private String status;
}