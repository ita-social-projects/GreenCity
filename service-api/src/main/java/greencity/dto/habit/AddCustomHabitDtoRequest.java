package greencity.dto.habit;

import greencity.constant.ServiceValidationConstants;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
public class AddCustomHabitDtoRequest {
    @Min(value = 1, message = ServiceValidationConstants.HABIT_COMPLEXITY)
    @Max(value = 3, message = ServiceValidationConstants.HABIT_COMPLEXITY)
    @NotNull(message = ServiceValidationConstants.HABIT_COMPLEXITY)
    private Integer complexity;
    private Integer defaultDuration;
    private List<HabitTranslationDto> habitTranslations;
    private String image;
    private List<CustomShoppingListItemResponseDto> customShoppingListItemDto;
    private Set<String> tags;
}
