package greencity.dto.todolistitem;

import greencity.dto.language.LanguageDTO;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToDoListItemTranslationDTO {
    private Long id;

    private LanguageDTO language;

    private String content;
}
