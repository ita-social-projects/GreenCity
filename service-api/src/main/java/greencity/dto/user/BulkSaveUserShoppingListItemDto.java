package greencity.dto.user;

import java.util.List;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class BulkSaveUserShoppingListItemDto {
    @Valid
    List<@Valid UserShoppingListItemDto> userShoppingListItems;
}
