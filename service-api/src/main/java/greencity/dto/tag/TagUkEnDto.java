package greencity.dto.tag;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TagUkEnDto {
    private Long id;
    private String nameUk;
    private String nameEn;
}
