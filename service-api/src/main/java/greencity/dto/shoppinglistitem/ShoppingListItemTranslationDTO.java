package greencity.dto.shoppinglistitem;

import greencity.dto.language.LanguageVO;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppingListItemTranslationDTO {
    private Long id;

    private LanguageVO language;

    private String content;
}
