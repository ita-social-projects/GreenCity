package greencity.dto.shoppinglistitem;

import greencity.dto.language.LanguageVO;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppingListItemTranslationDTO {
    private Long id;

    private LanguageVO language;

    private String content;
}
