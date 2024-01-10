package greencity.dto.user;

import greencity.enums.ShoppingListItemStatus;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@Setter
@Builder
public class UserShoppingListItemAdvanceDto {
    private Long id;
    private Long shoppingListItemId;
    private ShoppingListItemStatus status;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dateCompleted;
    private String content;

    /**
     * Method returns status in Boolean.
     */
    public Boolean getBoolStatus() {
        return status.equals(ShoppingListItemStatus.INPROGRESS);
    }
}
