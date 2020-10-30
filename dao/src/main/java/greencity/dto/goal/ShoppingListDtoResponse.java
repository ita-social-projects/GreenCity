package greencity.dto.goal;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class ShoppingListDtoResponse {
    private String status;
    private String text;
    private Long goalId;
    private Long customGoalId;
}